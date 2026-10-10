package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.LoadingScreen
import com.example.ui.screens.StartMenuScreen
import com.example.ui.screens.WaterSortScreen
import com.example.ui.theme.WaterSortTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.ScreenState

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaterSortTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: GameViewModel) {
    val screenState by viewModel.screenState.collectAsState()

    when (screenState) {
        ScreenState.LOADING -> LoadingScreen(viewModel)
        ScreenState.START_MENU -> StartMenuScreen(viewModel)
        ScreenState.GAME -> WaterSortScreen(viewModel)
        ScreenState.AUTH -> AuthScreen(viewModel)
    }
}
