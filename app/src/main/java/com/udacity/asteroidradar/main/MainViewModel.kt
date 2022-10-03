package com.udacity.asteroidradar.main

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(val database: AsteroidDatabaseDao) : ViewModel() {

    private val _asteroidList = MutableLiveData<List<Asteroid>>()

    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _offlineList: LiveData<List<Asteroid>>
    get() = database.getAllData()

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
                val data = parseAsteroidsJsonResult(jsonObject)
                database.updateData(data)
                _asteroidList.value = data
                _connectionStatus.value = false
                _dailyImage.value = AsteroidApi.asteroidApiService.getDailyImage(Constants.API_KEY)
            } catch (e: Exception) {
                getOfflineData()
                Log.e("MainVM", e.toString())
            }
        }
    }

    fun getOfflineData() {
        _connectionStatus.value = true
        _asteroidList.value = _offlineList.value
        _connectionStatus.value = false
    }

}

class TaskDates {

    companion object {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd")

        fun startDate(): String {
            val start = calendar.time
            return formatter.format(start)
        }

        fun endDate(): String {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val end = calendar.time
            return formatter.format(end)
        }
    }
}