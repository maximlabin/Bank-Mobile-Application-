package com.labin.piggybank.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.labin.piggybank.R
import com.labin.piggybank.compose.account.AccountScreen
import com.labin.piggybank.compose.analytics.AnalyticsScreen
import com.labin.piggybank.compose.analytics.CreateGoalScreen
import com.labin.piggybank.compose.calendar.CalendarScreen
import com.labin.piggybank.compose.homepage.HomeScreen
import com.labin.piggybank.compose.profile.ProfileScreen
import com.labin.piggybank.compose.operation.NewOperationScreen
import com.labin.piggybank.compose.category.CategoryEditorScreen
import com.labin.piggybank.compose.currency.CurrencyScreen
import com.labin.piggybank.compose.operation.TransactionListScreen
import com.labin.piggybank.viewmodels.CalendarViewModel

sealed class MainScreen(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Home : MainScreen("home", R.string.nav_home, Icons.Default.Home)
    data object NewOperation : MainScreen("newOperation", R.string.nav_new_op, Icons.Default.AccountBalanceWallet)
    data object Statistics : MainScreen("statistics", R.string.nav_stats, Icons.Default.PieChart)
    data object Profile : MainScreen("profile", R.string.nav_profile, Icons.Default.Person)

    companion object {
        val items = listOf(Home, NewOperation, Statistics, Profile)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PiggyBankApp(
    navController: NavHostController = rememberNavController(),
    currentUserId: Long = 1L
) {
    val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = null)
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            NavigationBar {
                MainScreen.items.forEach { screen ->
                    val isSelected = currentRoute == screen.route ||
                            currentRoute?.startsWith("${screen.route}/") == true
                    NavigationBarItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = isSelected,
                        onClick = {
                            val targetRoute = when (screen) {
                                MainScreen.Home -> "home"
                                MainScreen.Statistics -> "statistics"
                                MainScreen.NewOperation -> "operations"
                                MainScreen.Profile -> "profile/$currentUserId"
                            }
                            navController.navigate(targetRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(innerPadding),
        ) {
            composable("home") { HomeScreen(navController) }

            composable("newOperation/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                NewOperationScreen(userId = userId, navController = navController)
            }

            composable("statistics") {
                AnalyticsScreen(
                    onCreateGoalClick = { navController.navigate("create_goal") }
                )
            }

            composable("create_goal") {
                CreateGoalScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("operations") {
                TransactionListScreen(navController = navController)
            }
            composable("profile/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ProfileScreen(userId = userId, navController = navController)
            }
            composable("currency") { CurrencyScreen(navController = navController) }
            composable("calendar") {
                val calendarVM: CalendarViewModel = hiltViewModel()
                CalendarScreen(navController = navController)
            }
            composable("account") { AccountScreen(navController = navController) }
            composable("categoryEditor") { CategoryEditorScreen(navController = navController) }
        }
    }
}
