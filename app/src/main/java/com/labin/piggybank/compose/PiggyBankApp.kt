package com.labin.piggybank.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.labin.piggybank.compose.homepage.HomeScreen
import com.labin.piggybank.compose.operation.NewOperation
import com.labin.piggybank.compose.profile.ProfileScreen



@Composable
fun PiggyBankApp() {
    val navController = rememberNavController()
    PiggyBankNavHost(
        navController = navController
    )
}

@Composable
fun PiggyBankNavHost (
    navController: NavHostController
) {
    //val activity = (LocalContext.current as Activity)
    NavHost(
        navController= navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreen(navController = navController, modifier = Modifier)
        }
        composable(route="profile/{userId}") {
            backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileScreen(
                userId = userId,
                navController = navController
            )
        }
        composable(route = "newOperation/{userId}") {
            backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            NewOperation(
                userId = userId,
                navController = navController
            )
        }

    }
}