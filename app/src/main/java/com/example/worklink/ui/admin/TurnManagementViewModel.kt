package com.example.worklink.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.model.Turn
import com.example.worklink.data.model.TurnAssigned
import com.example.worklink.data.model.User
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TurnManagementUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val turns: List<Turn> = emptyList(),
    val selectedUser: User? = null,
    val assignedTurns: List<TurnAssigned> = emptyList(),
    val selectedAssigned: TurnAssigned? = null,
    val showAssignSheet: Boolean = false,
    val showEditSheet: Boolean = false,
    val selectedDate: String = "",
    val selectedTurnId: Long? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

class TurnManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(TurnManagementUiState())
    val uiState: StateFlow<TurnManagementUiState> = _uiState

    init {
        loadInitialData()
    }

    // Obtener lista de usuarios y turnos
    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val token = sessionManager.token.first() ?: return@launch

            val usersResult = repository.getUsers(token)
            val turnsResult = repository.getTurns(token)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                users = usersResult.getOrNull() ?: emptyList(),
                turns = turnsResult.getOrNull() ?: emptyList()
            )
        }
    }

    //Seleccionar usuario
    fun selectUser(user: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedUser = user,
                isLoading = true
            )
            val token = sessionManager.token.first() ?: return@launch
            val assignedResult = repository.getAssignedTurns(token, user.id)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                assignedTurns = assignedResult.getOrNull() ?: emptyList()
            )
        }
    }

    // Abrir panel de asignacion de turno en fecha sin turno
    fun openAssignSheet(date: String) {
        _uiState.value = _uiState.value.copy(
            showAssignSheet = true,
            selectedDate = date,
            selectedTurnId = null,
            error = null
        )
    }

    // Abrir panel para modificar turno asignado
    fun openEditSheet(assigned: TurnAssigned) {
        _uiState.value = _uiState.value.copy(
            showEditSheet = true,
            selectedAssigned = assigned,
            selectedDate = assigned.date,
            selectedTurnId = assigned.turnId,
            error = null
        )
    }

    // Cerrar panel
    fun closeSheets() {
        _uiState.value = _uiState.value.copy(
            showAssignSheet = false,
            showEditSheet = false,
            selectedAssigned = null,
            selectedDate = "",
            selectedTurnId = null,
            error = null
        )
    }

    // Guardar tunro
    fun onTurnSelected(turnId: Long) {
        _uiState.value = _uiState.value.copy(selectedTurnId = turnId)
    }

    // Asignar turno
    fun assignTurn() {
        viewModelScope.launch {
            val state = _uiState.value
            val user = state.selectedUser ?: return@launch
            val turnId = state.selectedTurnId ?: run {
                _uiState.value = _uiState.value.copy(error = "Selecciona un turno")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isSubmitting = true)
            val token = sessionManager.token.first() ?: return@launch

            val result = repository.createAssigned(token, user.id, turnId, state.selectedDate)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    showAssignSheet = false,
                    success = "Turno asignado correctamente"
                )
                selectUser(user)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = result.exceptionOrNull()?.message ?: "Error al asignar turno"
                )
            }
        }
    }

    // Modificar turno
    fun updateTurn() {
        viewModelScope.launch {
            val state = _uiState.value
            val user = state.selectedUser ?: return@launch
            val assigned = state.selectedAssigned ?: return@launch
            val turnId = state.selectedTurnId ?: run {
                _uiState.value = _uiState.value.copy(error = "Selecciona un turno")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isSubmitting = true)
            val token = sessionManager.token.first() ?: return@launch

            val result = repository.updateAssigned(token, assigned.id, user.id, turnId, state.selectedDate)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    showEditSheet = false,
                    success = "Turno modificado correctamente"
                )
                selectUser(user)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = result.exceptionOrNull()?.message ?: "Error al modificar turno"
                )
            }
        }
    }

    // Eliminar turno
    fun deleteTurn(assignedId: Long) {
        viewModelScope.launch {
            val state = _uiState.value
            val user = state.selectedUser ?: return@launch

            _uiState.value = _uiState.value.copy(isSubmitting = true)
            val token = sessionManager.token.first() ?: return@launch

            val result = repository.deleteAssigned(token, assignedId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    showEditSheet = false,
                    success = "Turno eliminado correctamente"
                )
                selectUser(user)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = result.exceptionOrNull()?.message ?: "Error al eliminar turno"
                )
            }
        }
    }

    // Limpiar mensaje de error/éxito
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, success = null)
    }
}