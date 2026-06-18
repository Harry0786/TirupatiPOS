package com.tirupati.pos.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PrinterIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        // Top paper slot
        drawRect(
            color = color,
            topLeft = Offset(w * 0.25f, h * 0.05f),
            size = Size(w * 0.5f, h * 0.2f)
        )
        // Main body
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, h * 0.25f),
            size = Size(w, h * 0.5f),
            cornerRadius = CornerRadius(2.5f.dp.toPx(), 2.5f.dp.toPx())
        )
        // Bottom output
        drawRect(
            color = color,
            topLeft = Offset(w * 0.2f, h * 0.75f),
            size = Size(w * 0.6f, h * 0.2f)
        )
        // LED light
        drawCircle(
            color = Color.White,
            radius = 1.dp.toPx(),
            center = Offset(w * 0.8f, h * 0.5f)
        )
    }
}

@Composable
fun BarcodeIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        // Distinct barcode vertical line widths
        drawRect(color = color, topLeft = Offset(w * 0.05f, h * 0.1f), size = Size(w * 0.12f, h * 0.8f))
        drawRect(color = color, topLeft = Offset(w * 0.25f, h * 0.1f), size = Size(w * 0.06f, h * 0.8f))
        drawRect(color = color, topLeft = Offset(w * 0.38f, h * 0.1f), size = Size(w * 0.2f, h * 0.8f))
        drawRect(color = color, topLeft = Offset(w * 0.65f, h * 0.1f), size = Size(w * 0.06f, h * 0.8f))
        drawRect(color = color, topLeft = Offset(w * 0.78f, h * 0.1f), size = Size(w * 0.12f, h * 0.8f))
    }
}

@Composable
fun DatabaseSyncIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        // 3 stacked database cylinders
        drawRoundRect(color = color, topLeft = Offset(0f, 0f), size = Size(w, h * 0.24f), cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()))
        drawRoundRect(color = color, topLeft = Offset(0f, h * 0.35f), size = Size(w, h * 0.24f), cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()))
        drawRoundRect(color = color, topLeft = Offset(0f, h * 0.7f), size = Size(w, h * 0.24f), cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()))
    }
}

@Composable
fun NewSaleIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        // Register/Terminal base
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f, h * 0.45f),
            size = Size(w * 0.7f, h * 0.45f),
            cornerRadius = CornerRadius(2.5f.dp.toPx(), 2.5f.dp.toPx())
        )
        // Receipt paper coming out of the top slot
        drawRect(
            color = color,
            topLeft = Offset(w * 0.35f, h * 0.1f),
            size = Size(w * 0.3f, h * 0.35f)
        )
        // Horizontal lines on paper (receipt text representation)
        drawLine(
            color = Color.White,
            start = Offset(w * 0.4f, h * 0.2f),
            end = Offset(w * 0.6f, h * 0.2f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.4f, h * 0.3f),
            end = Offset(w * 0.55f, h * 0.3f),
            strokeWidth = 1.dp.toPx()
        )
        // Cash drawer button/handle
        drawCircle(
            color = Color.White,
            radius = 1.5f.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.68f)
        )
    }
}

@Composable
fun ProductsIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        // Bottom container box body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f, h * 0.35f),
            size = Size(w * 0.7f, h * 0.55f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        // Left flap of open box
        drawLine(
            color = color,
            start = Offset(w * 0.15f, h * 0.35f),
            end = Offset(w * 0.05f, h * 0.15f),
            strokeWidth = 2.dp.toPx()
        )
        // Right flap of open box
        drawLine(
            color = color,
            start = Offset(w * 0.85f, h * 0.35f),
            end = Offset(w * 0.95f, h * 0.15f),
            strokeWidth = 2.dp.toPx()
        )
        // Box label detail for high visual fidelity
        drawRect(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = Offset(w * 0.42f, h * 0.45f),
            size = Size(w * 0.16f, h * 0.25f)
        )
    }
}

@Composable
fun InventoryIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // Draw 3D isometric cube sides to represent Inventory Stock
        val pathLeft = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.5f, h * 0.9f)
            close()
        }
        drawPath(pathLeft, color = color)
        
        val pathRight = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.75f)
            lineTo(w * 0.5f, h * 0.9f)
            close()
        }
        drawPath(pathRight, color = color.copy(alpha = 0.85f))
        
        val pathTop = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.5f, h * 0.2f)
            lineTo(w * 0.85f, h * 0.35f)
            close()
        }
        drawPath(pathTop, color = color.copy(alpha = 0.7f))
    }
}

@Composable
fun CustomersIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // Secondary/Background Person
        drawCircle(
            color = color.copy(alpha = 0.6f),
            radius = w * 0.15f,
            center = Offset(w * 0.7f, h * 0.35f)
        )
        drawRoundRect(
            color = color.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.5f, h * 0.55f),
            size = Size(w * 0.4f, h * 0.35f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        
        // Primary/Foreground Person
        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = Offset(w * 0.35f, h * 0.35f)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.1f, h * 0.55f),
            size = Size(w * 0.5f, h * 0.35f),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
        )
    }
}

@Composable
fun SuppliersIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // Truck cargo body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.1f, h * 0.25f),
            size = Size(w * 0.55f, h * 0.45f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        // Truck driver cab
        val pathCab = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.65f, h * 0.35f)
            lineTo(w * 0.82f, h * 0.35f)
            lineTo(w * 0.9f, h * 0.5f)
            lineTo(w * 0.9f, h * 0.7f)
            lineTo(w * 0.65f, h * 0.7f)
            close()
        }
        drawPath(pathCab, color = color)
        
        // Cab window
        val pathWindow = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.68f, h * 0.39f)
            lineTo(w * 0.78f, h * 0.39f)
            lineTo(w * 0.83f, h * 0.5f)
            lineTo(w * 0.68f, h * 0.5f)
            close()
        }
        drawPath(pathWindow, color = Color.White)
        
        // Wheels
        drawCircle(
            color = Color.Black.copy(alpha = 0.8f),
            radius = w * 0.09f,
            center = Offset(w * 0.28f, h * 0.76f)
        )
        drawCircle(
            color = Color.Black.copy(alpha = 0.8f),
            radius = w * 0.09f,
            center = Offset(w * 0.72f, h * 0.76f)
        )
        // Wheel metal hubs
        drawCircle(
            color = Color.White,
            radius = w * 0.03f,
            center = Offset(w * 0.28f, h * 0.76f)
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.03f,
            center = Offset(w * 0.72f, h * 0.76f)
        )
    }
}

@Composable
fun ReportsIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val barWidth = w * 0.16f
        val gap = w * 0.12f
        
        // Bar 1 (left)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f, h * 0.45f),
            size = Size(barWidth, h * 0.45f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        
        // Bar 2 (middle)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f + barWidth + gap, h * 0.2f),
            size = Size(barWidth, h * 0.7f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        
        // Bar 3 (right)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f + (barWidth + gap) * 2f, h * 0.35f),
            size = Size(barWidth, h * 0.55f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

@Composable
fun ChevronRightIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.35f, h * 0.25f)
            lineTo(w * 0.6f, h * 0.5f)
            lineTo(w * 0.35f, h * 0.75f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
