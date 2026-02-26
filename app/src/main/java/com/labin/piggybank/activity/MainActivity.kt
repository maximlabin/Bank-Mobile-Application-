package com.labin.piggybank.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labin.piggybank.compose.PiggyBankApp
import com.labin.piggybank.ui.theme.Theme
import com.labin.piggybank.viewmodels.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Если ThemeViewModel имеет аннотацию @HiltViewModel, Hilt сам создаст фабрику    private val viewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: ThemeViewModel = hiltViewModel()

            val themeMode by viewModel.themeModeFlow.collectAsStateWithLifecycle()

            Theme(themeMode = themeMode) {
                PiggyBankApp()
            }
        }
    }
}