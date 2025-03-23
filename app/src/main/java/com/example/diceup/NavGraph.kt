package com.example.diceup

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.diceup.HomeScreen
import com.example.diceup.Screen
import com.example.diceup.GameScreen

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
            route = Screen.Game.route
        ){
            GameScreen(navController = navController)
        }
    }
}
