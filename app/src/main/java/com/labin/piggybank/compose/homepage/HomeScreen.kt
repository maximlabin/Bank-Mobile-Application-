package com.labin.piggybank.compose.homepage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.labin.piggybank.ui.model.HomeUiState
import com.labin.piggybank.viewmodels.DashboardViewModel

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        uiState = HomeUiState(),
        navController = rememberNavController()
    )
}


@Composable
fun HomeScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier
) {
    HomeScreenContent(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        //onEvent = viewModel::onEvent,
        navController = navController,
        modifier = modifier
    )
}