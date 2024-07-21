package com.example.rickandmortyapplication.ui.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.data.database.entities.LocationEntity


class LocationsAdapter : ListAdapter<LocationEntity, LocationsAdapter.LocationViewHolder>(
    LocationDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        private val dimensionTextView: TextView = itemView.findViewById(R.id.dimensionTextView)

        fun bind(location: LocationEntity) {
            nameTextView.text = location.name
            typeTextView.text = location.type
            dimensionTextView.text = location.dimension
        }
    }

    class LocationDiffCallback : DiffUtil.ItemCallback<LocationEntity>() {
        override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem == newItem
        }
    }
}
