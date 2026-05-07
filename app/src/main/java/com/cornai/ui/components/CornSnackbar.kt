package com.cornai.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*

enum class SnackbarType {
    SUCCESS, ERROR, WARNING, INFO
}

@Composable
fun CornSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.INFO,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, icon, iconTint) = when (type) {
        SnackbarType.SUCCESS -> Triple(Success, Icons.Default.CheckCircle, Color.White)
        SnackbarType.ERROR -> Triple(Error, Icons.Default.Error, Color.White)
        SnackbarType.WARNING -> Triple(Warning, Icons.Default.Warning, Color.Black)
        SnackbarType.INFO -> Triple(GreenPrimary, Icons.Default.Info, Color.White)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (type == SnackbarType.WARNING) Color.Black else Color.White,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = if (type == SnackbarType.WARNING) Color.Black else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CornSnackbarSuccess(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    CornSnackbar(message = message, type = SnackbarType.SUCCESS, onDismiss = onDismiss, modifier = modifier)
}

@Composable
fun CornSnackbarError(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    CornSnackbar(message = message, type = SnackbarType.ERROR, onDismiss = onDismiss, modifier = modifier)
}

@Composable
fun CornSnackbarWarning(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    CornSnackbar(message = message, type = SnackbarType.WARNING, onDismiss = onDismiss, modifier = modifier)
}

@Composable
fun CornSnackbarInfo(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    CornSnackbar(message = message, type = SnackbarType.INFO, onDismiss = onDismiss, modifier = modifier)
}