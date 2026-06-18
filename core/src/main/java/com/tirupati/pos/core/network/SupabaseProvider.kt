package com.tirupati.pos.core.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseProvider @Inject constructor() {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://YOUR_PROJECT.supabase.co",
            supabaseKey = "YOUR_ANON_KEY"
        ) {
            install(Postgrest)
        }
    }
}
