package com.example.tictactoe.ui

import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player


data class BoardState(
    val marks: List<Mark?> = List(9) { null },
    val currentTurn: Mark = Mark.X,
    val winner: Mark? = null
) {

    fun markFor(idx: Int): Mark? {
        return marks[idx]
    }
}


data class GameSetup(
    val playerOne: Player = Player("", Mark.X),
    val playerTwo: Player = Player("", Mark.O),
    val gameInProgress: Boolean = false,
    val isNameError: Boolean = false
)


fun GameSetup.updateMarks(playerOneMark: Mark) = copy(
    playerOne = playerOne.copy(mark = playerOneMark),
    playerTwo = playerTwo.copy(mark = if (playerOneMark == Mark.X) Mark.O else Mark.X)
)