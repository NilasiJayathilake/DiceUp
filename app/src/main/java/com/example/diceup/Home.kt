package com.example.diceup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
//
//@Preview
//@Composable
//private fun PreviewHome() {
//    HomeScreen(navController = rememberNavController())
//
//}

@Composable
fun HomeScreen(navController: NavHostController) {
    var targetScore by rememberSaveable { mutableStateOf("101") }
    var isHardMode by rememberSaveable { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Target Score", style = MaterialTheme.typography.titleMedium)
        TextField(
            value = targetScore,
            onValueChange = { if (it.all { ch -> ch.isDigit() }) targetScore = it },
            label = { Text("Target Score") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Hard Mode")
            Switch(checked = isHardMode, onCheckedChange = { isHardMode = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val score = targetScore.toIntOrNull() ?: 101
            navController.navigate(Screen.Game.createRoute(score, isHardMode))
        }) {
            Text("New Game")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {showAboutDialog=true}) { Text("About") }
        if (showAboutDialog) {
            ShowAboutDialog(onDismiss = { showAboutDialog = false })
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class) // Using Material 3 components though its not stable
@Composable
fun ShowAboutDialog(onDismiss: ()->Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About") },
        text = {
            Text("I confirm that I understand what plagiarism is and have read and understood the section on " +
                    "Assessment Offences in the Essential Information for Students. The work that" +
                    " I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged.")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

