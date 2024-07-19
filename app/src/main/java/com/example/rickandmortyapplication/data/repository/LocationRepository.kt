
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.network.ApiService
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocationRepository(private val apiService: ApiService, private val locationDao: LocationDao) {

    // Функция для получения эпизодов с API и сохранения их в базе данных
    suspend fun refreshLocations(page: Int) {
        withContext(Dispatchers.IO) {
            val response = api.getAllLocations(page)
            val locations = response.results.map {
                LocationEntity(it.id, it.name, it.type, it.dimension, it.url)
            }
            locationDao.insertLocations(locations)
        }
    }

    fun getAllLocations(): Flow<List<LocationEntity>> {
        return locationDao.getAllLocations()
    }
}
