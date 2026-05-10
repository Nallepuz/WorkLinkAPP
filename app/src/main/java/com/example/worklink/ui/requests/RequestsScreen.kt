package com.example.worklink.ui.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worklink.data.model.Application
import com.example.worklink.data.model.ApplicationType
import com.example.worklink.data.model.Turn
import com.example.worklink.data.model.User
import com.example.worklink.ui.admin.ApplicationDetail
import com.example.worklink.ui.calendar.parseColor
import com.example.worklink.ui.theme.WorkLinkTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(viewModel: RequestsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    RequestsScreenContent(
        applications = uiState.applications,
        applicationTypes = uiState.applicationTypes,
        users = uiState.users,
        turns = uiState.turns,
        isLoading = uiState.isLoading,
        isSending = uiState.isSending,
        isLoadingDetail = uiState.isLoadingDetail,
        showCreateSheet = uiState.showCreateSheet,
        showDetailSheet = uiState.showDetailSheet,
        selectedApplication = uiState.selectedApplication,
        selectedDetail = uiState.selectedDetail,
        selectedTypeId = uiState.selectedTypeId,
        formType = uiState.formType,
        comments = uiState.comments,
        startDate = uiState.startDate,
        endDate = uiState.endDate,
        date = uiState.date,
        fromTime = uiState.fromTime,
        toTime = uiState.toTime,
        hoursRequested = uiState.hoursRequested,
        selectedAffectedUser = uiState.selectedAffectedUser,
        selectedTurnGive = uiState.selectedTurnGive,
        selectedTurnReceive = uiState.selectedTurnReceive,
        sendSuccess = uiState.sendSuccess,
        error = uiState.error,
        onSelectApplication = { viewModel.selectApplication(it) },
        onClearSelectedApplication = { viewModel.clearSelectedApplication() },
        onShowCreateSheet = { viewModel.showCreateSheet() },
        onHideCreateSheet = { viewModel.hideCreateSheet() },
        onTypeSelected = { id, name -> viewModel.onTypeSelected(id, name) },
        onCommentsChange = { viewModel.onCommentsChange(it) },
        onStartDateChange = { viewModel.onStartDateChange(it) },
        onEndDateChange = { viewModel.onEndDateChange(it) },
        onDateChange = { viewModel.onDateChange(it) },
        onFromTimeChange = { viewModel.onFromTimeChange(it) },
        onToTimeChange = { viewModel.onToTimeChange(it) },
        onHoursRequestedChange = { viewModel.onHoursRequestedChange(it) },
        onAffectedUserSelected = { viewModel.onAffectedUserSelected(it) },
        onTurnGiveSelected = { viewModel.onTurnGiveSelected(it) },
        onTurnReceiveSelected = { viewModel.onTurnReceiveSelected(it) },
        onSendRequest = { viewModel.sendRequest() }
    )
}

