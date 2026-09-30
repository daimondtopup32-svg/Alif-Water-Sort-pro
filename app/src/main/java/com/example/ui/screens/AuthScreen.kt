package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundTheme
import com.example.ui.components.GameAestheticBackground

enum class AuthMode {
    LOGIN,
    REGISTER
}

@Composable
fun AuthScreen(
    isBengali: Boolean,
    bgTheme: BackgroundTheme,
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (usernameOrEmail: String, password: String) -> Unit,
    onRegister: (username: String, email: String, password: String, avatarEmoji: String) -> Unit,
    onPlayAsGuest: () -> Unit,
    onToggleLanguage: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var mode by remember { mutableStateOf(AuthMode.LOGIN) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val avatars = listOf("💧", "🧪", "👑", "⚡", "🌟", "🧙", "🐯", "🚀", "🎮", "💎")
    var selectedAvatar by remember { mutableStateOf(avatars[0]) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var localError by remember { mutableStateOf<String?>(null) }

    fun submit() {
        localError = null
        if (mode == AuthMode.LOGIN) {
            if (username.isBlank() || password.isBlank()) {
                localError = if (isBengali) "দয়া করে সব ঘর পূরণ করুন" else "Please fill all fields"
                return
            }
            onLogin(username, password)
        } else {
            if (username.trim().length < 3) {
                localError = if (isBengali) "নাম কমপক্ষে ৩ অক্ষরের হতে হবে" else "Username must be at least 3 chars"
                return
            }
            if (password.length < 4) {
                localError = if (isBengali) "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে" else "Password must be at least 4 chars"
                return
            }
            if (password != confirmPassword) {
                localError = if (isBengali) "পাসওয়ার্ড দুটি মেলেনি" else "Passwords do not match"
                return
            }
            onRegister(username, email, password, selectedAvatar)
        }
    }

    GameAestheticBackground(
        theme = bgTheme,
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header: Back Button & Language Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .testTag("auth_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onToggleLanguage,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color(0xFF00E5FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Logo / Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))
                        )
                    )
                    .border(2.dp, Color(0x66FFFFFF), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🧪",
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isBengali) "আলিফ ওয়াটার সর্ট প্রো" else "Alif Water Sort Pro",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = if (isBengali) "আপনার অ্যাকাউন্ট তৈরি বা লগইন করুন" else "Login or create your account",
                fontSize = 13.sp,
                color = Color(0xAAFFFFFF)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF131D38).copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Segmented Tabs: Login / Register
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (mode == AuthMode.LOGIN) Color(0xFF00E5FF) else Color.Transparent
                                )
                                .clickable {
                                    mode = AuthMode.LOGIN
                                    localError = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBengali) "লগ ইন" else "Login",
                                fontWeight = FontWeight.Bold,
                                color = if (mode == AuthMode.LOGIN) Color(0xFF061E38) else Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (mode == AuthMode.REGISTER) Color(0xFF00E5FF) else Color.Transparent
                                )
                                .clickable {
                                    mode = AuthMode.REGISTER
                                    localError = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBengali) "রেজিস্ট্রেশন" else "Register",
                                fontWeight = FontWeight.Bold,
                                color = if (mode == AuthMode.REGISTER) Color(0xFF061E38) else Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Avatar Selector for Registration
                    if (mode == AuthMode.REGISTER) {
                        Text(
                            text = if (isBengali) "আপনার অবতার নির্বাচন করুন:" else "Choose your avatar:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xCCFFFFFF),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(avatars) { emoji ->
                                val isSelected = emoji == selectedAvatar
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.3f) else Color(0x22FFFFFF)
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color(0xFF00E5FF) else Color(0x33FFFFFF),
                                            shape = CircleShape
                                        )
                                        .clickable { selectedAvatar = emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 22.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = {
                            Text(
                                if (isBengali) "ব্যবহারকারীর নাম" else "Username"
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = if (mode == AuthMode.REGISTER) ImeAction.Next else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_username_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF00E5FF),
                            unfocusedLabelColor = Color(0xAAFFFFFF)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // Email Input (Registration Only)
                    if (mode == AuthMode.REGISTER) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text(
                                    if (isBengali) "ইমেইল (ঐচ্ছিক)" else "Email (Optional)"
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color(0x44FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFF00E5FF),
                                unfocusedLabelColor = Color(0xAAFFFFFF)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                if (isBengali) "পাসওয়ার্ড" else "Password"
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF38BDF8))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xAAFFFFFF)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (mode == AuthMode.REGISTER) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = {
                                focusManager.clearFocus()
                                submit()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF00E5FF),
                            unfocusedLabelColor = Color(0xAAFFFFFF)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // Confirm Password (Registration Only)
                    if (mode == AuthMode.REGISTER) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = {
                                Text(
                                    if (isBengali) "পাসওয়ার্ড নিশ্চিত করুন" else "Confirm Password"
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    submit()
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_confirm_password_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color(0x44FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFF00E5FF),
                                unfocusedLabelColor = Color(0xAAFFFFFF)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    // Error Message
                    val activeError = localError ?: errorMessage
                    if (activeError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = activeError,
                            fontSize = 12.sp,
                            color = Color(0xFFFF5252),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Submit Button
                    Button(
                        onClick = { submit() },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.Black,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = if (mode == AuthMode.LOGIN) {
                                    if (isBengali) "লগ ইন করুন" else "Login"
                                } else {
                                    if (isBengali) "অ্যাকাউন্ট তৈরি করুন" else "Create Account"
                                },
                                color = Color(0xFF061E38),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider or OR text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0x33FFFFFF))
                        )
                        Text(
                            text = if (isBengali) " অথবা " else " OR ",
                            fontSize = 12.sp,
                            color = Color(0x88FFFFFF),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0x33FFFFFF))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play as Guest Button
                    OutlinedButton(
                        onClick = onPlayAsGuest,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("play_as_guest_button"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFFFFF)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isBengali) "🎮 অতিথি হিসেবে সরাসরি খেলুন" else "🎮 Play Directly as Guest",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
