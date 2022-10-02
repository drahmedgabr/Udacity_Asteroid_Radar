package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(val database: AsteroidDatabaseDao) : ViewModel() {

    val asteroidList: LiveData<List<Asteroid>>
        get() = database.getAllData()


    init {
        getData()
    }

    fun getData() {
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
            } catch (e: Exception) {
                Log.e("MainVM", e.toString())
            }
        }
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