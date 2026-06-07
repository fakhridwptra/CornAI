package com.cornai.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.components.GradientButton
import com.cornai.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onSendSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(GreenPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Lupa Kata Sandi?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isSent)
                "Kami telah mengirim tautan reset kata sandi ke email Anda"
            else
                "Masukkan email Anda dan kami akan mengirim tautan untuk reset kata sandi",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (!isSent) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    cursorColor = GreenPrimary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = Error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            GradientButton(
                text = if (isLoading) "Mengirim..." else "Kirim Tautan Reset",
                onClick = {
                    if (email.isBlank()) {
                        errorMessage = "Email harus diisi"
                    } else if (!email.contains("@")) {
                        errorMessage = "Format email tidak valid"
                    } else {
                        isLoading = true
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            isLoading = false
                            isSent = true
                        }, 1500)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tautan reset sudah dikirim ke $email",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tidak menerima email?",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { isSent = false }) {
                Text("Kirim ulang", color = GreenPrimary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Back to Login
        TextButton(onClick = onBack) {
            Text(
                text = "← Kembali ke Masuk",
                fontSize = 14.sp,
                color = GreenPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
