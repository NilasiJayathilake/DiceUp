package com.example.diceup

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(
            route= Screen.Home.route
        ) { HomeScreen(navController=navController) }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("targetScore") { type = NavType.IntType },
                navArgument("isHardMode") { type = NavType.BoolType }
            )
        ) {
            val targetScore = it.arguments?.getInt("targetScore") ?: 101
            val isHardMode = it.arguments?.getBoolean("isHardMode") ?: false
            GameScreen(navController = navController, targetScore, isHardMode)
        }
    }
}
