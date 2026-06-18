package com.tirupati.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tirupati.pos.ui.PosApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.tirupati.pos.core.sync.SyncManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        syncManager.startPeriodicSync()
        setContent {
            PosApp()
        }
    }
}
