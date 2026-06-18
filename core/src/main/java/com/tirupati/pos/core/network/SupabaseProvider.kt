package com.tirupati.pos.core.network

import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lightweight wrapper around SupabaseClient so DI can inject shared client.
 * The actual client is provided by [com.tirupati.pos.core.di.NetworkModule].
 */
@Singleton
class SupabaseProvider @Inject constructor(private val client: SupabaseClient) {
    fun client(): SupabaseClient = client
}
