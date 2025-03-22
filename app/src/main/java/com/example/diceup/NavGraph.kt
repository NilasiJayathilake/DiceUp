package com.example.diceup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(
            route= Screen.Home.route
        ) { HomeScreen(navController=navController) }

        composable(
            route = Screen.Game.route
        ){
            GameScreen()
        }
    }
}
