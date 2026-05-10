package com.example.worklink.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worklink.data.model.Application
import com.example.worklink.data.model.ApplicationType
import com.example.worklink.data.repository.WorkLinkRepository
import com.example.worklink.ui.theme.WorkLinkTheme
import com.example.worklink.utils.SessionManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = viewModel(),
    onNavigateToTurnManagement: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPendingApplications()
    }

    if (uiState.selectedApplication != null) {
        AdminDetailScreen(
            application = uiState.selectedApplication!!,
            detail = uiState.selectedDetail,
            isLoading = uiState.isLoadingDetail,
            applicationTypes = uiState.applicationTypes,
            onBack = { viewModel.clearSelection() },
            onApprove = { viewModel.resolveApplication(uiState.selectedApplication!!.id, true) },
            onReject = { viewModel.resolveApplication(uiState.selectedApplication!!.id, false) }
        )
    } else {
        AdminListScreen(
            pendingApplications = uiState.pendingApplications,
            applicationTypes = uiState.applicationTypes,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onSelectApplication = { viewModel.selectApplication(it) },
            onRefresh = { viewModel.loadPendingApplications() },
            onNavigateToTurnManagement = onNavigateToTurnManagement
        )
    }
}

// Solicitudes pendientes, gestión de turnos y anuncios
@Composable
fun AdminListScreen(
    pendingApplications: List<Application>,
    applicationTypes: List<ApplicationType>,
    isLoading: Boolean,
    error: String?,
    onSelectApplication: (Application) -> Unit,
    onRefresh: () -> Unit = {},
    onNavigateToTurnManagement: () -> Unit = {}
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val repository = remember { WorkLinkRepository() }

    var showAnnouncementDialog by remember { mutableStateOf(false) }
    var announcementText by remember { mutableStateOf("") }
    var isSendingAnnouncement by remember { mutableStateOf(false) }

    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A237E), Color(0xFF283593))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Panel de administración",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${pendingApplications.size} pendientes",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Botón gestión de turnos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(3.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                onClick = onNavigateToTurnManagement
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Gestión de turnos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Asignar, modificar o eliminar turnos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Botón enviar anuncio
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(3.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                onClick = { showAnnouncementDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFF9800).copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Enviar anuncio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Notificar a todos los empleados", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (pendingApplications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF4CAF50).copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "No hay solicitudes pendientes", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pendingApplications) { application ->
                        val typeName = applicationTypes.find { it.id == application.applicationTypeId }?.name ?: "Solicitud"
                        PendingApplicationCard(
                            application = application,
                            typeName = typeName,
                            onClick = { onSelectApplication(application) }
                        )
                    }
                }
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
        }
    }

    // Dialog de anuncio
    if (showAnnouncementDialog) {
        AlertDialog(
            onDismissRequest = {
                showAnnouncementDialog = false
                announcementText = ""
            },
            icon = { Icon(Icons.Default.Campaign, null, tint = Color(0xFFFF9800)) },
            title = { Text("Nuevo anuncio") },
            text = {
                Column {
                    Text(
                        text = "El mensaje se enviará a todos los empleados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = announcementText,
                        onValueChange = { announcementText = it },
                        label = { Text("Mensaje") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isSendingAnnouncement = true
                            val token = sessionManager.token.first()
                            if (token != null) {
                                repository.createAnnouncement(token, announcementText)
                            }
                            isSendingAnnouncement = false
                            showAnnouncementDialog = false
                            announcementText = ""
                        }
                    },
                    enabled = announcementText.isNotBlank() && !isSendingAnnouncement,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    if (isSendingAnnouncement) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Enviar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAnnouncementDialog = false
                    announcementText = ""
                }) { Text("Cancelar") }
            }
        )
    }
}

// Tarjeta solicitud
@Composable
fun PendingApplicationCard(application: Application, typeName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFFF9800)))
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).background(Color(0xFFFF9800).copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.HourglassEmpty, null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = typeName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = application.userName ?: "Usuario #${application.userId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = application.comments, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = application.created.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// Detalle de solicitud
@Composable
fun AdminDetailScreen(
    application: Application,
    detail: ApplicationDetail?,
    isLoading: Boolean,
    applicationTypes: List<ApplicationType>,
    onBack: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    val typeName = applicationTypes.find { it.id == application.applicationTypeId }?.name ?: "Solicitud"

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(brush = Brush.verticalGradient(colors = listOf(Color(0xFF1A237E), Color(0xFF283593))))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = typeName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Solicitud #${application.id}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Información general", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow("Usuario", application.userName ?: "ID: ${application.userId}")
                        application.affectedUserName?.let { DetailRow("Compañero afectado", it) }
                        DetailRow("Fecha solicitud", application.created.take(10))
                        DetailRow("Comentario", application.comments)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (detail) {
                    is ApplicationDetail.Days -> {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(3.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Detalle de días", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRow("Fecha inicio", detail.detail.startDate)
                                DetailRow("Fecha fin", detail.detail.endDate)
                            }
                        }
                    }
                    is ApplicationDetail.Hours -> {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(3.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Detalle de horas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRow("Fecha", detail.detail.date)
                                DetailRow("Hora inicio", detail.detail.fromTime)
                                DetailRow("Hora fin", detail.detail.toTime)
                                DetailRow("Horas solicitadas", "${detail.detail.hoursRequested}h")
                            }
                        }
                    }
                    is ApplicationDetail.Change -> {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(3.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Detalle cambio de turno", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRow("Fecha inicio", detail.detail.startDate)
                                DetailRow("Fecha fin", detail.detail.endDate)
                                DetailRow("Compañero afectado", application.affectedUserName ?: "ID: ${detail.detail.affectedUserId}")
                                DetailRow("Turno que cede", "ID: ${detail.detail.turnGiveId}")
                                DetailRow("Turno que recibe", "ID: ${detail.detail.turnReceiveId}")
                            }
                        }
                    }
                    null -> {
                        if (!isLoading) {
                            Text(text = "No se pudo cargar el detalle", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { showRejectDialog = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rechazar", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onApprove,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Aprobar", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Rechazar solicitud") },
            text = { Text("¿Seguro que quieres rechazar la solicitud #${application.id}?") },
            confirmButton = {
                Button(
                    onClick = { onReject(); showRejectDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Rechazar") }
            },
            dismissButton = { TextButton(onClick = { showRejectDialog = false }) { Text("Cancelar") } }
        )
    }
}

// Mostrar detalle con etiqueta y valor
@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

// Previsualización
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    val fakeApplications = listOf(
        Application(1, "PENDING", 1, 2, "2026-05-01T10:00:00", "Necesito vacaciones urgentes", null),
        Application(2, "PENDING", 4, 3, "2026-05-02T09:00:00", "Cambio de turno con compañero", null),
    )
    val fakeTypes = listOf(
        ApplicationType(1, "Vacaciones", "Vacaciones pagadas", true, true),
        ApplicationType(4, "Bolsa de horas", "Horas libres pagadas", true, true),
        ApplicationType(5, "Cambio de turno", "Cambio de turno", false, true),
    )
    WorkLinkTheme {
        AdminListScreen(
            pendingApplications = fakeApplications,
            applicationTypes = fakeTypes,
            isLoading = false,
            error = null,
            onSelectApplication = {}
        )
    }
}