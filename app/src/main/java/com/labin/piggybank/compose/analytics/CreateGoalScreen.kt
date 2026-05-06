package com.labin.piggybank.compose.analytics
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labin.piggybank.viewmodels.GoalViewModel
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    viewModel: GoalViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateBack()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDeadlineChanged(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("Выбрать") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая финансовая цель") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Название цели") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.targetAmount,
                onValueChange = viewModel::onAmountChanged,
                label = { Text("Целевая сумма") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Срок достижения (опционально)", style = MaterialTheme.typography.labelMedium)
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.deadlineMillis?.let { millis ->
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))
                    } ?: "Выбрать дату"
                )
            }

            if (accounts.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedAccount = accounts.find { it.id == state.targetAccountId }
                    OutlinedTextField(
                        value = selectedAccount?.name ?: "Без привязки к счету",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Целевой счет") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Без привязки к счету") },
                            onClick = { viewModel.onAccountSelected(null); expanded = false }
                        )
                        accounts.forEach { acc ->
                            DropdownMenuItem(
                                text = { Text("${acc.name} (${acc.balance})") },
                                onClick = { viewModel.onAccountSelected(acc.id); expanded = false }
                            )
                        }
                    }
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = viewModel::saveGoal,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Создать цель")
                }
            }
        }
    }
}