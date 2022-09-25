package com.example.dreamflashcardsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcardsapp.databinding.ItemSetBinding
import com.example.dreamflashcardsapp.model.FlashcardsSet


class SetsAdapter(): ListAdapter<FlashcardsSet, SetsAdapter.SetsViewHolder>(DiffCallback) {

    class SetsViewHolder(private var binding: ItemSetBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(set: FlashcardsSet){

            binding.setName.text = set.setName
            binding.wordsCount.text = set.wordsCount

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsViewHolder {
        return SetsViewHolder(ItemSetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SetsViewHolder, position: Int) {

        val set = getItem(position)
        holder.bind(set)

    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FlashcardsSet>() {

            override fun areItemsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return newItem.setID == oldItem.setID
            }

        }
    }

}