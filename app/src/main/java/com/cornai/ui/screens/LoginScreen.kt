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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.components.GradientButton
import com.cornai.ui.theme.*

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGuestClick: () -> Unit = onLoginSuccess,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onLoginClick: () -> Unit = onLoginSuccess,
    onResetError: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            onResetError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Selamat Datang",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "Masuk untuk melanjutkan",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = Error, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = errorMessage, color = Error, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                cursorColor = GreenPrimary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Kata Sandi") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                cursorColor = GreenPrimary
            ),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPasswordClick) {
                Text(text = "Lupa kata sandi?", color = GreenPrimary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = if (isLoading) "Masuk..." else "Masuk",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    // Will show error via errorMessage
                } else {
                    onLoginClick()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Divider))
            Text(text = " atau ", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 16.dp))
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Divider))
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onGuestClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = GreenPrimary)
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Masuk sebagai Tamu", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Belum punya akun? ", fontSize = 14.sp, color = TextSecondary)
            TextButton(onClick = onRegisterClick) {
                Text(text = "Daftar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            }
        }
    }
}

@Composable
private fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()