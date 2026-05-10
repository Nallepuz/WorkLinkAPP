package com.example.worklink.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.model.Application as WorkApplication
import com.example.worklink.data.model.ApplicationChangeDetail
import com.example.worklink.data.model.ApplicationDaysDetail
import com.example.worklink.data.model.ApplicationHoursDetail
import com.example.worklink.data.model.ApplicationType
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.utils.NotificationHelper
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Distintos tipos de detalles segun la solicitud
sealed class ApplicationDetail {
    data class Days(val detail: ApplicationDaysDetail) : ApplicationDetail()
    data class Hours(val detail: ApplicationHoursDetail) : ApplicationDetail()
    data class Change(val detail: ApplicationChangeDetail) : ApplicationDetail()
}

// Estado general
// guardar solicitudes
data class AdminUiState(
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val pendingApplications: List<WorkApplication> = emptyList(),
    val applicationTypes: List<ApplicationType> = emptyList(),
    val selectedApplication: WorkApplication? = null,
    val selectedDetail: ApplicationDetail? = null,
    val error: String? = null
)

//Vista pantalla Admin
class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState

    init {
        loadPendingApplications()
    }

    // Cargar solicitudes
    fun loadPendingApplications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val token = sessionManager.token.first() ?: return@launch

            val previousPending = _uiState.value.pendingApplications

            val applicationsResult = repository.getAllApplications(token)
            val typesResult = repository.getApplicationTypes(token)

            val types = typesResult.getOrNull() ?: emptyList()

            val pending = applicationsResult.getOrNull()
                ?.filter { it.status == "PENDING" }
                ?: emptyList()


            val newPending = pending.filter { newApp ->
                previousPending.none { it.id == newApp.id }
            }
            newPending.forEach { app ->
                val typeName = types.find { it.id == app.applicationTypeId }?.name ?: "Solicitud"
                NotificationHelper.notifyAdminNewRequest(
                    context = getApplication(),
                    requestId = app.id,
                    typeName = typeName
                )
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                pendingApplications = pending,
                applicationTypes = types
            )
        }
    }

    // Seleccionar solicitus y cargar sus campos
    fun selectApplication(application: WorkApplication) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedApplication = application,
                selectedDetail = null,
                isLoadingDetail = true
            )

            val token = sessionManager.token.first() ?: return@launch
            val typeId = application.applicationTypeId

            val types = _uiState.value.applicationTypes
            val typeName = types.find { it.id == typeId }?.name ?: ""

            val detail: ApplicationDetail? = when {
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

    // Limpiar solicitud
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedApplication = null,
            selectedDetail = null
        )
    }

    // Aprobar o rechazar solicitud
    fun resolveApplication(applicationId: Long, approved: Boolean) {
        viewModelScope.launch {
            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch

            val result = repository.resolveApplication(
                token = token,
                applicationId = applicationId,
                approved = approved,
                resolverId = userId
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    selectedApplication = null,
                    selectedDetail = null
                )
                loadPendingApplications()
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Error al resolver la solicitud"
                )
            }
        }
    }
}