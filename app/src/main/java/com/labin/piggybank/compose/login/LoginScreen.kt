package com.labin.piggybank.compose.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.labin.piggybank.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val hasPin by viewModel.hasPinCode.collectAsState()
    val errorMessage = viewModel.errorMessage
    var currentStep by remember { mutableStateOf(0) }
    var firstPin by remember { mutableStateOf("") }

//    if (!hasPin) {
//        SetPinScreen { pin ->
//            viewModel.savePin(pin)
//        }
//        return
//    }

//    when (currentStep) {
//        0 -> {
//            SetPinScreen(
//                title = "Создайте код доступа",
//                onPinSet = { pin ->
//                    firstPin = pin
//                    currentStep = 1
//                    Log.d("onPinSet", "set0")
//                }
//            )
//        }
//        1 -> {
//            SetPinScreen(
//                title = "Подтвердите код доступа",
//                confirmMode = true,
//                onPinSet = { pin ->
//                    Log.d("onPinSet", "set1")
//                    if (pin == firstPin) {
//                        // onPinConfirmed(pin)
//                    } else {
//                        // Показать ошибку: коды не совпадают
//                        // Для этого нужно добавить обработку ошибок в SetPinScreen
//                    }
//                }
//            )
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Введите код",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        VerificationCodeInput(
            codeLength = 6,
            onCodeCompleted = { code ->
                viewModel.onPinEntered(code)
            }
        )
    }
}