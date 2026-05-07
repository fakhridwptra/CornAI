package com.cornai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBack: () -> Unit,
    onContactSubmit: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf("FAQ", "Kontak", "Feedback")

    val faqCategories = remember {
        listOf(
            FaqCategory(
                title = "Scan & Deteksi",
                faqs = listOf(
                    FaqItem(
                        question = "Bagaimana cara scan tanaman?",
                        answer = "Buka halaman scanner dan arahkan kamera ke daun atau tongkol jagung yang ingin diperiksa."
                    ),
                    FaqItem(
                        question = "Kenapa hasil scan berbeda?",
                        answer = "Pencahayaan dan posisi kamera mempengaruhi hasil. Pastikan cahaya cukup dan bagian yang discan jelas."
                    )
                )
            ),
            FaqCategory(
                title = "Teknis",
                faqs = listOf(
                    FaqItem(
                        question = "App tidak bisa dibuka",
                        answer = "Coba restart HP atau hapus cache aplikasi."
                    ),
                    FaqItem(
                        question = "Kamera tidak berfungsi",
                        answer = "Ijinkan akses kamera di pengaturan HP."
                    )
                )
            ),
            FaqCategory(
                title = "Akun",
                faqs = listOf(
                    FaqItem(
                        question = "Cara daftar akun?",
                        answer = "Buka halaman register dan isi data yang diperlukan."
                    ),
                    FaqItem(
                        question = "Lupa password?",
                        answer = "Gunakan fitur 'Lupa Password' di halaman login."
                    )
                )
            )
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Bantuan & Dukungan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari bantuan...", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = GreenPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> FAQSection(faqCategories = faqCategories)
                1 -> ContactForm(onSubmit = onContactSubmit)
                2 -> FeedbackSection()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Links
            QuickLinksSection()
        }
    }
}

data class FaqCategory(val title: String, val faqs: List<FaqItem>)
data class FaqItem(val question: String, val answer: String)

@Composable
private fun FAQSection(faqCategories: List<FaqCategory>) {
    Column {
        faqCategories.forEach { category ->
            Text(
                text = category.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    category.faqs.forEachIndexed { index, faq ->
                        var expanded by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.question,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }

                            AnimatedVisibility(visible = expanded) {
                                Text(
                                    text = faq.answer,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }

                        if (index < category.faqs.size - 1) {
                            Divider(color = Divider)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactForm(onSubmit: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var issueType by remember { mutableStateOf("") }

    val issueTypes = listOf("Bug Report", "Saran", "Pertanyaan Umum", "Lainnya")

    Column {
        // Issue Type Dropdown
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = issueType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Jenis Pertanyaan") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(16.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                issueTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            issueType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Message Field
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Pesan") },
            leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = GreenPrimary) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(16.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kirim Pesan", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FeedbackSection() {
    var rating by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bagaimana pengalaman Anda?",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Star Rating
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { rating = index + 1 }
                )
            }
        }

        if (rating > 0) {
            Text(
                text = when {
                    rating == 5 -> "Luar biasa! 🌟"
                    rating >= 4 -> "Sangat bagus! 👍"
                    rating >= 3 -> "Bagus 👍"
                    rating >= 2 -> "Bisa lebih baik 💪"
                    else -> "Terima kasih! 🙏"
                },
                fontSize = 14.sp,
                color = GreenPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feedback Text Field
        OutlinedTextField(
            value = feedback,
            onValueChange = { feedback = it },
            placeholder = { Text("Tulis feedback Anda...", fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Icon(Icons.Default.RateReview, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kirim Feedback", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuickLinksSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Link Cepat",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuickLinkItem(
                icon = Icons.Default.VideoLibrary,
                title = "Video Tutorial",
                subtitle = "Pelajari cara penggunaan"
            )

            Divider(color = Divider)

            QuickLinkItem(
                icon = Icons.Default.Forum,
                title = "Komunitas",
                subtitle = "Bergabung dengan komunitas"
            )

            Divider(color = Divider)

            QuickLinkItem(
                icon = Icons.Default.BugReport,
                title = "Report Bug",
                subtitle = "Laporkan masalah teknis"
            )

            Divider(color = Divider)

            QuickLinkItem(
                icon = Icons.Default.Chat,
                title = "Live Chat",
                subtitle = "Chat dengan tim support"
            )
        }
    }
}

@Composable
private fun QuickLinkItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(GreenPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}