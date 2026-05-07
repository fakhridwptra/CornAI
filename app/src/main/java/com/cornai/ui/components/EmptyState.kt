package com.cornai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*

@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = GreenPrimary.copy(alpha = 0.1f)
            ) {}
            Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                icon()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAction,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun HistoryEmptyState(onScanClick: () -> Unit) {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = "Belum Ada Riwayat",
        message = "Scan tanaman jagung Anda untuk memulai deteksi penyakit",
        actionLabel = "Mulai Scan",
        onAction = onScanClick
    )
}

@Composable
fun SearchEmptyState(query: String) {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = "Tidak Ditemukan",
        message = "Tidak ada hasil untuk \"$query\"",
        actionLabel = null,
        onAction = null
    )
}

@Composable
fun NotificationEmptyState() {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = "Tidak Ada Notifikasi",
        message = "Semua notifikasi sudah dibaca"
    )
}

@Composable
fun ProfilePhotoEmptyState() {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = "Belum Ada Foto",
        message = "Tambahkan foto profil untuk personalisasi"
    )
}