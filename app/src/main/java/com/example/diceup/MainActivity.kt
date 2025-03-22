package com.example.diceup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diceup.ui.theme.DiceUpTheme
import androidx.navigation.compose.NavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var navController: NavHostController

        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Optional: for immersive layouts

        setContent {
            DiceUpTheme {
                DiceUpTheme {
                    navController = rememberNavController()
                    SetUpNavGraph(navController=navController)
                }

            }

        }
    }
}