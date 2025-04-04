package com.example.diceup

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")

    object Game : Screen("game_screen/{targetScore}/{isHardMode}") {
        fun createRoute(targetScore: Int, isHardMode: Boolean): String {
            return "game_screen/$targetScore/$isHardMode"
        }
    }
}
