package com.example.dreamflashcardsapp.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dreamflashcardsapp.R
import com.example.dreamflashcardsapp.databinding.FragmentCreateSetBinding
import com.example.dreamflashcardsapp.viewmodels.ModifySetViewModel

class CreateSetFragment : Fragment() {

    // view binding
    private lateinit var _binding: FragmentCreateSetBinding
    private val binding get() = _binding!!

    // ModifySetFragment
    private val modifySetViewModel: ModifySetViewModel by activityViewModels()

    var setName = ""

    companion object {
        private const val TAG = "CreateSetFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createSetButton.setOnClickListener {
            createSet()
        }

        val colors = listOf("Purple", "Blue", "Yellow", "Orange", "Red")
        binding.colorIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.set_option_purple))
        modifySetViewModel.setColor("Purple")
        val dropdownMenuAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_color_item, colors)
        (binding.colorDropdownMenu.editText as? AutoCompleteTextView)?.setAdapter(dropdownMenuAdapter)

        binding.colorDropdownMenuText.setText(colors[0], false)

        binding.colorDropdownMenuText.setOnItemClickListener { adapterView, view, i, l ->

            val selected = binding.colorDropdownMenuText.text.toString()

            Log.d(TAG, "Color option selected: $selected")

            modifySetViewModel.setColor(selected)

            var colorReference = ContextCompat.getColor(requireContext(), R.color.set_option_red)
            when(selected){

                "Red" -> { colorReference = ContextCompat.getColor(requireContext(), R.color.set_option_red) }
                "Orange" -> { colorReference = ContextCompat.getColor(requireContext(), R.color.set_option_orange) }
                "Yellow" -> { colorReference = ContextCompat.getColor(requireContext(), R.color.set_option_yellow) }
                "Blue" -> { colorReference = ContextCompat.getColor(requireContext(), R.color.set_option_blue) }
                "Purple" -> { colorReference = ContextCompat.getColor(requireContext(), R.color.set_option_purple) }
            }

            binding.colorIcon.setColorFilter(colorReference)

        }

    }

    /** create new set function */
    private fun createSet(){

        setName = binding.setNameInputLayout.editText?.text.toString()
        hideErrors()

        if(setName.isNullOrEmpty()){

            Log.e(TAG, "Empty or wrong name of the set...")
            binding.setNameInputLayout.error = "Please enter the name of your set"

        } else if(setName.length < 3) {

            Log.e(TAG, "Length of the name of the set is less than 3")
            binding.setNameInputLayout.error = "Please enter at least 3 letters"

        } else {

            Log.d(TAG, "Create set in Firestore")
            Toast.makeText(requireContext(), "Creating new set!\nIt might take a while...", Toast.LENGTH_SHORT).show()
            modifySetViewModel.setSetName(setName)
            modifySetViewModel.addSetToFirestore()

            modifySetViewModel.setCreated.observe(this.viewLifecycleOwner) {
                if(modifySetViewModel.setCreated.value == true){

                    Log.d(TAG, "Going to the next screen")
                    val action = CreateSetFragmentDirections.actionCreateSetFragmentToFlashcardsFragment()
                    findNavController().navigate(action)

                }
            }

        }

    }

    /** hide all errors function */
    private fun hideErrors() {
        binding.setNameInputLayout.isErrorEnabled = false
    }

}