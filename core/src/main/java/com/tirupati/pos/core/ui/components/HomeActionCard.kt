package com.tirupati.pos.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import com.tirupati.pos.core.ui.layout.calculateWindowWidthSizeClass
import com.tirupati.pos.core.ui.layout.WindowWidthSizeClass
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.ui.home.HomeAction

@Composable
fun HomeActionCard(
    action: HomeAction,
    onClick: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth, premium spring-based press scaling
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_scale"
    )

    // Map Action ID / Route to its corresponding themed Color
    val baseColor = remember(action.id) {
        when (action.id) {
            "new_sale" -> Color(0xFF2563EB)   // Blue
            "products" -> Color(0xFF10B981)   // Green
            "purchases" -> Color(0xFF8B5CF6)  // Purple
            "inventory" -> Color(0xFFF59E0B)  // Orange
            "customers" -> Color(0xFF2563EB)  // Blue
            "suppliers" -> Color(0xFF10B981)  // Green
            "reports" -> Color(0xFFEF4444)    // Red
            "settings" -> Color(0xFF6B7280)   // Gray
            else -> Color(0xFF2563EB)
        }
    }

    val widthClass = calculateWindowWidthSizeClass()
    val isTablet = widthClass != WindowWidthSizeClass.Compact
    val paddingValues = if (isTablet) 20.dp else 12.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { onClick(action) }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 0.dp
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Colored Icon Backdrop
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 44.dp else 36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(baseColor.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    val iconSize = if (isTablet) 22.dp else 18.dp
                    when (action.id) {
                        "new_sale" -> NewSaleIcon(color = baseColor, modifier = Modifier.size(iconSize))
                        "products" -> ProductsIcon(color = baseColor, modifier = Modifier.size(iconSize))
                        "purchases" -> Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = baseColor,
                            modifier = Modifier.size(iconSize)
                        )
                        "inventory" -> InventoryIcon(color = baseColor, modifier = Modifier.size(iconSize))
                        "customers" -> CustomersIcon(color = baseColor, modifier = Modifier.size(iconSize))
                        "suppliers" -> SuppliersIcon(color = baseColor, modifier = Modifier.size(iconSize))
                        "reports" -> ReportsIcon(color = baseColor, modifier = Modifier.size(iconSize))
                        "settings" -> Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = baseColor,
                            modifier = Modifier.size(iconSize)
                        )
                        else -> Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = baseColor,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(if (isTablet) 16.dp else 10.dp))

                Column {
                    Text(
                        text = action.title,
                        style = if (isTablet) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280),
                        maxLines = 1
                    )
                }
            }

            ChevronRightIcon(
                color = Color(0xFFD1D5DB),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
