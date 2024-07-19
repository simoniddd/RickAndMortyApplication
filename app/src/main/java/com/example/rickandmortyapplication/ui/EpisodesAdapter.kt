import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity

class EpisodesAdapter : ListAdapter<EpisodeEntity, EpisodesAdapter.EpisodeViewHolder>(EpisodeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = getItem(position)
        holder.bind(episode)
    }

    class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val airDateTextView: TextView = itemView.findViewById(R.id.airDateTextView)
        private val urlTextView: TextView = itemView.findViewById(R.id.urlTextView)

        fun bind(episode: EpisodeEntity) {
            nameTextView.text = episode.name
            airDateTextView.text = episode.airdate
            urlTextView.text = episode.url
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
