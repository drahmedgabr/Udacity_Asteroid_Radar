package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.main.DailyImage
import com.udacity.asteroidradar.main.TaskDates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepo(val database: AsteroidDatabaseDao) {

    private val _asteroids = MutableLiveData<List<Asteroid>>()

    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    val offlineList = database.getAllData(TaskDates.startDate())

    private val _todayList = MutableLiveData<List<Asteroid>>()

    val todayList: LiveData<List<Asteroid>>
        get() = _todayList

    val dailyImage = MutableLiveData<DailyImage>()


    suspend fun refreshData() {

        val dataString = AsteroidApi.asteroidApiService.getAsteroidData(
            TaskDates.startDate(),
            TaskDates.endDate(),
            Constants.API_KEY
        )
        val jsonObject = JSONObject(dataString)
        val data: List<Asteroid> = parseAsteroidsJsonResult(jsonObject)
        _asteroids.value = data
        val todayImage = AsteroidApi.asteroidApiService.getDailyImage(Constants.API_KEY)
        dailyImage.value = todayImage
        _todayList.value = asteroids.value?.filter {
            it.closeApproachDate == TaskDates.startDate()
        }
        withContext(Dispatchers.IO) {
            database.updateData(data)
        }
    }

}