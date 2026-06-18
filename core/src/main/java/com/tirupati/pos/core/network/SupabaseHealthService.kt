package com.tirupati.pos.core.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseHealthService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val pkg = context.packageName
            val res = context.resources
            val urlId = res.getIdentifier("supabase_url", "string", pkg)
            val urlStr = if (urlId != 0) res.getString(urlId) else ""
            
            if (urlStr.isEmpty()) return@withContext false

            val url = URL("$urlStr/rest/v1/")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val responseCode = connection.responseCode
            // Even a 401 Unauthorized means the backend is reachable
            responseCode in 200..499
        } catch (e: Exception) {
            false
        }
    }
}
