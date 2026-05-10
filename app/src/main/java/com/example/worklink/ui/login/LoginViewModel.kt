package com.example.worklink.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    // Inicio de sesión
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email y contraseña obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            val loginResult = repository.login(email, password)

            if (loginResult.isFailure) {
                _uiState.value = LoginUiState.Error("Credenciales incorrectas")
                return@launch
            }

            val user = loginResult.getOrNull()!!

            sessionManager.saveSession(
                token = user.token,
                email = user.email,
                userId = user.id,
                name = user.name,
                roleId = user.roleId
            )

            _uiState.value = LoginUiState.Success
        }
    }
}