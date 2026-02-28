package com.labin.piggybank.compose.homepage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.labin.piggybank.viewmodels.DashboardViewModel

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier
) {

    HomeScreenContent(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        navController = navController,
        modifier = modifier,

    )
}