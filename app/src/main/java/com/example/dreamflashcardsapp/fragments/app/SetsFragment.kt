package com.example.dreamflashcardsapp.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcardsapp.R
import com.example.dreamflashcardsapp.adapters.SetsAdapter
import com.example.dreamflashcardsapp.databinding.FragmentSetsBinding
import com.example.dreamflashcardsapp.viewmodels.ModifySetViewModel
import com.example.dreamflashcardsapp.viewmodels.SetsViewModel

class SetsFragment : Fragment() {

    private  var _binding: FragmentSetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private val setsViewModel: SetsViewModel by activityViewModels()
    private val modifySetViewModel: ModifySetViewModel by activityViewModels()

    companion object{
        private const val TAG = "SetsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setsViewModel.getSetsFromFirestore()

        binding.addFloatingActionButton.setOnClickListener {
            val action = SetsFragmentDirections.actionSetsFragmentToCreateSetFragment()
            findNavController().navigate(action)
        }

        val filters = listOf("All", "Learned", "Unlearned")
        val dropdownMenuAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_filter_item, filters)
        (binding.filterDropdownMenu.editText as? AutoCompleteTextView)?.setAdapter(dropdownMenuAdapter)
        binding.filterDropdownMenuText.setText(filters[0], false)

        recyclerView = binding.setsRecyclerview

        val adapter = SetsAdapter( object : SetsAdapter.OptionsMenuClickListener {

            override fun onOptionsMenuClicked(position: Int) {
                performOptionsMenuClick(position)
            }

        })

        setsViewModel.sets.observe(this.viewLifecycleOwner){
            if(!setsViewModel.sets.value.isNullOrEmpty()){
                adapter.submitList(setsViewModel.sets.value)
                Log.d(TAG, binding.setsRecyclerview.size.toString())
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    /** click handler for sets options */
    private fun performOptionsMenuClick(position: Int){

        Log.d(TAG, binding.setsRecyclerview.size.toString())

        val popupMenu = PopupMenu(requireContext(), binding.setsRecyclerview[position%5])

        popupMenu.inflate(R.menu.set_card_options_menu)

        popupMenu.setOnMenuItemClickListener( object : PopupMenu.OnMenuItemClickListener {

            override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                when(menuItem?.itemId){

                    R.id.modify_set -> {
                        Toast.makeText(requireContext(), "Modify set with name: ${setsViewModel.sets.value!![position].setName}", Toast.LENGTH_SHORT).show()
                        return true
                    }

                    R.id.delete_set -> {
                        Toast.makeText(requireContext(), "Delete set with name: ${setsViewModel.sets.value!![position].setName}", Toast.LENGTH_SHORT).show()
                        return true
                    }

                }

                return false
            }

        })

        popupMenu.show()

    }

    override fun onResume(){
        super.onResume()
        if(modifySetViewModel.setCreated.value!!){
            Toast.makeText(requireContext(), "New set added to the end of the list", Toast.LENGTH_SHORT).show()
            modifySetViewModel.setSetCreated(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}