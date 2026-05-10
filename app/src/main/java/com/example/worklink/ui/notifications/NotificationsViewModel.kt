package com.example.worklink.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worklink.data.model.Notification
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Long = 0L,
    val error: String? = null
)

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkLinkRepository()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init {
        loadNotifications()
    }

    // Cargar notificaciones
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch

            val notificationsResult = repository.getNotifications(token, userId)
            val countResult = repository.getUnreadCount(token, userId)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                notifications = notificationsResult.getOrNull() ?: emptyList(),
                unreadCount = countResult.getOrNull() ?: 0L
            )
        }
    }

    // Marcar todas como leídas
    fun markAllAsRead() {
        viewModelScope.launch {
            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch
            repository.markAllAsRead(token, userId)
            _uiState.value = _uiState.value.copy(unreadCount = 0L)
            loadNotifications()
        }
    }

    // Eliminar notificaciones
    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            val token = sessionManager.token.first() ?: return@launch
            repository.deleteNotificationById(token, id)
            loadNotifications()
        }
    }

    // Eliminar notificaciones leídas
    fun deleteAllRead() {
        viewModelScope.launch {
            val token = sessionManager.token.first() ?: return@launch
            val userId = sessionManager.userId.first() ?: return@launch
            repository.deleteAllReadNotifications(token, userId)
            loadNotifications()
        }
    }
}