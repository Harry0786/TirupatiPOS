package com.tirupati.pos.core.di

import com.tirupati.pos.core.sync.DefaultSyncManager
import com.tirupati.pos.core.sync.DefaultSyncScheduler
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
    fun provideSyncManager(impl: DefaultSyncManager): SyncManager = impl

    @Provides
    @Singleton
    fun provideSyncScheduler(impl: DefaultSyncScheduler): SyncScheduler = impl
}
