package com.example.worklink.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.model.User
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AccountUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val user: User? = null,
    val isEditing: Boolean = false,
    val editName: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    val editCurrentPassword: String = "",
    val editNewPassword: String = "",
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    init {
        loadUser()
    }

    // Carga los datos de usuario
    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val token = sessionManager.token.first() ?: return@launch
            val email = sessionManager.email.first() ?: return@launch
            val result = repository.getUserByEmail(token, email)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = user,
                    editName = user?.name ?: "",
                    editEmail = user?.email ?: "",
                    editPhone = user?.phone ?: ""
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar los datos"
                )
            }
        }
    }

    // Modo edición
    fun startEditing() {
        val user = _uiState.value.user ?: return
        _uiState.value = _uiState.value.copy(
            isEditing = true,
            editName = user.name,
            editEmail = user.email,
            editPhone = user.phone,
            editCurrentPassword = "",
            editNewPassword = "",
            saveSuccess = false,
            error = null
        )
    }

    // Cancelar edición
    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(
            isEditing = false,
            editCurrentPassword = "",
            editNewPassword = "",
            error = null
        )
    }

    // Actualizar campos formulario
    fun onNameChange(value: String) { _uiState.value = _uiState.value.copy(editName = value) }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(editEmail = value) }
    fun onPhoneChange(value: String) { _uiState.value = _uiState.value.copy(editPhone = value) }
    fun onCurrentPasswordChange(value: String) { _uiState.value = _uiState.value.copy(editCurrentPassword = value) }
    fun onNewPasswordChange(value: String) { _uiState.value = _uiState.value.copy(editNewPassword = value) }

    // Guardar cambios
    fun saveChanges() {
        val user = _uiState.value.user ?: return
        val state = _uiState.value

        if (state.editName.isBlank() || state.editEmail.isBlank() || state.editPhone.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Nombre, email y teléfono son obligatorios")
            return
        }

        if (state.editCurrentPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Introduce tu contraseña actual para guardar")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val token = sessionManager.token.first() ?: return@launch

            val passwordToSend = if (state.editNewPassword.isBlank())
                state.editCurrentPassword
            else
                state.editNewPassword

            val result = repository.updateUser(
                token = token,
                userId = user.id,
                name = state.editName,
                email = state.editEmail,
                phone = state.editPhone,
                password = passwordToSend,
                rolId = user.rolId
            )

            if (result.isSuccess) {
                val updatedUser = result.getOrNull()
                sessionManager.saveSession(
                    token = token,
                    email = updatedUser?.email ?: state.editEmail,
                    userId = user.id,
                    name = updatedUser?.name ?: state.editName,
                    roleId = user.rolId  // ← añade esto
                )
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isEditing = false,
                    saveSuccess = true,
                    user = updatedUser,
                    editCurrentPassword = "",
                    editNewPassword = ""
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error al guardar los cambios"
                )
            }
        }
    }
}