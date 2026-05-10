package com.example.worklink.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worklink.data.model.ApplicationHoursDetail
import com.example.worklink.data.model.Turn
import com.example.worklink.data.model.TurnAssigned
import com.example.worklink.ui.theme.WorkLinkTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    CalendarScreenContent(
        assignedTurns = uiState.assignedTurns,
        turns = uiState.turns,
        selectedTurn = uiState.selectedTurn,
        selectedTurnDetail = uiState.selectedTurnDetail,
        selectedHoursDetail = uiState.selectedHoursDetail,
        isLoading = uiState.isLoading,
        error = uiState.error,
        userName = uiState.userName,
        approvedHoursDates = uiState.approvedHoursDates,
        onDayClick = { viewModel.selectDay(it) },
        onRefresh = { viewModel.loadData() }
    )
}

// Calendario, leyenda y detalles del turno
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreenContent(
    assignedTurns: List<TurnAssigned>,
    turns: List<Turn>,
    selectedTurn: TurnAssigned?,
    selectedTurnDetail: Turn?,
    selectedHoursDetail: ApplicationHoursDetail? = null,
    isLoading: Boolean,
    error: String?,
    userName: String,
    approvedHoursDates: Set<String> = emptySet(),
    onDayClick: (TurnAssigned?) -> Unit,
    onRefresh: () -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showContacts by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (userName.isNotEmpty()) {
                            Column {
                                Text(
                                    text = when (java.time.LocalTime.now().hour) {
                                        in 6..11 -> "Buenos días"
                                        in 12..19 -> "Buenas tardes"
                                        else -> "Buenas noches"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = userName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        IconButton(onClick = { showContacts = true }) {
                            Icon(Icons.Default.Info, contentDescription = "Contactos", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CalendarHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                        onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    DayOfWeekHeader()
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        CalendarGrid(
                            currentMonth = currentMonth,
                            assignedTurns = assignedTurns,
                            turns = turns,
                            selectedTurn = selectedTurn,
                            approvedHoursDates = approvedHoursDates,
                            onDayClick = onDayClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (turns.isNotEmpty()) {
                            TurnLegend(
                                turns = turns,
                                hasHours = approvedHoursDates.isNotEmpty()
                            )
                        }
                    }
                    error?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            selectedTurnDetail?.let { turn ->
                TurnDetailCard(
                    turn = turn,
                    turnAssigned = selectedTurn,
                    hoursDetail = selectedHoursDetail,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Si no hay turno asignado pero sí horas, mostrar card de horas
            if (selectedTurn != null && selectedTurnDetail == null && selectedHoursDetail != null) {
                HoursOnlyCard(
                    hoursDetail = selectedHoursDetail,
                    date = selectedTurn.date,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showContacts) {
        ModalBottomSheet(
            onDismissRequest = { showContacts = false },
            sheetState = sheetState
        ) {
            ContactsBottomSheet(onDismiss = { showContacts = false })
        }
    }
}

// Header calendario
@Composable
fun CalendarHeader(currentMonth: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior", tint = Color.White)
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente", tint = Color.White)
        }
    }
}

// Fila con los días de la semana
@Composable
fun DayOfWeekHeader() {
    val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Cuadricula calendario
@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    assignedTurns: List<TurnAssigned>,
    turns: List<Turn>,
    selectedTurn: TurnAssigned?,
    approvedHoursDates: Set<String> = emptySet(),
    onDayClick: (TurnAssigned?) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val turnsMap = assignedTurns.associateBy { it.date }
    val turnsById = turns.associateBy { it.id }
    val today = LocalDate.now()
    val totalCells = firstDayOfWeek - 1 + daysInMonth
    val rows = (totalCells + 6) / 7

    Column {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - (firstDayOfWeek - 1) + 1
                    if (dayIndex < 1 || dayIndex > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = currentMonth.atDay(dayIndex)
                        val dateStr = date.toString()
                        val assigned = turnsMap[dateStr]
                        val turn = assigned?.let { turnsById[it.turnId] }
                        val isSelected = selectedTurn?.date == dateStr
                        val isToday = date == today
                        DayCell(
                            day = dayIndex,
                            turnColor = turn?.colorHex,
                            isSelected = isSelected,
                            isToday = isToday,
                            hasHours = dateStr in approvedHoursDates,
                            onClick = { onDayClick(assigned) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// Celda individual calendario
@Composable
fun DayCell(
    day: Int,
    turnColor: String?,
    isSelected: Boolean,
    isToday: Boolean,
    hasHours: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = turnColor?.let { parseColor(it) } ?: Color.Transparent
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (turnColor != null) bgColor.copy(alpha = 0.9f)
                else if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isToday && turnColor == null) {
            Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)))
        }
        Text(
            text = day.toString(),
            fontSize = 13.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
            color = if (turnColor != null) Color.White
            else if (isToday) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (hasHours) {
            Text(
                text = "H",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (turnColor != null) Color.White.copy(alpha = 0.9f)
                else MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp)
            )
        }
    }
}

// Leyenda poara los turnos
@Composable
fun TurnLegend(turns: List<Turn>, hasHours: Boolean = false) {
    Text(
        text = "Leyenda",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    val chunks = turns.chunked(2)
    chunks.forEach { rowTurns ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowTurns.forEach { turn ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(parseColor(turn.colorHex)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = turn.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (rowTurns.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
    }
    if (hasHours) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "H",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Bolsa de horas",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Tarjeta con detalles del turno
@Composable
fun TurnDetailCard(
    turn: Turn,
    turnAssigned: TurnAssigned?,
    hoursDetail: ApplicationHoursDetail? = null,
    modifier: Modifier = Modifier
) {
    val turnColor = parseColor(turn.colorHex)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(turnColor))
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(turnColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = turn.name.first().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = turnColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = turn.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = turn.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    DetailItem(emoji = "📅", label = "Fecha", value = turnAssigned?.date ?: "")
                    if (turn.workStart != null && turn.workEnd != null) {
                        DetailItem(emoji = "🕐", label = "Entrada", value = turn.workStart)
                        DetailItem(emoji = "🕔", label = "Salida", value = turn.workEnd)
                    }
                }

                hoursDetail?.let { hours ->
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "⏱ Bolsa de horas aprobada",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                DetailItem(emoji = "🕐", label = "Desde", value = hours.fromTime.take(5))
                                DetailItem(emoji = "🕔", label = "Hasta", value = hours.toTime.take(5))
                                DetailItem(emoji = "⏳", label = "Horas", value = "${hours.hoursRequested}h")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Tarjeta de horas aprobadas
@Composable
fun HoursOnlyCard(
    hoursDetail: ApplicationHoursDetail,
    date: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(MaterialTheme.colorScheme.primary))
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "H", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Bolsa de horas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Horas aprobadas este día", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    DetailItem(emoji = "📅", label = "Fecha", value = date)
                    DetailItem(emoji = "🕐", label = "Desde", value = hoursDetail.fromTime.take(5))
                    DetailItem(emoji = "🕔", label = "Hasta", value = hoursDetail.toTime.take(5))
                    DetailItem(emoji = "⏳", label = "Horas", value = "${hoursDetail.hoursRequested}h")
                }
            }
        }
    }
}

// Mostrar datos con emojis
@Composable
fun DetailItem(emoji: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 20.sp)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

// Color no valido = Gris
fun parseColor(hex: String?): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex ?: "#CCCCCC"))
    } catch (e: Exception) {
        Color.LightGray
    }
}

// Panel de contactos
@Composable
fun ContactsBottomSheet(onDismiss: () -> Unit) {
    val contacts = listOf(
        Triple("Recursos Humanos", "rrhh@worklink.com", Icons.Default.Person),
        Triple("Prevención de Riesgos", "prevencion@worklink.com", Icons.Default.Info),
        Triple("Ausencias o emergencias", "ausencias.ple@worklink.com", Icons.Default.Phone)
    )
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Box(
            modifier = Modifier.width(40.dp).height(4.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Contactos de la empresa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Pulsa el email para contactar directamente", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(20.dp))
        contacts.forEach { (name, email, icon) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO, android.net.Uri.parse("mailto:$email"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Email, contentDescription = "Enviar email", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Previsualización
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarScreenPreview() {
    val fakeTurns = listOf(
        Turn(1, "Mañana", "Turno de mañanas", "06:00:00", "14:00:00", "#F5CC27", false, true),
        Turn(2, "Tarde", "Turno de tardes", "14:00:00", "22:00:00", "#F562A4", false, true),
        Turn(3, "Noche", "Turno de noches", "22:00:00", "06:00:00", "#0E8262", true, true),
        Turn(4, "Vacaciones", "Vacaciones pagadas", null, null, "#FF8000", false, true),
        Turn(5, "Día Exceso", "Día de exceso retribuido", null, null, "#1976D2", false, true),
        Turn(6, "No Retribuido", "Día libre no retribuido", null, null, "#9C27B0", false, true),
    )
    val fakeAssigned = listOf(
        TurnAssigned(1, "2026-05-04", null, null, 2, "David", 1, "Mañana"),
        TurnAssigned(2, "2026-05-05", null, null, 2, "David", 2, "Tarde"),
        TurnAssigned(3, "2026-05-06", null, null, 2, "David", 3, "Noche"),
        TurnAssigned(4, "2026-05-07", null, null, 2, "David", 4, "Vacaciones"),
    )
    WorkLinkTheme {
        CalendarScreenContent(
            assignedTurns = fakeAssigned,
            turns = fakeTurns,
            selectedTurn = fakeAssigned.first(),
            selectedTurnDetail = fakeTurns.first(),
            selectedHoursDetail = null,
            isLoading = false,
            error = null,
            userName = "David",
            approvedHoursDates = setOf("2026-05-05"),
            onDayClick = {}
        )
    }
}