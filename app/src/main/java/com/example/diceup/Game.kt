package com.example.diceup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun GameScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var pcDiceList by remember { mutableStateOf(List(5){(1..6).random()}) }
    var userDiceList by remember { mutableStateOf(List(5){(1..6).random()}) }
    var userScore by remember { mutableStateOf(0) }
    var pcScore by remember { mutableStateOf(0) }
    var keepUserList by remember { mutableStateOf(List(5){false}) }
    var userTurns by remember { mutableStateOf(0) }
    var pcTurns by remember { mutableStateOf(0) }
    var rollCount by remember { mutableStateOf(1) } // Since one roll is already done at start

    // Alert Box Variables
    var showWinDialog by remember { mutableStateOf(false) }
    var winnerMessage by remember { mutableStateOf("") }
    var winnerColor by remember { mutableStateOf(Color.Unspecified) }
    fun score(){
        userScore += calculateScore(userDiceList)
        pcScore += calculateScore(pcDiceList)
        // If WON
        val (message, color, won) = checkWinner(userScore, pcScore, userTurns, pcTurns, 101)
        if (won) { winnerMessage = message
            winnerColor = color
            showWinDialog = true
        } else {
            // resetting
            startNewTurn(
                onUserDiceReset = { userDiceList = it },
                onPCDiceReset = { pcDiceList = it },
                onKeepListReset = { keepUserList = List(5) { false } },
                onRollCountReset = { rollCount = 1 },
                onTurnIncrement = {
                    userTurns++
                    pcTurns++
                }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.align(Alignment.TopEnd).offset(x=(-2).dp)) {
            ScoreBoard(userScore = userScore, pcScore = pcScore)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-200).dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Player(userName = "AndraiaPC", profilePicID = R.drawable.computeravatar)
        }

        // Second: Computer Dice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-100).dp),
            horizontalArrangement = Arrangement.Center
        ) {
            DiceRoll(pcDiceList, null)
        }

        // Third: Human Dice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 100.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            DiceRoll(userDiceList, keepUserList,
                onToggle = {index->keepUserList = keepUserList.toMutableList().apply { this[index] = !this[index] }})
        }

        // Bottom: Human Profile
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (300).dp, y = 200.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            Player(userName = "You", profilePicID = R.drawable.useravatar)

        }

        Column (modifier = Modifier.fillMaxWidth().offset(y= 300.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Roll: $rollCount / 3",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                Button(onClick = {
                    if (rollCount <3) {
                        userDiceList = userDiceList.mapIndexed { index, value ->
                            if (!keepUserList[index]) (1..6).random() else value
                            // If the Index of the Dice is NOT in the Keep List dice is re rolled
                        }
                        keepUserList = List(5) { false }
                        pcDiceList = applyComputerStrategy(pcDiceList)
                    }
                        rollCount++

                    if (rollCount==3) {
                        // Calling Score Function
                        score()

                    }
                },
                    enabled = rollCount<3
                ) {
                    Text("Rethrow")
                }
                Spacer(modifier=Modifier.width(16.dp))

                Button(onClick = {
                    if (rollCount<3){ // If the rollCount is less than 3
                        for (i in 1..(3-rollCount)) { // pc will roll 3-roll count times
                            pcDiceList = applyComputerStrategy(pcDiceList)
                        }
                    }
                   // Calling Score Function
                    score()

                }) { Text("Score") }
            }

            }
        if (showWinDialog) {
            AlertDialog(
                onDismissRequest = {}, confirmButton = {
                    Button(onClick = { showWinDialog = false // Optional: reset full game
                        navController.popBackStack()
                    }) {
                        Text("Go Back")
                    } },

                title = {
                    Text(
                        text = winnerMessage,
                        color = winnerColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text("Game Over. Thanks for playing")
                }
            )
        }
        }
    }


@Preview
@Composable
private fun PreviewGameScreen() {
   GameScreen(navController = rememberNavController())
}