// Mostrar lista solicitudes, botones y paneles
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreenContent(
    applications: List<Application>,
    applicationTypes: List<ApplicationType>,
    users: List<User>,
    turns: List<Turn>,
    isLoading: Boolean,
    isSending: Boolean,
    isLoadingDetail: Boolean,
    showCreateSheet: Boolean,
    showDetailSheet: Boolean,
    selectedApplication: Application?,
    selectedDetail: ApplicationDetail?,
    selectedTypeId: Long?,
    formType: RequestFormType,
    comments: String,
    startDate: String,
    endDate: String,
    date: String,
    fromTime: String,
    toTime: String,
    hoursRequested: String,
    selectedAffectedUser: User?,
    selectedTurnGive: Turn?,
    selectedTurnReceive: Turn?,
    sendSuccess: Boolean,
    error: String?,
    onSelectApplication: (Application) -> Unit,
    onClearSelectedApplication: () -> Unit,
    onShowCreateSheet: () -> Unit,
    onHideCreateSheet: () -> Unit,
    onTypeSelected: (Long, String) -> Unit,
    onCommentsChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onFromTimeChange: (String) -> Unit,
    onToTimeChange: (String) -> Unit,
    onHoursRequestedChange: (String) -> Unit,
    onAffectedUserSelected: (User) -> Unit,
    onTurnGiveSelected: (Turn) -> Unit,
    onTurnReceiveSelected: (Turn) -> Unit,
    onSendRequest: () -> Unit
) {
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Mis solicitudes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${applications.size} solicitudes",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (applications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "No tienes solicitudes", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Pulsa + para crear una nueva", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(applications) { application ->
                        val typeName = applicationTypes.find { it.id == application.applicationTypeId }?.name ?: ""
                        ApplicationCard(
                            application = application,
                            typeName = typeName,
                            onClick = { onSelectApplication(application) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onShowCreateSheet,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nueva solicitud")
        }

        // Sheet de detalle
        if (showDetailSheet && selectedApplication != null) {
            ModalBottomSheet(
                onDismissRequest = onClearSelectedApplication,
                sheetState = detailSheetState
            ) {
                ApplicationDetailSheet(
                    application = selectedApplication,
                    detail = selectedDetail,
                    isLoading = isLoadingDetail,
                    applicationTypes = applicationTypes,
                    onDismiss = onClearSelectedApplication
                )
            }
        }

        // Sheet de crear
        if (showCreateSheet) {
            ModalBottomSheet(
                onDismissRequest = onHideCreateSheet,
                sheetState = createSheetState
            ) {
                CreateRequestSheet(
                    applicationTypes = applicationTypes,
                    users = users,
                    turns = turns,
                    selectedTypeId = selectedTypeId,
                    formType = formType,
                    comments = comments,
                    startDate = startDate,
                    endDate = endDate,
                    date = date,
                    fromTime = fromTime,
                    toTime = toTime,
                    hoursRequested = hoursRequested,
                    selectedAffectedUser = selectedAffectedUser,
                    selectedTurnGive = selectedTurnGive,
                    selectedTurnReceive = selectedTurnReceive,
                    isSending = isSending,
                    error = error,
                    onTypeSelected = onTypeSelected,
                    onCommentsChange = onCommentsChange,
                    onStartDateChange = onStartDateChange,
                    onEndDateChange = onEndDateChange,
                    onDateChange = onDateChange,
                    onFromTimeChange = onFromTimeChange,
                    onToTimeChange = onToTimeChange,
                    onHoursRequestedChange = onHoursRequestedChange,
                    onAffectedUserSelected = onAffectedUserSelected,
                    onTurnGiveSelected = onTurnGiveSelected,
                    onTurnReceiveSelected = onTurnReceiveSelected,
                    onSendRequest = onSendRequest,
                    onCancel = onHideCreateSheet
                )
            }
        }
    }
}

// Mostrar info general, estado y detalle del tipo de solicitud
@Composable
fun ApplicationDetailSheet(
    application: Application,
    detail: ApplicationDetail?,
    isLoading: Boolean,
    applicationTypes: List<ApplicationType>,
    onDismiss: () -> Unit
) {
    val statusColor = when (application.status) {
        "APPROVED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }
    val statusText = when (application.status) {
        "APPROVED" -> "Aprobada"
        "REJECTED" -> "Rechazada"
        else -> "Pendiente"
    }
    val typeName = applicationTypes.find { it.id == application.applicationTypeId }?.name ?: "Solicitud"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = typeName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Solicitud #${application.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.15f)) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(3.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Información general", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                RequestDetailRow("Fecha solicitud", application.created.take(10))
                RequestDetailRow("Comentario", application.comments)
                application.resolved?.let {
                    RequestDetailRow("Fecha resolución", it.take(10))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            when (detail) {
                is ApplicationDetail.Days -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(3.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Detalle", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            RequestDetailRow("Fecha inicio", detail.detail.startDate)
                            RequestDetailRow("Fecha fin", detail.detail.endDate)
                            detail.detail.resolverComments?.let {
                                if (it.isNotBlank()) RequestDetailRow("Comentario resolución", it)
                            }
                        }
                    }
                }
                is ApplicationDetail.Hours -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(3.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Detalle de horas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            RequestDetailRow("Fecha", detail.detail.date)
                            RequestDetailRow("Hora inicio", detail.detail.fromTime)
                            RequestDetailRow("Hora fin", detail.detail.toTime)
                            RequestDetailRow("Horas solicitadas", "${detail.detail.hoursRequested}h")
                            detail.detail.resolverComments?.let {
                                if (it.isNotBlank()) RequestDetailRow("Comentario resolución", it)
                            }
                        }
                    }
                }
                is ApplicationDetail.Change -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(3.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Detalle cambio de turno", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            RequestDetailRow("Fecha inicio", detail.detail.startDate)
                            RequestDetailRow("Fecha fin", detail.detail.endDate)
                            RequestDetailRow("Compañero afectado", "ID: ${detail.detail.affectedUserId}")
                            detail.detail.resolverComments?.let {
                                if (it.isNotBlank()) RequestDetailRow("Comentario resolución", it)
                            }
                        }
                    }
                }
                null -> {
                    Text(
                        text = "No se pudo cargar el detalle",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Fila para mostrar dato del detalle
@Composable
fun RequestDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

// Tarjeta solicitud dentro de la lista
@Composable
fun ApplicationCard(
    application: Application,
    typeName: String = "",
    onClick: () -> Unit = {}
) {
    val statusColor = when (application.status) {
        "APPROVED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }
    val statusText = when (application.status) {
        "APPROVED" -> "Aprobada"
        "REJECTED" -> "Rechazada"
        else -> "Pendiente"
    }
    val statusIcon = when (application.status) {
        "APPROVED" -> Icons.Default.CheckCircle
        "REJECTED" -> Icons.Default.Cancel
        else -> Icons.Default.HourglassEmpty
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(statusColor))
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp).background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (typeName.isNotEmpty()) typeName else "Solicitud #${application.id}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = application.comments, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = application.created.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.15f)) {
                        Text(text = statusText, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = statusColor)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// SSeleccionar fechas YYYY-MM-DD
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(label: String, value: String, onDateSelected: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        placeholder = { Text("Seleccionar fecha") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        onDateSelected(date.format(formatter))
                    }
                    showPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState, showModeToggle = false, title = null, headline = null)
        }
    }
}

