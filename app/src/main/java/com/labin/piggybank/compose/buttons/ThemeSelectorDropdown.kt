package com.labin.piggybank.compose.buttons

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labin.piggybank.ui.theme.ThemeMode
import com.labin.piggybank.viewmodels.ThemeViewModel

@Composable
fun ThemeSegmentedButtons(viewModel: ThemeViewModel) {
    val themeMode by viewModel.themeModeFlow.collectAsStateWithLifecycle()

    ThemeSegmentedButtonsContent(
        selectedMode = themeMode,
        onModeSelected = { newMode -> viewModel.setTheme(newMode) }
    )
}

//@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSegmentedButtonsContent(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    val options = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, mode ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onModeSelected(mode) },
                selected = selectedMode == mode
            ) {
                Text(
                    text = when (mode) {
                        ThemeMode.SYSTEM -> "Auto"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    }
                )
            }
        }
    }
}