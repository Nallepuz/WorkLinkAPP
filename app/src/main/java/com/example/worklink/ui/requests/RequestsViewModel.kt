package com.example.worklink.ui.requests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.model.Application as WorkApplication
import com.example.worklink.data.model.ApplicationRequest
import com.example.worklink.data.model.ApplicationType
import com.example.worklink.data.model.Turn
import com.example.worklink.data.model.User
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.ui.admin.ApplicationDetail
import com.example.worklink.utils.NotificationHelper
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Tipo de formulario segun solicitud
enum class RequestFormType { DAYS, HOURS, SHIFT_CHANGE }

data class RequestsUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val applications: List<WorkApplication> = emptyList(),
    val applicationTypes: List<ApplicationType> = emptyList(),
    val users: List<User> = emptyList(),
    val turns: List<Turn> = emptyList(),
    val showCreateSheet: Boolean = false,
    val showDetailSheet: Boolean = false,
    val selectedApplication: WorkApplication? = null,
    val selectedDetail: ApplicationDetail? = null,
    val selectedTypeId: Long? = null,
    val formType: RequestFormType = RequestFormType.DAYS,
    val comments: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val date: String = "",
    val fromTime: String = "",
    val toTime: String = "",
    val hoursRequested: String = "",
    val affectedUserId: String = "",
    val turnGiveId: String = "",
    val turnReceiveId: String = "",
    val selectedAffectedUser: User? = null,
    val selectedTurnGive: Turn? = null,
    val selectedTurnReceive: Turn? = null,
    val sendSuccess: Boolean = false,
    val error: String? = null
)

class RequestsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState: StateFlow<RequestsUiState> = _uiState

    init {
        loadData()
    }

    // Cargar datos solicitudes
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch

            val previousApplications = _uiState.value.applications

            val applicationsResult = repository.getApplicationsByUser(token, userId)
            val typesResult = repository.getApplicationTypes(token)
            val usersResult = repository.getUsers(token)
            val turnsResult = repository.getTurns(token)

            val newApplications = applicationsResult.getOrNull() ?: emptyList()

            newApplications.forEach { newApp ->
                val oldApp = previousApplications.find { it.id == newApp.id }
                if (oldApp != null && oldApp.status == "PENDING" && newApp.status != "PENDING") {
                    NotificationHelper.notifyRequestStatusChanged(
                        context = getApplication(),
                        requestId = newApp.id,
                        status = newApp.status
                    )
                }
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                applications = newApplications,
                applicationTypes = typesResult.getOrNull() ?: emptyList(),
                users = usersResult.getOrNull() ?: emptyList(),
                turns = turnsResult.getOrNull() ?: emptyList()
            )
        }
    }

    // Seleccionar solicitud
    fun selectApplication(application: WorkApplication) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedApplication = application,
                selectedDetail = null,
                isLoadingDetail = true,
                showDetailSheet = true
            )

            val token = sessionManager.token.first() ?: return@launch
            val types = _uiState.value.applicationTypes
            val typeName = types.find { it.id == application.applicationTypeId }?.name ?: ""

            val detail = when {
                typeName.contains("horas", ignoreCase = true) -> {
                    repository.getApplicationHours(token, application.id)
                        .getOrNull()?.let { ApplicationDetail.Hours(it) }
                }
                typeName.contains("turno", ignoreCase = true) -> {
                    repository.getApplicationChange(token, application.id)
                        .getOrNull()?.let { ApplicationDetail.Change(it) }
                }
                else -> {
                    repository.getApplicationDays(token, application.id)
                        .getOrNull()?.let { ApplicationDetail.Days(it) }
                }
            }

            _uiState.value = _uiState.value.copy(
                isLoadingDetail = false,
                selectedDetail = detail
            )
        }
    }

    // Limpia la solicitud y cierra el panel detalle
    fun clearSelectedApplication() {
        _uiState.value = _uiState.value.copy(
            selectedApplication = null,
            selectedDetail = null,
            showDetailSheet = false
        )
    }

    // Abrir panel para crear solicitud
    fun showCreateSheet() {
        _uiState.value = _uiState.value.copy(
            showCreateSheet = true,
            selectedTypeId = null,
            formType = RequestFormType.DAYS,
            comments = "",
            startDate = "",
            endDate = "",
            date = "",
            fromTime = "",
            toTime = "",
            hoursRequested = "",
            affectedUserId = "",
            turnGiveId = "",
            turnReceiveId = "",
            selectedAffectedUser = null,
            selectedTurnGive = null,
            selectedTurnReceive = null,
            sendSuccess = false,
            error = null
        )
    }

    // Cierra panel creacion solicitud
    fun hideCreateSheet() {
        _uiState.value = _uiState.value.copy(showCreateSheet = false, error = null)
    }

    // Guarda el tipo solicitado
    fun onTypeSelected(typeId: Long, typeName: String) {
        val formType = when {
            typeName.contains("horas", ignoreCase = true) -> RequestFormType.HOURS
            typeName.contains("turno", ignoreCase = true) -> RequestFormType.SHIFT_CHANGE
            else -> RequestFormType.DAYS
        }
        _uiState.value = _uiState.value.copy(selectedTypeId = typeId, formType = formType)
    }

    // Actualizar campos del formulario
    fun onCommentsChange(value: String) { _uiState.value = _uiState.value.copy(comments = value) }
    fun onStartDateChange(value: String) { _uiState.value = _uiState.value.copy(startDate = value) }
    fun onEndDateChange(value: String) { _uiState.value = _uiState.value.copy(endDate = value) }
    fun onDateChange(value: String) { _uiState.value = _uiState.value.copy(date = value) }
    fun onFromTimeChange(value: String) { _uiState.value = _uiState.value.copy(fromTime = value) }
    fun onToTimeChange(value: String) { _uiState.value = _uiState.value.copy(toTime = value) }
    fun onHoursRequestedChange(value: String) { _uiState.value = _uiState.value.copy(hoursRequested = value) }

    // Guardar usuario afectado
    fun onAffectedUserSelected(user: User) {
        _uiState.value = _uiState.value.copy(
            selectedAffectedUser = user,
            affectedUserId = user.id.toString()
        )
    }

    // Guardar turno que se da
    fun onTurnGiveSelected(turn: Turn) {
        _uiState.value = _uiState.value.copy(
            selectedTurnGive = turn,
            turnGiveId = turn.id.toString()
        )
    }

    // Guardar turno que se recibe
    fun onTurnReceiveSelected(turn: Turn) {
        _uiState.value = _uiState.value.copy(
            selectedTurnReceive = turn,
            turnReceiveId = turn.id.toString()
        )
    }

    // Enviar solicitud
    fun sendRequest() {
        val state = _uiState.value
        if (state.selectedTypeId == null) {
            _uiState.value = _uiState.value.copy(error = "Selecciona un tipo de solicitud")
            return
        }
        if (state.comments.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Añade un comentario")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch

            val request = when (state.formType) {
                RequestFormType.DAYS -> ApplicationRequest(
                    applicationTypeId = state.selectedTypeId,
                    userId = userId,
                    comments = state.comments,
                    startDate = state.startDate,
                    endDate = state.endDate
                )
                RequestFormType.HOURS -> ApplicationRequest(
                    applicationTypeId = state.selectedTypeId,
                    userId = userId,
                    comments = state.comments,
                    date = state.date,
                    fromTime = state.fromTime,
                    toTime = state.toTime,
                    hoursRequested = state.hoursRequested.toFloatOrNull()
                )
                RequestFormType.SHIFT_CHANGE -> ApplicationRequest(
                    applicationTypeId = state.selectedTypeId,
                    userId = userId,
                    comments = state.comments,
                    startDate = state.startDate,
                    endDate = state.endDate,
                    affectedUserId = state.affectedUserId.toLongOrNull(),
                    turnGiveId = state.turnGiveId.toLongOrNull(),
                    turnReceiveId = state.turnReceiveId.toLongOrNull()
                )
            }

            val result = repository.createApplication(token, request)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    showCreateSheet = false,
                    sendSuccess = true
                )
                loadData()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = "Error al enviar la solicitud"
                )
            }
        }
    }
}