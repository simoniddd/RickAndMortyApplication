import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.ui.EpisodeViewModel
import com.example.rickandmortyapplication.R
import kotlinx.coroutines.launch


class EpisodesActivity : AppCompatActivity() {
    private val viewModel: EpisodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = EpisodeAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 столбца

        lifecycleScope.launch {
            viewModel.allEpisodes.collect { episodes ->
                adapter.submitList(episodes)
            }
        }

        // Загрузить данные при запуске
        viewModel.refreshEpisodes(1)
    }
}
