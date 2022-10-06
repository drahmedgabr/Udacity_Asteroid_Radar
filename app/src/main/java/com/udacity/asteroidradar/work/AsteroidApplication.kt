package com.udacity.asteroidradar.work

import android.app.Application
import androidx.work.*
import java.util.concurrent.TimeUnit

class AsteroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupWork()
    }

    private fun setupWork() {

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}