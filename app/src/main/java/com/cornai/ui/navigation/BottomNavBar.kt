package com.cornai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.R
import com.cornai.ui.theme.*

enum class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME("home", R.string.nav_home, Icons.Default.Home, Icons.Default.Home),
    SCAN("scanner", R.string.nav_scan, Icons.Default.QrCodeScanner, Icons.Default.QrCodeScanner),
    HISTORY("history", R.string.nav_history, Icons.Default.History, Icons.Default.History),
    PROFILE("profile", R.string.nav_profile, Icons.Default.Person, Icons.Default.Person)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = BottomNavItem.values()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White.copy(alpha = 0.85f),
        shadowElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(300),
        label = "bg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) GreenPrimary else TextSecondary,
        animationSpec = tween(300),
        label = "icon"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) GreenPrimary else TextSecondary,
        animationSpec = tween(300),
        label = "text"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = stringResource(id = item.labelRes),
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = item.labelRes),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
