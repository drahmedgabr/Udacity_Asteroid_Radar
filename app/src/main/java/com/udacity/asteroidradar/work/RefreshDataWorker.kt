package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepo
import retrofit2.HttpException

class RefreshDataWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(context).asteroidDatabaseDao
        val repo = AsteroidRepo(database)

        return try {
            repo.refreshData()
            repo.deleteOdlData()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}