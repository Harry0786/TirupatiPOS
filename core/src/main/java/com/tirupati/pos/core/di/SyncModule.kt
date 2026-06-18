package com.tirupati.pos.core.di

import com.tirupati.pos.core.sync.NoOpSyncManager
import com.tirupati.pos.core.sync.NoOpSyncScheduler
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideSyncManager(impl: NoOpSyncManager): SyncManager = impl

    @Provides
    @Singleton
    fun provideSyncScheduler(impl: NoOpSyncScheduler): SyncScheduler = impl
}
