package com.labin.piggybank.compose.category

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.labin.piggybank.domain.CategoryType
import com.labin.piggybank.utilities.PRESET_COLORS
import com.labin.piggybank.utilities.PRESET_ICONS
import com.labin.piggybank.viewmodels.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.popBackStack()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая категория") },
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
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Тип категории", style = MaterialTheme.typography.titleMedium)
            FilterChipRow(
                selected = state.type,
                onTypeSelected = viewModel::onTypeSelect
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Название категории") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.error != null
            )

            Text("Цвет", style = MaterialTheme.typography.titleMedium)
            ColorPicker(
                colors = PRESET_COLORS,
                selected = state.selectedColor,
                onColorSelected = viewModel::onColorSelect
            )

            Text("Иконка", style = MaterialTheme.typography.titleMedium)
            IconGrid(
                icons = PRESET_ICONS,
                selectedId = state.selectedIconResId,
                onIconSelected = viewModel::onIconSelect,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::saveCategory,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = LocalContentColor.current, modifier = Modifier.size(24.dp))
                } else {
                    Text("Создать категорию")
                }
            }

            state.error?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun IconGrid(
    icons: List<Int>,
    selectedId: Int?,
    onIconSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier.height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = if (selectedId == null) 2.dp else 0.dp,
                        color = if (selectedId == null) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { onIconSelected(null) },
                contentAlignment = Alignment.Center
            ) {
                Text("—", color = LocalContentColor.current)
            }
        }
        items(icons) { iconId ->
            val isSelected = selectedId == iconId
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { onIconSelected(iconId) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else LocalContentColor.current
                )
            }
        }
    }
}

@Composable
fun FilterChipRow(selected: CategoryType, onTypeSelected: (CategoryType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(CategoryType.EXPENSE, CategoryType.INCOME).forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onTypeSelected(type) },
                label = { Text(if (type == CategoryType.EXPENSE) "Расход" else "Доход") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ColorPicker(colors: List<String>, selected: String, onColorSelected: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(colors) { colorHex ->
            val color = runCatching { Color(AndroidColor.parseColor(colorHex)) }.getOrDefault(Color.Gray)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selected == colorHex) 3.dp else 0.dp,
                        color = if (selected == colorHex) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colorHex) }
            )
        }
    }
}

@Composable
fun IconGrid(icons: List<Int>, selectedId: Int?, onIconSelected: (Int?) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = if (selectedId == null) 2.dp else 0.dp,
                        color = if (selectedId == null) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { onIconSelected(null) },
                contentAlignment = Alignment.Center
            ) {
                Text("—", color = LocalContentColor.current)
            }
        }
        items(icons) { iconId ->
            val isSelected = selectedId == iconId
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { onIconSelected(iconId) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else LocalContentColor.current
                )
            }
        }
    }
}
