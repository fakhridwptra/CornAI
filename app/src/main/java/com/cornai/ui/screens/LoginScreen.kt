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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.R
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
    onLoginClick: (String, String) -> Unit,
    onResetError: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    android.util.Log.d("CornAI_Login", "LoginScreen composing. isLoading=$isLoading, errorMessage=$errorMessage")

    val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordStrong = password.length >= 6

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
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_cornai),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
            trailingIcon = {
                if (email.isNotEmpty()) {
                    if (isEmailValid) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Email Valid", tint = Success)
                    } else {
                        Icon(Icons.Default.Error, contentDescription = "Email Invalid", tint = Error)
                    }
                }
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

        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isPasswordStrong) Success else Error)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPasswordStrong) "Sandi kuat" else "Sandi terlalu pendek (min 6 karakter)",
                    fontSize = 11.sp,
                    color = if (isPasswordStrong) Success else Error
                )
            }
        }

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientButton(
                text = if (isLoading) "Masuk..." else "Masuk",
                onClick = {
                    onLoginClick(email, password)
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading && isEmailValid && isPasswordStrong
            )

            val loginContext = LocalContext.current
            IconButton(
                onClick = {
                    android.widget.Toast.makeText(loginContext, "Autentikasi Sidik Jari Berhasil", android.widget.Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GreenPrimary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Masuk Sidik Jari",
                    tint = GreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

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

        Spacer(modifier = Modifier.height(32.dp))

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