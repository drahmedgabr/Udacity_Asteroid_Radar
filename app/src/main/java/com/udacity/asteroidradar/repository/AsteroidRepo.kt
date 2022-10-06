package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.main.DailyImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepo(val database: AsteroidDatabaseDao) {

    private val _asteroids = MutableLiveData<List<Asteroid>>()

    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    val offlineList = database.getAllData(TaskDates.todayDate())

    private val _todayList = MutableLiveData<List<Asteroid>>()

    val todayList: LiveData<List<Asteroid>>
        get() = _todayList

    val dailyImage = MutableLiveData<DailyImage>()


    suspend fun refreshData() {
        val dataString = AsteroidApi.asteroidApiService.getAsteroidData(
            TaskDates.todayDate(),
            TaskDates.endDate(),
            Constants.API_KEY
        )
        val jsonObject = JSONObject(dataString)
        val data: List<Asteroid> = parseAsteroidsJsonResult(jsonObject)
        _asteroids.value = data
        withContext(Dispatchers.IO) {
            database.insert(data)
        }
    }

    suspend fun getTodayItems() {
        val todayImage = AsteroidApi.asteroidApiService.getDailyImage(Constants.API_KEY)
        dailyImage.value = todayImage
        _todayList.value = asteroids.value?.filter {
            it.closeApproachDate == TaskDates.todayDate()
        }
    }

    suspend fun deleteOdlData() {
        database.deleteOldData(TaskDates.todayDate())
    }

}

class TaskDates {
    companion object {
        val formatter = SimpleDateFormat("yyyy-MM-dd")

        fun todayDate(): String {
            val start = Calendar.getInstance().time
            return formatter.format(start)
        }

        fun endDate(): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val end = calendar.time
            return formatter.format(end)
        }
    }
}