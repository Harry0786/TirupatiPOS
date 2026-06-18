package com.tirupati.pos.data.remote

import com.tirupati.pos.core.network.SupabaseService

/**
 * Generic remote data source abstraction for Supabase operations.
 */
interface RemoteDataSource {
    val supabase: SupabaseService
}
