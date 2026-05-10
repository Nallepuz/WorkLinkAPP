package com.example.worklink.ui.balance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.model.UserBalance
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

data class BalanceUiState(
    val isLoading: Boolean = false,
    val balance: UserBalance? = null,
    val userName: String = "",
    val error: String? = null
)

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState

    init {
        loadBalance()
    }

    // Cargar balance de usuario
    fun loadBalance() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch
            val userName = sessionManager.userName.first() ?: ""

            val result = repository.getUserBalance(token, userId)

            if (result.isSuccess) {
                val balances = result.getOrNull() ?: emptyList()
                val currentYear = LocalDate.now().year
                val balance = balances.firstOrNull { it.year == currentYear }
                    ?: balances.firstOrNull()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    balance = balance,
                    userName = userName
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar el balance"
                )
            }
        }
    }
}