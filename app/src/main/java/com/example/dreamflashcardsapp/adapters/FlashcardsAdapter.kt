package com.example.dreamflashcardsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcardsapp.R
import com.example.dreamflashcardsapp.databinding.ItemFlashcardBinding
import com.example.dreamflashcardsapp.model.Flashcard

class FlashcardsAdapter(private val context: Context, private val optionsFunctions: (Flashcard, String) -> (Unit))
    : ListAdapter<Flashcard, FlashcardsAdapter.FlashcardViewHolder>(DiffCallback) {

    class FlashcardViewHolder(var binding: ItemFlashcardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(flashcard: Flashcard){

            binding.term.text = flashcard.term
            binding.definition.text = flashcard.definition

            if(flashcard.learned == "yes"){
                binding.rightIcon.setImageResource(R.drawable.ic_learned)
            } else {
                binding.rightIcon.setImageResource(R.drawable.ic_unlearned)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        return FlashcardViewHolder(ItemFlashcardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {

        val flashcard = getItem(position)
        holder.bind(flashcard)

        holder.binding.flashcardsOptions.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.binding.flashcardsOptions)
            popupMenu.inflate(R.menu.flashcard_card_options_menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when(menuItem.itemId){
                    R.id.modify_flashcard -> {
                        optionsFunctions(flashcard, "Modify")
                        true
                    }
                    R.id.delete_flashcard -> {
                        optionsFunctions(flashcard, "Delete")
                        true
                    }
                    else -> { false }
                }

            }

            popupMenu.show()
        }

    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Flashcard>() {

            override fun areItemsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
                return oldItem.flashcardOrder == newItem.flashcardOrder
            }

            override fun areContentsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
                return oldItem == newItem
            }

        }
    }

}