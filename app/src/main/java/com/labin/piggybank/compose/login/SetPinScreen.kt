package com.labin.piggybank.compose.login
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SetPinScreen(
    title: String = "Создайте код",
    onPinSet: (String) -> Unit,
    modifier: Modifier = Modifier,
    confirmMode: Boolean = false
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        VerificationCodeInput(
            codeLength = 6,
            onCodeCompleted = { enteredCode ->
                if (enteredCode.length == 6) {
                    if (confirmMode) {
                        onPinSet(enteredCode)
                    } else {
                        // Переходим к подтверждению
                        // Обычно делают два шага: ввод → подтверждение
                        // Но для простоты можно сохранить сразу
                        onPinSet(enteredCode)
                    }
                }
            },
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}