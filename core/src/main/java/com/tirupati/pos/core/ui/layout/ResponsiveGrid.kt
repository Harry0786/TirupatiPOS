package com.tirupati.pos.core.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.ui.home.HomeAction

@Composable
fun ActionsGrid(
    actions: List<HomeAction>,
    columns: Int,
    modifier: Modifier = Modifier,
    content: @Composable (HomeAction) -> Unit
) {
    val safeColumns = columns.coerceAtLeast(1)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.chunked(safeColumns).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowActions.forEach { action ->
                    androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {
                        content(action)
                    }
                }

                if (rowActions.size < safeColumns) {
                    repeat(safeColumns - rowActions.size) {
                        androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
