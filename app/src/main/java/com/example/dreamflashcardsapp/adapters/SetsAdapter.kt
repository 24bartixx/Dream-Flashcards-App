package com.example.dreamflashcardsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcardsapp.R
import com.example.dreamflashcardsapp.databinding.ItemSetBinding
import com.example.dreamflashcardsapp.model.FlashcardsSet


class SetsAdapter(private val context: Context, private val optionsFunction: (FlashcardsSet, String) -> (Unit))
    : ListAdapter<FlashcardsSet, SetsAdapter.SetsViewHolder>(DiffCallback) {

    class SetsViewHolder(var binding: ItemSetBinding): RecyclerView.ViewHolder(binding.root){

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

        holder.binding.setsOptions.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.binding.setsOptions)
            popupMenu.inflate(R.menu.set_card_options_menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.modify_set -> {
                        optionsFunction(set, "Modify")
                        true
                    }
                    R.id.delete_set -> {
                        optionsFunction(set, "Delete")
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            popupMenu.show()
        }

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