package com.labin.piggybank.compose.operation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.labin.piggybank.data.Account
import com.labin.piggybank.data.Category
import com.labin.piggybank.viewmodels.AccountViewModel
import com.labin.piggybank.viewmodels.CategoryViewModel
import com.labin.piggybank.viewmodels.SaveResult
import com.labin.piggybank.viewmodels.TransactionViewModel
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.lazy.items
import com.labin.piggybank.domain.TransactionType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOperation(
    userId: Long,
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    accountViewModel: AccountViewModel
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var showAccountPicker by remember { mutableStateOf(false) }
    var selectedDestinationAccountId by remember { mutableStateOf<Long?>(null) }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    val isTransfer = selectedType == TransactionType.TRANSFER

    val snackbarHostState = remember { SnackbarHostState() }
    val saveResult by transactionViewModel.saveResult.collectAsState()
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val accounts by accountViewModel.accounts.collectAsStateWithLifecycle()

    LaunchedEffect(saveResult) {
        saveResult?.let { result ->
            when (result) {
                is SaveResult.Success -> navController.popBackStack()
                is SaveResult.Error -> {
                    snackbarHostState.showSnackbar(result.message, duration = SnackbarDuration.Short)
                    transactionViewModel.resetSaveResult()
                }
            }
        }
    }

    LaunchedEffect(selectedType) {
        selectedCategory = null
        selectedDestinationAccountId = null
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Новая операция") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TransactionType.values().forEachIndexed { index, type ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                        onClick = { selectedType = type },
                        selected = selectedType == type
                    ) {
                        Text(
                            text = when(type) {
                                TransactionType.EXPENSE -> "Расход"
                                TransactionType.INCOME -> "Доход"
                                TransactionType.TRANSFER -> "Перевод"
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сумма") },
                suffix = { Text("₽") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание операции") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            val sourceAccountName = accounts.find { it.id == selectedAccountId }?.name ?: "Выберите счёт"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAccountPicker = true }
            ) {
                OutlinedTextField(
                    value = sourceAccountName,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text(if (isTransfer) "Счёт отправления" else "Счёт списания") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = selectedAccountId == null,
                    singleLine = true
                )
            }

            if (isTransfer) {
                var showDestinationPicker by remember { mutableStateOf(false) }
                val destinationAccountName = accounts.find { it.id == selectedDestinationAccountId }?.name ?: "Выберите счёт"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDestinationPicker = true }
                ) {
                    OutlinedTextField(
                        value = destinationAccountName,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Счёт зачисления") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = selectedDestinationAccountId == null || selectedDestinationAccountId == selectedAccountId,
                        singleLine = true
                    )
                }

                if (selectedAccountId != null && selectedDestinationAccountId == selectedAccountId) {
                    Text(
                        "Счета не могут совпадать",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                if (showDestinationPicker) {
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ModalBottomSheet(
                        onDismissRequest = { showDestinationPicker = false },
                        sheetState = sheetState
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text("Выберите счёт", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 16.dp))
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(accounts.filter { it.id != selectedAccountId }, key = { it.id }) { account ->
                                    AccountPickerItem(
                                        account = account,
                                        isSelected = account.id == selectedDestinationAccountId,
                                        onSelect = {
                                            selectedDestinationAccountId = account.id
                                            showDestinationPicker = false
                                        }
                                    )
                                }
                                item { Spacer(Modifier.height(80.dp)) }
                            }
                        }
                    }
                }
            }

            if (!isTransfer) {
                Text(
                    text = "Выберите категорию",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        CategoryItem(
                            category = category,
                            isSelected = selectedCategory?.id == category.id,
                            onSelect = { selectedCategory = category }
                        )
                    }
                    item {
                        AddNewCategoryItem(onClick = { navController.navigate("categoryEditor") })
                    }
                }
            }

            Button(
                onClick = {
                    transactionViewModel.saveTransaction(
                        amount = amount,
                        category = if (isTransfer) null else selectedCategory,
                        accountId = selectedAccountId!!,
                        currencyId = 1L,
                        description = description.text,
                        destinationAccountId = if (isTransfer) selectedDestinationAccountId else null,
                        type = selectedType
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = amount.isNotBlank() &&
                        selectedAccountId != null &&
                        if (isTransfer) {
                            selectedDestinationAccountId != null && selectedDestinationAccountId != selectedAccountId
                        } else {
                            selectedCategory != null
                        }
            ) {
                Text("Добавить операцию")
            }
        }
    }

    if (showAccountPicker) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = {
            showAccountPicker = false
                                            }, sheetState = sheetState) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Выберите счёт", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 16.dp))

                if (accounts.isEmpty()) {
                    Text("Нет доступных счетов", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(accounts, key = { it.id }) { account ->
                            AccountPickerItem(
                                account = account,
                                isSelected = account.id == selectedAccountId,
                                onSelect = {
                                    selectedAccountId = account.id
                                    showAccountPicker = false
                                }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountPickerItem(account: Account, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(account.name.take(1), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(account.name, style = MaterialTheme.typography.titleMedium)
                Text(account.type.name.replace("_", " "), style = MaterialTheme.typography.bodySmall, color = LocalContentColor.current.copy(alpha = 0.6f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${account.balance}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (isSelected) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onSelect: () -> Unit) {
    val categoryColor = remember(category.colorHex) {
        runCatching { Color(AndroidColor.parseColor(category.colorHex)) }.getOrDefault(Color.Gray)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(MaterialTheme.shapes.medium)
            .background(if (isSelected) categoryColor.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onSelect() }.padding(8.dp)
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape)
                .background(if (isSelected) categoryColor else categoryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (category.iconResId != null) {
                Icon(painter = painterResource(id = category.iconResId), contentDescription = category.name,
                    tint = if (isSelected) Color.White else categoryColor)
            } else {
                Icon(imageVector = Icons.Default.QuestionMark, contentDescription = null,
                    tint = if (isSelected) Color.White else categoryColor)
            }
        }
        Text(text = category.name, style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp), color = if (isSelected) categoryColor else LocalContentColor.current)
    }
}

@Composable
fun AddNewCategoryItem(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() }.padding(8.dp)
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить новую категорию",
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = "Новая", style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
fun NewOperationScreenPreview() {
    NewOperation(
        userId = 1L,
        navController = rememberNavController(),
        transactionViewModel = hiltViewModel(),
        categoryViewModel = hiltViewModel(),
        accountViewModel = hiltViewModel()
    )
}

@Composable
fun NewOperationScreen(
    userId: Long,
    navController: NavController,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel(),
) {
    NewOperation(
        userId=userId,
        navController,
        transactionViewModel = transactionViewModel,
        categoryViewModel = categoryViewModel,
        accountViewModel = accountViewModel
    )
}
