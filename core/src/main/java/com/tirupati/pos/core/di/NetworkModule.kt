package com.tirupati.pos.core.di

import com.tirupati.pos.core.network.SupabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseProvider(): SupabaseProvider = SupabaseProvider()
}
