package com.example.worklink.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.model.Application as WorkApplication
import com.example.worklink.data.model.ApplicationHoursDetail
import com.example.worklink.data.model.Turn
import com.example.worklink.data.model.TurnAssigned
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CalendarUiState(
    val isLoading: Boolean = false,
    val assignedTurns: List<TurnAssigned> = emptyList(),
    val turns: List<Turn> = emptyList(),
    val selectedTurn: TurnAssigned? = null,
    val selectedTurnDetail: Turn? = null,
    val selectedHoursDetail: ApplicationHoursDetail? = null,
    val userName: String = "",
    val error: String? = null,
    val approvedHoursDates: Set<String> = emptySet(),
    val hoursDetailByDate: Map<String, ApplicationHoursDetail> = emptyMap()
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        loadData()
    }

    // Cargar los turnos asignados, turnos y solicitudes
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch
            val userName = sessionManager.userName.first() ?: ""

            val assignedResult = repository.getAssignedTurns(token, userId)
            val turnsResult = repository.getTurns(token)
            val applicationsResult = repository.getApplicationsByUser(token, userId)

            val approvedHoursDates = mutableSetOf<String>()
            val hoursDetailByDate = mutableMapOf<String, ApplicationHoursDetail>()

            val applications = applicationsResult.getOrNull() ?: emptyList()
            val hoursApplications = applications.filter { it.status == "APPROVED" }

            hoursApplications.forEach { app ->
                val detail = repository.getApplicationHours(token, app.id).getOrNull()
                detail?.let {
                    approvedHoursDates.add(it.date)
                    hoursDetailByDate[it.date] = it
                }
            }

            if (assignedResult.isSuccess && turnsResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignedTurns = assignedResult.getOrNull() ?: emptyList(),
                    turns = turnsResult.getOrNull() ?: emptyList(),
                    userName = userName,
                    approvedHoursDates = approvedHoursDates,
                    hoursDetailByDate = hoursDetailByDate
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar los turnos"
                )
            }
        }
    }

    // Seleccionar dia del calendario y mostrar detalles
    fun selectDay(turnAssigned: TurnAssigned?) {
        val turnDetail = turnAssigned?.let { assigned ->
            _uiState.value.turns.find { it.id == assigned.turnId }
        }
        val hoursDetail = turnAssigned?.let {
            _uiState.value.hoursDetailByDate[it.date]
        }
        _uiState.value = _uiState.value.copy(
            selectedTurn = turnAssigned,
            selectedTurnDetail = turnDetail,
            selectedHoursDetail = hoursDetail
        )
    }
}