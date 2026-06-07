package com.cornai.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Kebijakan Privasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Corn AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Terakhir diperbarui: Mei 2026",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val sections = listOf(
                PrivacySection(
                    title = "1. Informasi yang Kami Kumpulkan",
                    content = "Corn AI mengumpulkan informasi berikut:\n\n• Foto scan yang Anda ambil dengan kamera aplikasi\n• Data riwayat scan yang tersimpan di perangkat Anda\n• Informasi perangkat (untuk keperluan debugging)\n\nKami tidak mengumpulkan data pribadi tanpa persetujuan Anda."
                ),
                PrivacySection(
                    title = "2. Penggunaan Informasi",
                    content = "Informasi yang dikumpulkan digunakan untuk:\n\n• Menyediakan fitur deteksi penyakit jagung\n• Menyimpan riwayat scan Anda\n• Meningkatkan kinerja aplikasi\n• Analisis untuk pengembangan fitur baru"
                ),
                PrivacySection(
                    title = "3. Penyimpanan Data",
                    content = "• Semua data scan disimpan secara lokal di perangkat Anda\n• Kami tidak mengunggah data Anda ke server manapun\n• Data dapat dihapus kapan saja melalui pengaturan aplikasi\n•Kami tidak menjual atau membagikan data Anda kepada pihak ketiga"
                ),
                PrivacySection(
                    title = "4. Keamanan Data",
                    content = "Kami mengambil langkah-langkah keamanan berikut:\n\n• Data disimpan secara terenkripsi di perangkat\n• Tidak ada transmisi data ke server eksternal\n• Akses ke data hanya melalui aplikasi Corn AI"
                ),
                PrivacySection(
                    title = "5. Hak Anda",
                    content = "Anda memiliki hak untuk:\n\n• Mengakses data pribadi Anda kapan saja\n• Menghapus semua data yang tersimpan\n• Menolak pengumpulan data tertentu\n• Menghubungi kami untuk pertanyaan privasi"
                ),
                PrivacySection(
                    title = "6. Perubahan Kebijakan",
                    content = "Kebijakan privasi ini dapat diperbarui sewaktu-waktu. Perubahan signifikan akan diumumkan melalui aplikasi. Dengan melanjutkan penggunaan aplikasi, Anda dianggap menyetujui kebijakan privasi yang berlaku."
                ),
                PrivacySection(
                    title = "7. Hubungi Kami",
                    content = "Jika Anda memiliki pertanyaan tentang kebijakan privasi ini, silakan hubungi kami:\n\n• Email: privacy@cornai.app\n• Melalui aplikasi: Pengaturan > Bantuan"
                )
            )

            sections.forEach { section ->
                SectionCard(section = section)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

private data class PrivacySection(
    val title: String,
    val content: String
)

@Composable
private fun SectionCard(section: PrivacySection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = section.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = section.content,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}
