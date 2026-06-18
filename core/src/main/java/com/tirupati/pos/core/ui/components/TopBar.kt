package com.tirupati.pos.core.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlin.Suppress
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import android.content.res.Resources
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    shopName: String,
    modifier: Modifier = Modifier,
    onSearchClicked: () -> Unit = {},
    onNotificationsClicked: () -> Unit = {},
    onOverflowClicked: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(text = shopName, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            AppLogo(size = 40.dp)
        },
        actions = {
            IconButton(onClick = onSearchClicked, modifier = Modifier.semantics { contentDescription = "Search" }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
            IconButton(onClick = onNotificationsClicked, modifier = Modifier.semantics { contentDescription = "Notifications" }) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
            }
            IconButton(onClick = onOverflowClicked, modifier = Modifier.semantics { contentDescription = "More" }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun AppLogo(size: Dp) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    // Prefer mipmap `ic_app_logo` from the app package so it resolves at runtime
    val resId = try {
        ctx.resources.getIdentifier("ic_app_logo", "mipmap", ctx.packageName)
    } catch (_: Resources.NotFoundException) { 0 }

    if (resId != 0) {
        val drawable = remember(resId) { ContextCompat.getDrawable(ctx, resId) }
        if (drawable != null) {
            val sizePx = with(density) { size.toPx().roundToInt() }
            val bitmap = remember(drawable, sizePx) {
                val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else sizePx
                val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else sizePx
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { bmp ->
                    val canvas = Canvas(bmp)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                }
            }

            Image(
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = "App logo",
                modifier = Modifier.size(size)
            )
            return
        }
    }

    // No resource found or drawable null — render a small text fallback for accessibility
    Text(text = "TE", style = MaterialTheme.typography.titleMedium)
}
