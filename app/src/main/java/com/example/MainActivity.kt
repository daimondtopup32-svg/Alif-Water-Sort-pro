package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.PolicyDialog
import com.example.ui.components.ThemeSettingsSheet
import com.example.ui.components.UserProfileDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.LoadingScreen
import com.example.ui.screens.StartMenuScreen
import com.example.ui.screens.WaterSortScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    LOADING,
    AUTH,
    START_MENU,
    GAMEPLAY
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize Google AdMob SDK
        com.example.ads.AdManager.initialize(this)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: GameViewModel = viewModel()
                    val state by viewModel.uiState.collectAsState()

                    var currentScreen by remember { mutableStateOf(AppScreen.LOADING) }
                    var showLevelSelectDialog by remember { mutableStateOf(false) }
                    var showThemeSettingsSheet by remember { mutableStateOf(false) }
                    var showUserProfileDialog by remember { mutableStateOf(false) }
                    var showPolicyDialog by remember { mutableStateOf(false) }

                    when (currentScreen) {
                        AppScreen.LOADING -> {
                            LoadingScreen(
                                isBengali = state.isBengali,
                                bgTheme = state.bgTheme,
                                onLoadingComplete = {
                                    // User sees Home Page (Start Menu) first on launch
                                    currentScreen = AppScreen.START_MENU
                                }
                            )
                        }
                        AppScreen.AUTH -> {
                            AuthScreen(
                                isBengali = state.isBengali,
                                bgTheme = state.bgTheme,
                                isLoading = state.isAuthLoading,
                                errorMessage = state.authErrorMessage,
                                onLogin = { usernameOrEmail, password ->
                                    viewModel.login(usernameOrEmail, password) {
                                        currentScreen = AppScreen.START_MENU
                                    }
                                },
                                onRegister = { username, email, password, avatarEmoji ->
                                    viewModel.register(username, email, password, avatarEmoji) {
                                        currentScreen = AppScreen.START_MENU
                                    }
                                },
                                onPlayAsGuest = {
                                    viewModel.playAsGuest {
                                        currentScreen = AppScreen.START_MENU
                                    }
                                },
                                onToggleLanguage = {
                                    viewModel.toggleLanguage()
                                },
                                onBack = {
                                    currentScreen = AppScreen.START_MENU
                                }
                            )
                        }
                        AppScreen.START_MENU -> {
                            StartMenuScreen(
                                currentLevel = state.currentLevel,
                                coins = state.coins,
                                isBengali = state.isBengali,
                                bgTheme = state.bgTheme,
                                currentUser = state.currentUser,
                                onStartGame = {
                                    // Start button opens Level selection page directly
                                    showLevelSelectDialog = true
                                },
                                onOpenLevelSelect = {
                                    showLevelSelectDialog = true
                                },
                                onOpenThemes = {
                                    showThemeSettingsSheet = true
                                },
                                onToggleLanguage = {
                                    viewModel.toggleLanguage()
                                },
                                onOpenProfile = {
                                    showUserProfileDialog = true
                                },
                                onOpenLogin = {
                                    currentScreen = AppScreen.AUTH
                                },
                                onOpenPolicy = {
                                    showPolicyDialog = true
                                }
                            )

                            // Policy Dialog
                            PolicyDialog(
                                show = showPolicyDialog,
                                isBengali = state.isBengali,
                                onDismiss = { showPolicyDialog = false }
                            )

                            // User Profile Dialog
                            UserProfileDialog(
                                show = showUserProfileDialog,
                                user = state.currentUser,
                                isBengali = state.isBengali,
                                coins = state.coins,
                                currentLevel = state.currentLevel,
                                onSelectAvatar = { viewModel.updateUserAvatar(it) },
                                onLogout = {
                                    viewModel.logout {
                                        showUserProfileDialog = false
                                        currentScreen = AppScreen.AUTH
                                    }
                                },
                                onOpenAuth = {
                                    showUserProfileDialog = false
                                    currentScreen = AppScreen.AUTH
                                },
                                onDismiss = { showUserProfileDialog = false }
                            )

                            // Dialogs accessible from Start Menu
                            LevelSelectDialog(
                                show = showLevelSelectDialog,
                                currentLevel = state.currentLevel,
                                maxUnlockedLevel = state.maxUnlockedLevel,
                                isBengali = state.isBengali,
                                getStarsForLevel = { viewModel.preferences.getLevelStars(it) },
                                onSelectLevel = {
                                    viewModel.loadLevel(it)
                                    showLevelSelectDialog = false
                                    currentScreen = AppScreen.GAMEPLAY
                                },
                                onDismiss = { showLevelSelectDialog = false }
                            )

                            ThemeSettingsSheet(
                                show = showThemeSettingsSheet,
                                isBengali = state.isBengali,
                                coins = state.coins,
                                unlockedSkins = state.unlockedSkins,
                                unlockedBgThemes = state.unlockedBgThemes,
                                shopMessage = state.shopMessage,
                                soundEnabled = state.soundEnabled,
                                soundVolume = state.soundVolume,
                                vibrationEnabled = state.vibrationEnabled,
                                currentTubeSkin = state.tubeSkin,
                                currentBgTheme = state.bgTheme,
                                onToggleSound = { viewModel.toggleSound() },
                                onVolumeChange = { viewModel.setSoundVolume(it) },
                                onToggleVibration = { viewModel.toggleVibration() },
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onBuyOrEquipSkin = { viewModel.buyOrEquipSkin(it) },
                                onBuyOrEquipBgTheme = { viewModel.buyOrEquipBgTheme(it) },
                                onClearShopMessage = { viewModel.clearShopMessage() },
                                onDismiss = { showThemeSettingsSheet = false }
                            )
                        }
                        AppScreen.GAMEPLAY -> {
                            WaterSortScreen(
                                viewModel = viewModel,
                                onBackToMenu = {
                                    currentScreen = AppScreen.START_MENU
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
