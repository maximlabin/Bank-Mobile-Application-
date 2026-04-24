package com.labin.piggybank.compose.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.labin.piggybank.domain.AccountType
import com.labin.piggybank.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.popBackStack();
            viewModel.resetSuccess();
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый счёт") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() })
                { Icon(Icons.Default.ArrowBack, null) } })
            }) { padding ->
                Column(Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = state.name, onValueChange = viewModel::updateName, label = { Text("Название") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = state.balance, onValueChange = viewModel::updateBalance, label = { Text("Начальный баланс") }, prefix = { Text("₽") }, keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ), modifier = Modifier.fillMaxWidth())

                Text("Тип счета", style = MaterialTheme.typography.titleMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AccountType.entries.forEach { type ->
                            FilterChip(
                                selected = state.type == type,
                                onClick = { viewModel.updateType(type) },
                                label = {
                                    Text(
                                        text = when (type) {
                                            AccountType.CASH -> "Наличные"
                                            AccountType.BANK_ACCOUNT -> "Банковский счёт"
                                            AccountType.CREDIT_CARD -> "Кредитная карта"
                                            AccountType.DEBIT_CARD -> "Дебетовая карта"
                                            AccountType.INVESTMENT -> "Инвестиции"
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },

                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    }


                if (state.type in listOf(AccountType.BANK_ACCOUNT, AccountType.CREDIT_CARD, AccountType.DEBIT_CARD)) {
                    OutlinedTextField(value = state.bankName, onValueChange = viewModel::updateBank, label = { Text("Название банка") }, modifier = Modifier.fillMaxWidth())
                }

                OutlinedTextField(value = state.note, onValueChange = viewModel::updateNote, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                Button(onClick = { viewModel.save(userId = 1L) }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    if (state.isLoading) CircularProgressIndicator() else Text("Создать счёт")
                }
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(
                    Alignment.CenterHorizontally)) }
        }
    }
}
