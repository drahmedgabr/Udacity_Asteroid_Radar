package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val _asteroidList = MutableLiveData<List<Asteroid>>()

    val asteroidList: LiveData<List<Asteroid>>
    get() = _asteroidList



    init {
        getData()
    }

    fun getData(){
        viewModelScope.launch {
            try {
                val currentTime = Calendar.getInstance().time
                val startDate =SimpleDateFormat("yyyy-MM-DD").format(currentTime).toString()
                val dataString = AsteroidApi.asteroidApiService.getAsteroidData("2022-10-01", "2022-10-08", "hkdKjdyWLVUEaYr7vz4J62lYyiFQkKYOcXo9IHCj")
                val jsonObject = JSONObject(dataString)
                _asteroidList.value = parseAsteroidsJsonResult(jsonObject)
            } catch (e: Exception) {
                Log.e("MainVM", e.toString())
            }
        }
    }
}