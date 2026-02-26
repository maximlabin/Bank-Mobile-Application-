package com.labin.piggybank.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labin.piggybank.data.PinCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val pinCodeRepository: PinCodeRepository
) : ViewModel() {

    val uiState = LoginUiState()

    val hasPinCode = pinCodeRepository.hasPinCode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private var _errorMessage: String? = null
    val errorMessage: String?
    get() = _errorMessage

    fun onPinEntered(pin: String) {
        viewModelScope.launch {
            if (pinCodeRepository.verifyPin(pin)) {
                // Успешный вход
                onLoginSuccess()
            } else {
                _errorMessage = "Неверный код"
            }
        }
    }

    fun onLoginSuccess() {
        // Здесь вы вызываете callback в Activity/Composable
        // Например, через callback или Navigation
    }
}

data class LoginUiState(
    val errorMessage: String? = null,
    val canResend: Boolean = false
)