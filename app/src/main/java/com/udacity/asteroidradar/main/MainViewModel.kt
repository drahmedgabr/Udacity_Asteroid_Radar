package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(val database: AsteroidDatabaseDao) :
    ViewModel() {

    private val _offlineList = database.getAllData(TaskDates.startDate())

    private val _asteroidList = MutableLiveData<List<Asteroid>>()

    private val _todayAsteroid = MutableLiveData<List<Asteroid>>()

    val asteroidList = MediatorLiveData<List<Asteroid>>()

    private val _connectionStatus = MutableLiveData<Boolean>()

    val connectionStatus: LiveData<Boolean>
        get() = _connectionStatus

    private val _dailyImage = MutableLiveData<DailyImage>()

    val dailyImage: LiveData<DailyImage>
        get() = _dailyImage


    init {
        getData()
    }


    fun getData() {
        _connectionStatus.value = true
        viewModelScope.launch {
            try {
                val dataString = AsteroidApi.asteroidApiService.getAsteroidData(
                    TaskDates.startDate(),
                    TaskDates.endDate(),
                    Constants.API_KEY
                )
                val jsonObject = JSONObject(dataString)
                val data: List<Asteroid> = parseAsteroidsJsonResult(jsonObject)
                database.updateData(data)
                _asteroidList.value = data
                showOnlineData()
                _connectionStatus.value = false
                _dailyImage.value = AsteroidApi.asteroidApiService.getDailyImage(Constants.API_KEY)
            } catch (e: Exception) {
                showOfflineData()
                _connectionStatus.value = false
                Log.e("MainVM", e.toString())
            }
        }
    }


    private fun addSource(list: LiveData<List<Asteroid>>) {
        asteroidList.removeSource(_offlineList)
        asteroidList.removeSource(_asteroidList)
        asteroidList.removeSource(_todayAsteroid)
        asteroidList.addSource(list) {
            asteroidList.value = it
        }
    }

    fun showOfflineData() {
        addSource(_offlineList)
    }

    fun showOnlineData() {
        addSource(_asteroidList)
    }

    fun showTodayData() {
        _todayAsteroid.value = _asteroidList.value?.filter {
            it.closeApproachDate == TaskDates.startDate()
        }
        addSource(_todayAsteroid)
    }
}

class TaskDates {
    companion object {
        val formatter = SimpleDateFormat("yyyy-MM-dd")

        fun startDate(): String {
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