@Composable
fun DiceRoll(diceList:List<Int>,  keepList: List<Boolean>? = null, onToggle: ((Int) -> Unit)? = null ) {
    val dice = listOf(
//        R stands for resources- so we can access the resources of
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for ((index, num) in diceList.withIndex()) {
                val notSelectedForReRoll = keepList?.getOrNull(index)
                Column(modifier = Modifier
                        .padding(8.dp).then(
                        if (onToggle != null)
                            Modifier.clickable { onToggle(index) }
                        else Modifier
                    )
                    .border(
                        width = 2.dp,
                        color = if (notSelectedForReRoll==true) Color.Yellow else Color.Transparent
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = dice[num - 1]),
                        contentDescription = "Dice $num",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Player(userName: String, profilePicID: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ){
        Image(painter = painterResource(id = profilePicID),
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(CircleShape).border(2.dp,Color.Black)
            )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = userName)
    }
}

fun calculateScore(diceList: List<Int>): Int {
    return diceList.sum()
}

@Composable
fun ScoreBoard(userScore: Int, pcScore: Int) {
    Row(
        modifier = Modifier
            .width(270.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(2.dp, Color.DarkGray,RoundedCornerShape(50.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text("PC", style = MaterialTheme.typography.labelSmall)
            Text("$pcScore", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            "SCOREBOARD",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        Column(horizontalAlignment = Alignment.End) {
            Text("You", style = MaterialTheme.typography.labelSmall)
            Text("$userScore", style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun applyComputerStrategy(currentList: List<Int>): List<Int> {
    val reRoll = listOf(true, false).random()
    if (!reRoll) {
        return currentList
    }
    val noOfDiceToReRoll = (1..5).random() // decides how many Dice to re roll
    val diceToReRoll = (0..5).shuffled()
        .take(noOfDiceToReRoll) // picks a no of indexes from 1 to 5 randomly to be rerolled.
    return currentList.mapIndexed { index, value ->
        if (index in diceToReRoll) (1..6).random() else value // checks if the index is in
                        // the diceToReRoll if it is generates a new value if not keeps the value as it is

    }
}

fun checkWinner(userScore: Int, pcScore: Int, userTurns: Int, pcTurns: Int, maxScore: Int): Triple<String, Color, Boolean> {
    // First check if PC or User achieved maxScore
    if (userScore >= maxScore && (pcScore < maxScore)) {
        return Triple("You Win!", Color.Green, true)
    }
    if (pcScore >= maxScore && (userScore < maxScore)) {
        return Triple("You Lose :/ Better Luck Next Time", Color.Red, true)
    }

    // In case of both achieving the same score we check who achieved it in less turns
    if (userScore >= maxScore && pcScore >= maxScore)  {
        // Compare turns
        return when {
            userTurns < pcTurns -> Triple("You Win! That was Intense", Color.Green, true)
            pcTurns < userTurns -> Triple("You Loose, Too Many Turns", Color.Red, true)
            userScore > pcScore -> Triple("You Win!", Color.Green, true)
            pcScore > userScore -> Triple("You Lose, Close Call", Color.Red, true)
            else -> {
                // Tie-breaker: both same score and same turns — sudden death roll
                val userLast = List(5) { (1..6).random() }.sum()
                val pcLast = List(5) { (1..6).random() }.sum()
                if (userLast > pcLast)
                    Triple("You Win (Tie Breaker)!", Color.Green, true)
                else
                    Triple("You Lose (Tie Breaker)", Color.Red, true)
            }
        }
    }
    return Triple(" ", Color.Transparent, false)
}

    fun startNewTurn(
        onUserDiceReset: (List<Int>) -> Unit,
        onPCDiceReset: (List<Int>) -> Unit,
        onKeepListReset: () -> Unit,
        onRollCountReset: () -> Unit,
        onTurnIncrement: () -> Unit
    ) {
        onUserDiceReset(List(5) { (1..6).random() })
        onPCDiceReset(List(5) { (1..6).random() })
        onKeepListReset()
        onRollCountReset()
        onTurnIncrement()
    }



