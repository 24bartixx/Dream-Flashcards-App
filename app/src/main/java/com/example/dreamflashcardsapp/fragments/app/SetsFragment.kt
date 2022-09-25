package com.example.dreamflashcardsapp.fragments.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dreamflashcardsapp.R
import com.example.dreamflashcardsapp.adapters.SetsAdapter
import com.example.dreamflashcardsapp.databinding.FragmentSetsBinding
import com.example.dreamflashcardsapp.model.FlashcardsSet

class SetsFragment : Fragment() {

    private lateinit var _binding: FragmentSetsBinding
    private val binding get() = _binding!!

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

        binding.addFloatingActionButton.setOnClickListener {
            val action = SetsFragmentDirections.actionSetsFragmentToCreateSetFragment()
            findNavController().navigate(action)
        }

        val filters = listOf("All", "Learned", "Unlearned")
        val dropdownMenuAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_filter_item, filters)
        (binding.filterDropdownMenu.editText as? AutoCompleteTextView)?.setAdapter(dropdownMenuAdapter)
        binding.filterDropdownMenuText.setText(filters[0], false)

        val sets = listOf(
            FlashcardsSet("Some ID", "Siuuu", "Top G", "7", "4", "9", "Purple"),
            FlashcardsSet("Some ID 2", "Matusz", "Sponge Bob", "57", "2", "106", "Orange"),
            FlashcardsSet("Some ID 3", "Neffex", "Fuck u", "107", "60", "222", "Yellow"),
            FlashcardsSet("Some ID 4", "Top G", "Never give up", "99", "99", "106", "Blue"),
            FlashcardsSet("Some ID 5", "Mama", "Milosc", "105", "0", "106", "Red"),
            FlashcardsSet("Some ID 5", "Mama", "Milosc", "105", "0", "106", "Red")
        )

        val recyclerView = binding.setsRecyclerview

        val adapter = SetsAdapter()
        adapter.submitList(sets)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

}