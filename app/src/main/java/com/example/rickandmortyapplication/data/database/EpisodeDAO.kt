import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes")
    fun getAllEpisodes(): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE id = :id")
    fun getEpisodeById(id: Int): Flow<EpisodeEntity?>

    @Query("SELECT * FROM episodes WHERE name LIKE '%' || :query || '%'")
    fun getFilteredEpisodes(query: String): Flow<List<EpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)
}