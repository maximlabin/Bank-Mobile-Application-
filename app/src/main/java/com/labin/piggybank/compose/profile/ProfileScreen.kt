package com.labin.piggybank.compose.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.labin.piggybank.compose.buttons.ThemeSegmentedButtons
import com.labin.piggybank.compose.buttons.ThemeSegmentedButtonsContent
import com.labin.piggybank.ui.theme.ThemeMode
import com.labin.piggybank.viewmodels.ThemeViewModel

@Composable
fun ProfileScreen(
    userId: String,
    navController: NavController,
    viewModel: ThemeViewModel = hiltViewModel()
) {
    // Подписываемся на состояние темы из ViewModel
    val themeMode by viewModel.themeModeFlow.collectAsStateWithLifecycle()

    ProfileScreenContent(
        userId = userId,
        navController = navController,
        currentThemeMode = themeMode, // Передаем состояние
        onThemeChanged = { newMode -> viewModel.setTheme(newMode) } // Передаем действие
    )
}

@Composable
fun ProfileScreenContent(
    userId: String,
    navController: NavController,
    currentThemeMode: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Профиль пользователя: $userId",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Выберите тему:",
            style = MaterialTheme.typography.titleMedium
        )

        ThemeSegmentedButtonsContent(
            selectedMode = currentThemeMode,
            onModeSelected = onThemeChanged
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Перейти на главный экран")
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreenContent(
        userId = "3",
        navController = rememberNavController(),
        currentThemeMode = ThemeMode.SYSTEM, // Просто передаем значение
        onThemeChanged = {} // Пустая функция для превью
    )
}
