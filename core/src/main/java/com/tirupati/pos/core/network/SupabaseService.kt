package com.tirupati.pos.core.network

import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseService @Inject constructor(
    private val client: SupabaseClient
) {
    // Placeholder for shared client operations
    fun client(): SupabaseClient = client
}
