package com.example.diceup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
private fun PreviewHome() {
    HomeScreen(navController = rememberNavController())

}

@Composable
fun HomeScreen(
    navController: NavController, modifier: Modifier = Modifier) {
    var showAboutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(modifier = Modifier.padding(2.dp)) {
                Button(onClick = {navController.navigate(route = Screen.Game.route)}) {
                    Text("New Game")
                }
            }
            Row(modifier = Modifier.padding(2.dp)) {
                Button(onClick = { showAboutDialog = true }) {
                    Text("About")
                }
            }
            if (showAboutDialog) {
                ShowAboutDialog(onDismiss = { showAboutDialog = false }) // Will close the dialog box if tapped outside
            }

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
            Text(" I confirm that I understand what plagiarism is and have read and\n" +
                    " understood the section on Assessment Offences in the Essential\n" +
                    " Information for Students. The work that I have submitted is\n" +
                    " entirely my own. Any work from other authors is duly referenced\n" +
                    " and acknowledged.")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

