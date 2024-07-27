package com.example.rickandmortyapplication.ui.character

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity

class CharacterAdapter : ListAdapter<CharacterEntity, CharacterAdapter.CharacterViewHolder>(
    CharacterDiffCallback()
) {

    private var onItemClickListener: ((CharacterEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (CharacterEntity) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view, this, onItemClickListener)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    class CharacterViewHolder(
        itemView: View,
        private val adapter: CharacterAdapter,
        private val onItemClickListener: ((CharacterEntity) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.characterName)
        private val speciesTextView: TextView = itemView.findViewById(R.id.characterSpecies)
        private val statusTextView: TextView = itemView.findViewById(R.id.characterStatus)
        private val genderTextView: TextView = itemView.findViewById(R.id.characterGender)
        private val imageView: ImageView = itemView.findViewById(R.id.characterImage)

        fun bind(character: CharacterEntity) {
            nameTextView.text = character.name
            speciesTextView.text = character.species
            statusTextView.text = character.status
            genderTextView.text = character.gender
            Glide.with(itemView.context)
                .load(character.image)
                .placeholder(R.drawable.placeholder_image) // Add a placeholder image if needed
                .error(R.drawable.error_image) // Add an error image if needed
                .into(imageView)
        }

        init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val character = adapter.getItem(position)
                        if (character != null) {
                            onItemClickListener?.invoke(character)
                    }
                }
            }
        }
    }
}

class CharacterDiffCallback : DiffUtil.ItemCallback<CharacterEntity>() {
    override fun areItemsTheSame(oldItem: CharacterEntity, newItem: CharacterEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CharacterEntity, newItem: CharacterEntity): Boolean {
        return oldItem == newItem
    }
}


