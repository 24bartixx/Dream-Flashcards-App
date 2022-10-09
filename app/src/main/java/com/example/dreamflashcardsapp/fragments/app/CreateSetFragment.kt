package com.example.dreamflashcardsapp.fragments.app

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dreamflashcardsapp.databinding.FragmentCreateSetBinding
import com.example.dreamflashcardsapp.viewmodels.ModifySetViewModel
import com.example.dreamflashcardsapp.viewmodels.SetsViewModel

class CreateSetFragment : Fragment() {

    // view binding
    private lateinit var _binding: FragmentCreateSetBinding
    private val binding get() = _binding!!

    // ModifySetViewModel
    private val modifySetViewModel: ModifySetViewModel by activityViewModels()

    // SetsViewModel
    private val setsViewModel: SetsViewModel by activityViewModels()

    // ProgressDialog
    private lateinit var progressDialog: ProgressDialog

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

        // configure the ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Preparing the set")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.createSetButton.setOnClickListener {
            createSet()
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
            modifySetViewModel.setSetName(setName)
            modifySetViewModel.addSetToFirestore()

            modifySetViewModel.setCreated.observe(this.viewLifecycleOwner) {
                if(modifySetViewModel.setCreated.value == true){

                    setsViewModel.addCreatedSet(modifySetViewModel.modifySet.value!!)
                    
                    progressDialog.hide()
                    Log.d(TAG, "Going to the next screen")
                    val action = CreateSetFragmentDirections.actionCreateSetFragmentToFlashcardsFragment()
                    findNavController().navigate(action)

                } else {
                    hideEverything()
                    progressDialog.show()
                }
            }

        }

    }

    /** hide all errors function */
    private fun hideErrors() {
        binding.setNameInputLayout.isErrorEnabled = false
    }

    /** hide everything when the progress dialog in on the screen function */
    private fun hideEverything(){
        binding.setNameInputLayout.visibility = View.INVISIBLE
        binding.createSetButton.visibility = View.INVISIBLE
        binding.createSetText.visibility = View.INVISIBLE
    }

}