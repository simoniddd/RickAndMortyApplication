
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.ApiService
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EpisodeRepository(private val apiService: ApiService, private val episodeDao: EpisodeDao) {

    // Функция для получения эпизодов с API и сохранения их в базе данных
        suspend fun refreshEpisodes(page: Int) {
            withContext(Dispatchers.IO) {
                val response = api.getAllEpisodes(page)
                val episodes = response.results.map {
                    EpisodeEntity(it.id, it.name, it.airdate, it.url)
                }
                episodeDao.insertEpisodes(episodes)
            }
        }

        fun getAllEpisodes(): Flow<List<EpisodeEntity>> {
            return episodeDao.getAllEpisodes()
        }
    }
