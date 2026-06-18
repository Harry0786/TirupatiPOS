package com.tirupati.pos.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.ui.components.StatusChip
import androidx.compose.ui.Alignment
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart

@Composable
fun ShopInfoCard(
    shopName: String,
    date: String,
    time: String,
    internetStatus: String,
    syncStatus: String,
    pending: Int,
    lastSync: String,
    printerStatus: String,
    scannerStatus: String,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // small circular shop avatar placeholder
                androidx.compose.foundation.layout.Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                }

                Column(modifier = Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = shopName, style = MaterialTheme.typography.titleLarge)
                    Text(text = "$date • $time", style = MaterialTheme.typography.bodySmall)
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(text = internetStatus)
                    StatusChip(text = "Pending: $pending")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(text = "Printer: $printerStatus")
                    StatusChip(text = "Scanner: $scannerStatus")
                }
            }
        }
    }
}
