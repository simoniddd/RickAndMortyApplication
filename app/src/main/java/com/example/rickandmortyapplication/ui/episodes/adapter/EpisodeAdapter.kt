package com.example.rickandmortyapplication.ui.episodes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity

class EpisodeAdapter : ListAdapter<EpisodeEntity, EpisodeAdapter.EpisodeViewHolder>(
    EpisodeDiffCallback()
) {

    private var onItemClickListener: ((EpisodeEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (EpisodeEntity) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view, onItemClickListener, this::getItem)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = getItem(position)
        holder.bind(episode)
    }

    class EpisodeViewHolder(
        itemView: View,
        private val onItemClickListener: ((EpisodeEntity) -> Unit)?,
        private val getItem: (Int) -> EpisodeEntity?
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val airDateTextView: TextView = itemView.findViewById(R.id.airDateTextView)
        private val episodeTextView: TextView = itemView.findViewById(R.id.episodeTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { onItemClickListener?.invoke(it) }
                }
            }
        }

        fun bind(episode: EpisodeEntity) {
            nameTextView.text = episode.name
            airDateTextView.text = episode.air_date
            episodeTextView.text = episode.episode
        }
    }

    class EpisodeDiffCallback : DiffUtil.ItemCallback<EpisodeEntity>() {
        override fun areItemsTheSame(oldItem: EpisodeEntity, newItem: EpisodeEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EpisodeEntity, newItem: EpisodeEntity): Boolean {
            return oldItem == newItem
        }
    }
}

