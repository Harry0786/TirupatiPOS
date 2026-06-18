package com.tirupati.pos.core.network

import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

/**
 * Generic supabase data source — no table-specific logic here.
 */
class SupabaseDataSource @Inject constructor(
    private val client: SupabaseClient
) {
    // Provide helper entry points for remote operations in future features.
}
