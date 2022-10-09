package com.example.dreamflashcardsapp.fragments.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcardsapp.adapters.FlashcardsAdapter
import com.example.dreamflashcardsapp.databinding.FragmentFlashcardsBinding
import com.example.dreamflashcardsapp.viewmodels.ModifySetViewModel

class FlashcardsFragment : Fragment() {

    private  var _binding: FragmentFlashcardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private val modifySetViewModel: ModifySetViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFlashcardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFlashcardButton.setOnClickListener {
            val action = FlashcardsFragmentDirections.actionFlashcardsFragmentToAddFlashcardFragment()
            findNavController().navigate(action)
        }

        recyclerView = binding.flashcardsRecyclerview

        val adapter = FlashcardsAdapter(requireContext()) { flashcard, option ->
            when (option) {
                "Modify" -> {
                    Toast.makeText(requireContext(), "Modifying flashcard", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        recyclerView.adapter = adapter

        modifySetViewModel.modifyFlashcards.observe(this.viewLifecycleOwner){
            if(!modifySetViewModel.modifyFlashcards.value.isNullOrEmpty()){
                adapter.submitList(modifySetViewModel.modifyFlashcards.value!!)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}