@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tictactoe.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.R
import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player
import com.example.tictactoe.ui.theme.TicTacToeTheme

/**
 * The main composable function for the Tic Tac Toe app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeApp() {
    // Get the ViewModel and Collect the UI state from the ViewModel
    val viewModel: GameViewModel = viewModel()
    val boardState = viewModel.boardState.collectAsState().value
    val gameSetup = viewModel.gameSetup.collectAsState().value

    Scaffold(
        // Top app bar
        topBar = { GameAppBar() }
    ) { contentPadding ->
        // Column for arranging its children vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // If the game is not in progress, show the game start screen
            if (!gameSetup.gameInProgress) {
                GameStartScreen(
                    playerOneName = gameSetup.playerOne.name,
                    playerTwoName = gameSetup.playerTwo.name,
                    onPlayerOneNameChange = viewModel::onPlayerOneNameChange,
                    onPlayerTwoNameChange = viewModel::onPlayerTwoNameChange,
                    isXPlayerOneMark = viewModel.isXPlayerOneMark(),
                    updatePlayerOneMark = viewModel::updatePlayerMarks,
                    isNameError = gameSetup.isNameError,
                    onStartGame = viewModel::startGame
                )
            } else {
                // If the game is in progress, show the game board
                GameBoard(
                    board = boardState.marks,
                    onSquareClick = viewModel::handleMove,
                    isGameFinished = viewModel.isGameFinished(),
                    winner = viewModel.getWinner(),
                    onReset = viewModel::resetGame,
                    onOneMoreRound = viewModel::restartGame,
                    currentTurn = viewModel.currentTurn()
                )
            }
        }
    }
}

/**
 * Represents the top app bar for the Tic Tac Toe app.
 * @param modifier The modifier to be applied to the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameAppBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = modifier.size(50.dp)
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 24.sp,
                    textAlign= TextAlign.Center
                )
            }
        },
    )
}

@Composable
fun GameStartScreen(
    playerOneName: String,
    playerTwoName: String,
    onPlayerOneNameChange: (name: String) -> Unit,
    onPlayerTwoNameChange: (name: String) -> Unit,
    isXPlayerOneMark: Boolean,
    updatePlayerOneMark: (Mark) -> Unit,
    isNameError: Boolean,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlayerNameField(
        playerName = playerOneName,
        onPlayerNameChange = onPlayerOneNameChange,
        isNameError = isNameError,
        label = R.string.player_1_name_label,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    PlayerNameField(
        playerName = playerTwoName,
        onPlayerNameChange = onPlayerTwoNameChange,
        isNameError = isNameError,
        label = R.string.player_2_name_label,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )

    MarkSelectionRow(isXPlayerOneMark = isXPlayerOneMark, updatePlayerOneMark = updatePlayerOneMark)

    Text(
        text = stringResource(R.string.choose_mark),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    )

    Button(
        onClick = onStartGame,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))
    ) {
        Text(text = stringResource(R.string.start_game))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerNameField(
    playerName: String,
    onPlayerNameChange: (name: String) -> Unit,
    isNameError: Boolean,
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = playerName,
        onValueChange = { onPlayerNameChange(it) },
        label = { Text(text = stringResource(label)) },
        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = playerName) },
        isError = isNameError,
        singleLine = true,
        keyboardOptions = keyboardOptions
    )
}


@Composable
fun MarkSelectionRow(
    isXPlayerOneMark: Boolean,
    updatePlayerOneMark: (Mark) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_large)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(modifier = modifier) {
            Row(modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))) {
                MarkButton(
                    mark = Mark.O,
                    markImg = Mark.O.markImg,
                    onUpdateMark = { updatePlayerOneMark(it) },
                    elevated = !isXPlayerOneMark
                )

                Spacer(modifier = modifier.width(10.dp))
                MarkButton(
                    mark = Mark.X,
                    markImg = Mark.X.markImg,
                    onUpdateMark = { updatePlayerOneMark(it) },
                    elevated = isXPlayerOneMark
                )
            }
        }
    }
}


@Composable
fun Square(
    @DrawableRes markImg: Int?,
    onSquareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.card_size_lg))
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        shadowElevation = dimensionResource(id = R.dimen.card_elevation),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable { onSquareClick() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            markImg?.let {
                Image(
                    painter = painterResource(id = markImg),
                    contentDescription = null,
                    modifier = modifier
                        .padding(dimensionResource(id = R.dimen.padding_large)),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}


@Composable
fun MarkButton(
    mark: Mark,
    @DrawableRes markImg: Int,
    onUpdateMark: (Mark) -> Unit,
    elevated: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onUpdateMark(mark) }
            .size(dimensionResource(id = R.dimen.card_size)),
        shadowElevation = if (elevated) 5.dp else 0.dp,
        tonalElevation = if (elevated) 24.dp else 0.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Image(
            painter = painterResource(id = markImg), contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}


@Composable
fun GameBoard(
    board: List<Mark?>,
    onSquareClick: (Int) -> Unit,
    isGameFinished: Boolean,
    winner: String,
    onReset: () -> Unit,
    onOneMoreRound: () -> Unit,
    currentTurn: Player,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isGameFinished) {
            Text(
                text = winner,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                modifier = modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium))
            )
        }

        Card {
            repeat(3) { rowIdx ->
                Row(
                    modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small))
                ) {
                    repeat(3) { colIdx ->
                        val slotPosition = 3 * rowIdx + colIdx
                        Square(
                            markImg = board[slotPosition]?.markImg,
                            onSquareClick = { onSquareClick(slotPosition) })
                    }
                }
            }
        }

        if (isGameFinished) {
            GameController(onReset = onReset, onOneMoreRound = onOneMoreRound)
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Text(
                    text = "Current Turn: ${currentTurn.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = modifier.padding(end = dimensionResource(id = R.dimen.padding_medium))
                )
                Image(
                    painter = painterResource(id = currentTurn.mark.markImg),
                    contentDescription = null,
                    modifier = modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .size(dimensionResource(id = R.dimen.mark_size_sm)),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}


@Composable
fun GameController(
    onReset: () -> Unit,
    onOneMoreRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Button(onClick = onReset) {
            Text(text = "Reset")
        }
        Button(onClick = onOneMoreRound) {
            Text(text = "One More Round")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicToeAppPreview() {
    TicTacToeTheme {
        TicTacToeApp()
    }
}

@Preview(showBackground = true)
@Composable
fun GameBoardPreview() {
    TicTacToeTheme {
        GameBoard(
            board = List(9) { null },
            onSquareClick = { _ -> {} },
            isGameFinished = false,
            winner = "",
            onReset = { },
            onOneMoreRound = { },
            currentTurn = Player("Aqib", Mark.X)
        )
    }
}