package com.example.dreamflashcardsapp.fragments.app

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcardsapp.R
import com.example.dreamflashcardsapp.adapters.SetsAdapter
import com.example.dreamflashcardsapp.databinding.FragmentSetsBinding
import com.example.dreamflashcardsapp.model.FlashcardsSet
import com.example.dreamflashcardsapp.viewmodels.ModifySetViewModel
import com.example.dreamflashcardsapp.viewmodels.SetsViewModel

class SetsFragment : Fragment() {

    private  var _binding: FragmentSetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var alertDialog: AlertDialog

    private var setToDelete: FlashcardsSet = FlashcardsSet("", "", "", "", "", "", "")

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

        /** configure AlertDialog */
        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete set")
            .setMessage("Are you sure you want to delete this set?")
            .setIcon(R.drawable.ic_alert_dialog_warning)
            .setPositiveButton("Yes") { dialog, which ->

                setsViewModel.deleteSetFlashcardsFromFirestore(setToDelete.setID)
                setsViewModel.deleteSet(setToDelete)
                Toast.makeText(requireContext(), "Set deleted...", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("No"){ dialog, which -> }
            .create()

        binding.addFloatingActionButton.setOnClickListener {
            val action = SetsFragmentDirections.actionSetsFragmentToCreateSetFragment()
            findNavController().navigate(action)
        }

        val filters = listOf("All", "Learned", "Unlearned")
        val dropdownMenuAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_filter_item, filters)
        (binding.filterDropdownMenu.editText as? AutoCompleteTextView)?.setAdapter(dropdownMenuAdapter)
        binding.filterDropdownMenuText.setText(filters[0], false)

        recyclerView = binding.setsRecyclerview

        val adapter = SetsAdapter(requireContext()) { flashcardsSet, option ->
            performActionOnSet(flashcardsSet, option)
        }

        recyclerView.adapter = adapter
        setsViewModel.sets.observe(this.viewLifecycleOwner){ sets ->
            Log.d(TAG, "Adapting sets list...")
            sets.let {
                if(!setsViewModel.sets.value.isNullOrEmpty()){

                    Log.d(TAG, "Updating to ${setsViewModel.sets.value!!.size}")
                    adapter.submitList(setsViewModel.sets.value!!)

                    binding.setsRecyclerview.visibility = View.GONE
                    binding.setsRecyclerview.visibility = View.VISIBLE
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun performActionOnSet(flashcardsSet: FlashcardsSet, option: String) {

        if(option == "Delete"){
            setToDelete = flashcardsSet
            alertDialog.show()
        } else if(option == "Modify"){
            modifySetViewModel.getFlashcardsToModify(flashcardsSet)
            val action = SetsFragmentDirections.actionSetsFragmentToFlashcardsFragment()
            findNavController().navigate(action)
        }

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