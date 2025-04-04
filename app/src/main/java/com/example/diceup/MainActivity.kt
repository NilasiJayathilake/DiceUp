package com.example.diceup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diceup.ui.theme.DiceUpTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var navController: NavHostController

        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Optional: for immersive layouts

        setContent {

                DiceUpTheme {
                    navController = rememberNavController()
                    SetUpNavGraph(navController=navController)
                }



        }
    }
}