// Seleccionar compañero al que cambiar turno
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelectorField(label: String, selectedUser: User?, users: List<User>, onUserSelected: (User) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedUser?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Seleccionar compañero") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            users.forEach { user ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(user.name, style = MaterialTheme.typography.bodyMedium)
                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    onClick = { onUserSelected(user); expanded = false }
                )
            }
        }
    }
}

// Seleccionar turno
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurnSelectorField(label: String, selectedTurn: Turn?, turns: List<Turn>, onTurnSelected: (Turn) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedTurn?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Seleccionar turno") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            turns.forEach { turn ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).background(parseColor(turn.colorHex), RoundedCornerShape(3.dp)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(turn.name, style = MaterialTheme.typography.bodyMedium)
                                if (turn.workStart != null && turn.workEnd != null) {
                                    Text("${turn.workStart} - ${turn.workEnd}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    },
                    onClick = { onTurnSelected(turn); expanded = false }
                )
            }
        }
    }
}

// Crear la solicitud
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestSheet(
    applicationTypes: List<ApplicationType>,
    users: List<User>,
    turns: List<Turn>,
    selectedTypeId: Long?,
    formType: RequestFormType,
    comments: String,
    startDate: String,
    endDate: String,
    date: String,
    fromTime: String,
    toTime: String,
    hoursRequested: String,
    selectedAffectedUser: User?,
    selectedTurnGive: Turn?,
    selectedTurnReceive: Turn?,
    isSending: Boolean,
    error: String?,
    onTypeSelected: (Long, String) -> Unit,
    onCommentsChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onFromTimeChange: (String) -> Unit,
    onToTimeChange: (String) -> Unit,
    onHoursRequestedChange: (String) -> Unit,
    onAffectedUserSelected: (User) -> Unit,
    onTurnGiveSelected: (Turn) -> Unit,
    onTurnReceiveSelected: (Turn) -> Unit,
    onSendRequest: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier.width(40.dp).height(4.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Nueva solicitud", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Tipo de solicitud", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        applicationTypes.forEach { type ->
            val isSelected = selectedTypeId == type.id
            Surface(
                onClick = { onTypeSelected(type.id, type.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onTypeSelected(type.id, type.name) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = type.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = type.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (selectedTypeId != null) {
            Spacer(modifier = Modifier.height(16.dp))
            when (formType) {
                RequestFormType.DAYS -> {
                    Text(text = "Fechas", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(label = "Fecha inicio", value = startDate, onDateSelected = onStartDateChange)
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(label = "Fecha fin", value = endDate, onDateSelected = onEndDateChange)
                }
                RequestFormType.HOURS -> {
                    Text(text = "Datos de la solicitud", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(label = "Fecha", value = date, onDateSelected = onDateChange)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = fromTime, onValueChange = onFromTimeChange, label = { Text("Hora inicio") }, placeholder = { Text("08:00:00") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = toTime, onValueChange = onToTimeChange, label = { Text("Hora fin") }, placeholder = { Text("10:00:00") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = hoursRequested, onValueChange = onHoursRequestedChange, label = { Text("Horas solicitadas") }, placeholder = { Text("2.0") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
                RequestFormType.SHIFT_CHANGE -> {
                    Text(text = "Datos del cambio de turno", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(label = "Fecha inicio", value = startDate, onDateSelected = onStartDateChange)
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(label = "Fecha fin", value = endDate, onDateSelected = onEndDateChange)
                    Spacer(modifier = Modifier.height(8.dp))
                    UserSelectorField(label = "Compañero", selectedUser = selectedAffectedUser, users = users, onUserSelected = onAffectedUserSelected)
                    Spacer(modifier = Modifier.height(8.dp))
                    TurnSelectorField(label = "Turno que cedes", selectedTurn = selectedTurnGive, turns = turns, onTurnSelected = onTurnGiveSelected)
                    Spacer(modifier = Modifier.height(8.dp))
                    TurnSelectorField(label = "Turno que recibes", selectedTurn = selectedTurnReceive, turns = turns, onTurnSelected = onTurnReceiveSelected)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Comentarios", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = comments, onValueChange = onCommentsChange, label = { Text("Escribe tu comentario") }, minLines = 3, maxLines = 5, modifier = Modifier.fillMaxWidth())
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Cancelar") }
            Button(onClick = onSendRequest, enabled = !isSending, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                if (isSending) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Enviar")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Previsualización
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RequestsScreenPreview() {
    val fakeTypes = listOf(
        ApplicationType(1, "Vacaciones", "Vacaciones pagadas", true, true),
        ApplicationType(2, "Días Exceso", "Día de exceso pagado", true, true),
        ApplicationType(3, "No Retribuido", "Día libre no pagado", true, true),
        ApplicationType(4, "Bolsa de horas", "Horas libres pagadas", true, true),
        ApplicationType(5, "Cambio de turno", "Cambio de turno", false, true),
    )
    val fakeApplications = listOf(
        Application(1, "PENDING", 1, 2, "2026-05-01T10:00:00", "Necesito vacaciones", null),
        Application(2, "APPROVED", 2, 2, "2026-04-15T09:00:00", "Cita médica", "2026-04-16T10:00:00"),
        Application(3, "REJECTED", 1, 2, "2026-04-01T08:00:00", "Vacaciones Semana Santa", null),
    )
    WorkLinkTheme {
        RequestsScreenContent(
            applications = fakeApplications,
            applicationTypes = fakeTypes,
            users = emptyList(),
            turns = emptyList(),
            isLoading = false,
            isSending = false,
            isLoadingDetail = false,
            showCreateSheet = false,
            showDetailSheet = false,
            selectedApplication = null,
            selectedDetail = null,
            selectedTypeId = null,
            formType = RequestFormType.DAYS,
            comments = "",
            startDate = "",
            endDate = "",
            date = "",
            fromTime = "",
            toTime = "",
            hoursRequested = "",
            selectedAffectedUser = null,
            selectedTurnGive = null,
            selectedTurnReceive = null,
            sendSuccess = false,
            error = null,
            onSelectApplication = {},
            onClearSelectedApplication = {},
            onShowCreateSheet = {},
            onHideCreateSheet = {},
            onTypeSelected = { _, _ -> },
            onCommentsChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onDateChange = {},
            onFromTimeChange = {},
            onToTimeChange = {},
            onHoursRequestedChange = {},
            onAffectedUserSelected = {},
            onTurnGiveSelected = {},
            onTurnReceiveSelected = {},
            onSendRequest = {}
        )
    }
}