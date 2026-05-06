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
import com.labin.piggybank.viewmodels.AccountViewModel
import com.labin.piggybank.viewmodels.DashboardViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        navController = navController,
        onTypeSelected = {},
        accountViewModel = accountViewModel,
        dashboardViewModel = dashboardViewModel,
    )
}