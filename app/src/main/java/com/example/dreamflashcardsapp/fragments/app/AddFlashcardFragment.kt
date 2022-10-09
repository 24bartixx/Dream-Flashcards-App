package com.example.dreamflashcardsapp.fragments.app

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dreamflashcardsapp.databinding.FragmentAddFlashcardBinding
import com.example.dreamflashcardsapp.viewmodels.ModifySetViewModel

class AddFlashcardFragment : Fragment() {

    private var _binding: FragmentAddFlashcardBinding? = null
    private val binding get() = _binding!!

    private val modifySetViewModel: ModifySetViewModel by activityViewModels()

    companion object {
        private const val TAG = "AddFlashcardFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding =  FragmentAddFlashcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFlashcardButton.setOnClickListener {
            addFlashcard()
        }

    }

    private fun addFlashcard(){

        val term = binding.termInputLayout.editText?.text.toString()
        val definition = binding.definitionInputLayout.editText?.text.toString()

        if(term.isNullOrEmpty()){
            Log.e(TAG, "Empty term")
            binding.termInputLayout.error = "Please insert a term"
        } else if(definition.isNullOrEmpty()) {
            Log.e(TAG, "Empty definition")
            binding.definitionInputLayout.error = "Please insert a definition"
        } else {
            modifySetViewModel.addFlashcardToFirestore(term, definition)
            binding.termInputLayout.editText?.text = SpannableStringBuilder("")
            binding.definitionInputLayout.editText?.text = SpannableStringBuilder("")
            Toast.makeText(requireContext(), "New flashcards added!", Toast.LENGTH_SHORT).show()
        }

    }

}