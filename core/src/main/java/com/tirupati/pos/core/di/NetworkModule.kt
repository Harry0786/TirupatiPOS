package com.tirupati.pos.core.di

import android.content.Context
import com.tirupati.pos.core.network.ConnectivityNetworkMonitor
import com.tirupati.pos.core.network.ConnectivityObserver
import com.tirupati.pos.core.network.NetworkConnectivityObserver
import com.tirupati.pos.core.network.NetworkMonitor
import com.tirupati.pos.core.network.SupabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(@ApplicationContext context: Context): SupabaseClient {
        val pkg = context.packageName
        val res = context.resources
        val urlId = res.getIdentifier("supabase_url", "string", pkg)
        val keyId = res.getIdentifier("supabase_anon_key", "string", pkg)

        val url = if (urlId != 0) res.getString(urlId) else "https://YOUR_PROJECT.supabase.co"
        val key = if (keyId != 0) res.getString(keyId) else "YOUR_ANON_KEY"

        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Postgrest)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseProvider(client: SupabaseClient): SupabaseProvider = SupabaseProvider(client)

    @Provides
    @Singleton
    fun provideNetworkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor = impl

    @Provides
    @Singleton
    fun provideConnectivityObserver(impl: NetworkConnectivityObserver): ConnectivityObserver = impl
}
