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
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
    var pcDiceList by rememberSaveable { mutableStateOf(List(5){(1..6).random()}) }
    var userDiceList by rememberSaveable  { mutableStateOf(List(5){(1..6).random()}) }
    var userScore by rememberSaveable  { mutableStateOf(0) }
    var pcScore by rememberSaveable  { mutableStateOf(0) }
    var keepUserList by rememberSaveable  { mutableStateOf(List(5){false}) }
    var keepPCList by rememberSaveable { mutableStateOf(List(5) { false }) }
    var userTurns by rememberSaveable  { mutableStateOf(0) }
    var pcTurns by rememberSaveable  { mutableStateOf(0) }
    var rollCount by rememberSaveable  { mutableStateOf(1) } // Since one roll is already done at start

    // Alert Box Variables
    var showWinDialog by rememberSaveable  { mutableStateOf(false) }
    var winnerMessage by rememberSaveable  { mutableStateOf("") }
    var winnerColor by remember  { mutableStateOf(Color.Unspecified) }
    var pcWins by rememberSaveable  { mutableStateOf(0) }
    var userWins by rememberSaveable  { mutableStateOf(0) }
    fun score(){
        userScore += calculateScore(userDiceList)
        pcScore += calculateScore(pcDiceList)
        // If WON
        val result = checkWinner(userScore, pcScore, userTurns, pcTurns, 101)
        if (result.won) {
            winnerMessage = result.message
            winnerColor = result.color
            showWinDialog = true
            if (result.userWin) userWins++ else pcWins++
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
            )
       {
            Text("H: $userWins / C: $pcWins", style = MaterialTheme.typography.bodyLarge)
           ScoreBoard(userScore = userScore, pcScore = pcScore)
        }

        // PC Dice
        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
           Player(userName = "AndraiaPC", profilePicID = R.drawable.computeravatar)
            DiceRoll(pcDiceList, keepPCList)
        }

        // Human Dice
        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Player(userName = "You", profilePicID = R.drawable.useravatar)
            DiceRoll(userDiceList, keepUserList,
                onToggle = {index->keepUserList = keepUserList.toMutableList().apply { this[index] = !this[index] }})
        }


        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Roll: $rollCount / 3",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {

                Button(onClick = {
                    if (rollCount <3) {
                        userDiceList = userDiceList.mapIndexed { index, value ->
                            if (!keepUserList[index]) (1..6).random() else value
                            // If the Index of the Dice is NOT in the Keep List dice is re rolled
                        }
                        keepUserList = List(5) { false }
//                        pcDiceList = applyComputerStrategy(pcDiceList)

                        val (newPCList, newKeepList) = applyEfficientComputerStrategy(pcDiceList, pcScore, userScore)
                        pcDiceList = newPCList
                        keepPCList = newKeepList
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
//                            pcDiceList = applyComputerStrategy(pcDiceList)
                            val (newPCList, newKeepList) = applyEfficientComputerStrategy(pcDiceList, pcScore, userScore)
                            pcDiceList = newPCList
                            keepPCList = newKeepList

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
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for ((index, num) in diceList.withIndex()) {
                val notSelectedForReRoll = keepList?.getOrNull(index)
                Column(modifier = Modifier
                    .padding(8.dp)
                    .then(
                        if (onToggle != null)
                            Modifier.clickable { onToggle(index) }
                        else Modifier
                    )
                    .border(
                        width = 2.dp,
                        color = if (notSelectedForReRoll == true) Color.Yellow else Color.Transparent
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
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black)
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
            .border(2.dp, Color.DarkGray, RoundedCornerShape(50.dp))
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

data class Results(
   val message: String, val color: Color, val won: Boolean, val userWin: Boolean)

fun checkWinner(userScore: Int, pcScore: Int, userTurns: Int, pcTurns: Int, maxScore: Int): Results {
    // First check if PC or User achieved maxScore
    if (userScore >= maxScore && (pcScore < maxScore)) {
        return Results("You Win!", Color.Green, true, true)
    }
    if (pcScore >= maxScore && (userScore < maxScore)) {
        return Results("You Lose :/ Better Luck Next Time", Color.Red, true,false)
    }

    // In case of both achieving the same score we check who achieved it in less turns
    if (userScore >= maxScore && pcScore >= maxScore)  {
        // Compare turns
        return when {
            userTurns < pcTurns -> Results("You Win! That was Intense", Color.Green, true,true)
            pcTurns < userTurns -> Results("You Lose, Too Many Turns", Color.Red, true,false)
            userScore > pcScore -> Results("You Win!", Color.Green, true,true)
            pcScore > userScore -> Results("You Lose, Close Call", Color.Red, true,false)
            else -> {
                // Tie-breaker: both same score and same turns — sudden death roll
                val userLast = List(5) { (1..6).random() }.sum()
                val pcLast = List(5) { (1..6).random() }.sum()
                if (userLast > pcLast)
                    Results("You Win (Tie Breaker)!", Color.Green, true,true)
                else if (pcLast > userLast)
                    Results("You Lose (Tie Breaker)", Color.Red, true, false)
                else
                //if Tie again, keep rolling till Tie is Broken
                    checkWinner(userScore, pcScore, userTurns + 1, pcTurns + 1, 101)
            }
        }
    }
    return Results(" ", Color.Transparent, false,false)
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

fun applyComputerStrategy(currentList: List<Int>): Any {
    val reRoll = listOf(true, false).random()
    if (!reRoll) {
        return Pair(currentList,List(5) { true })
    }
    val noOfDiceToReRoll = (1..5).random() // decides how many Dice to re roll
    val diceToReRoll = (0..5).shuffled()
        .take(noOfDiceToReRoll) // picks a no of indexes from 1 to 5 randomly to be rerolled.
    val keepList = currentList.map { it !in diceToReRoll }
    val newList = currentList.mapIndexed { index, value ->
        if (index in diceToReRoll) (1..6).random() else value }// checks if the index is in
        // the diceToReRoll if it is generates a new value if not keeps the value as it is
    return Pair(newList, keepList);

}

fun applyEfficientComputerStrategy(currentList:List<Int>, pcScore: Int, userScore: Int): Pair<List<Int>, List<Boolean>>{
   // if the pcScore is higher or equal to userScore no need to re roll
    if (pcScore>=userScore){
        return Pair(currentList, List(5) { true })
    }
    val sortedList = currentList.sortedDescending()
    var reRollBegin = sortedList.size

    for (i in sortedList.indices){
        if (sortedList[i]<4){
            reRollBegin=i
            break
        }
    }

    val valuesToReRoll = if (reRollBegin < sortedList.size) {
        sortedList.subList(0, reRollBegin).toSet()
    } else {
        emptySet()
    }

    val keepList = currentList.map { it !in valuesToReRoll }
    val newList = currentList.map { die ->
        if (die in valuesToReRoll) (1..6).random() else die
    }
    return Pair(newList, keepList)

}

// Efficient Computer Strategy

// Strategy:

// The PC will check and compare its score with User score in the scoreboard.
// If the user score > pc score (in the scoreboard) it will decide to re roll.
// It will sort the dice list in descending order.
// It will loop through the list and check if there are any values which are lower than 4
// If it meets with one it selects it to re roll and adds to a re roll set. Those dices will be re rolled.
// If all is more than 4 in value it returns a empty set meaning none will be re rolled.

//Justification:

// By checking if the PC is behind in score in each round compared to user, makes it easy to understand if a re roll is needed.
// Since the PC doesn't know how the scores of the current round turns out it uses the score board's userScore and pcScore to compare.
// When it decides to re roll, It will first sort the dices in Descending Order.
// This way its easier to check the lower ones and choose them to re roll.
