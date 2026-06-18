package com.tirupati.pos.data.remote.supabase

import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class SupabaseRemoteDataSource @Inject constructor(
    private val client: SupabaseClient
)
