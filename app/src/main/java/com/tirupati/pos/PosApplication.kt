package com.tirupati.pos

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.tirupati.pos.core.sync.SyncScheduler
import com.tirupati.pos.feature.products.data.repository.InsertMockData

@HiltAndroidApp
class PosApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var syncScheduler: SyncScheduler
    @Inject lateinit var mockDataInserter: InsertMockData

    override fun onCreate() {
        super.onCreate()
        mockDataInserter.seedIfEmpty()
        syncScheduler.schedulePeriodicSync()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
