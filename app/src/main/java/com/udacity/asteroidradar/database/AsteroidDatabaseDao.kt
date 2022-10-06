package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asteroids: List<Asteroid>)

    @Query("SELECT * FROM asteroid_week_data WHERE close_approach_date >= :today ORDER BY close_approach_date ASC")
    fun getAllData(today: String) : LiveData<List<Asteroid>>

    @Query("DELETE FROM asteroid_week_data WHERE close_approach_date < :today")
    suspend fun deleteOldData(today: String)
}