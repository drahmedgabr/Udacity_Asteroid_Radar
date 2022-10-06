package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.repository.AsteroidRepo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(val database: AsteroidDatabaseDao) :
    ViewModel() {

    val repo = AsteroidRepo(database)

    val asteroidList = MediatorLiveData<List<Asteroid>>()

    private val _connectionStatus = MutableLiveData<Boolean>()

    val connectionStatus: LiveData<Boolean>
        get() = _connectionStatus

    val dailyImage: LiveData<DailyImage>
        get() = repo.dailyImage

    init {
        getData()
    }


    fun getData() {
        _connectionStatus.value = true
        viewModelScope.launch {
            try {
                repo.refreshData()
                repo.getTodayItems()
                showOnlineData()
                _connectionStatus.value = false
            } catch (e: Exception) {
                showOfflineData()
                _connectionStatus.value = false
            }
        }
    }


    private fun addSource(list: LiveData<List<Asteroid>>) {
        asteroidList.removeSource(repo.offlineList)
        asteroidList.removeSource(repo.asteroids)
        asteroidList.removeSource(repo.todayList)
        asteroidList.addSource(list) {
            asteroidList.value = it
        }
    }

    fun showOfflineData() {
        addSource(repo.offlineList)
    }

    fun showOnlineData() {
        addSource(repo.asteroids)
    }

    fun showTodayData() {
        addSource(repo.todayList)
    }
}
