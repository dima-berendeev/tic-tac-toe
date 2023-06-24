package com.example.tic_tac_toe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun GameScreen() {
    val vm: GameViewModel = viewModel()
    val state by vm.viewState.collectAsState()
    val mode = state.mode ?: return
    val board = state.boardSnapshot ?: return
    Column {
        GameHeader(mode) { vm.onResetClick() }
        val alpha = if (state.isFinished()) 0.5f else 1f
        GameBoard(board, modifier = Modifier.alpha(alpha)) { r, c ->
            if (mode is ViewState.RealPlayerMove) {
                mode.playerMoveAction(PlayerMove(r,c))
            }
        }
    }
}

@Composable
private fun GameHeader(mode: ViewState.Mode, onRestartClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (mode) {
            is ViewState.AutoPlayerMove -> {
                when (mode.playerType) {
                    PlayerType.Cross -> {
                        Text(text = "Turn of Cross", style = MaterialTheme.typography.h4)
                    }
                    PlayerType.Nought -> {
                        Text(text = "Turn of Nought", style = MaterialTheme.typography.h4)
                    }
                }
            }
            is ViewState.RealPlayerMove -> {
                when (mode.playerType) {
                    PlayerType.Cross -> {
                        Text(text = "Turn of Cross", style = MaterialTheme.typography.h4)
                    }
                    PlayerType.Nought -> {
                        Text(text = "Turn of Nought", style = MaterialTheme.typography.h4)
                    }
                }
            }
            ViewState.Draw -> {
                Text(text = "Draw", style = MaterialTheme.typography.h4)
            }
            is ViewState.Win -> {
                when (mode.playerType) {
                    PlayerType.Cross -> {
                        Text(text = "Cross won", style = MaterialTheme.typography.h4)
                    }
                    PlayerType.Nought -> {
                        Text(text = "Nought won", style = MaterialTheme.typography.h4)
                    }
                }
            }
        }

//        if (mode !is Game.Mode.Move) {
//            Button(
//                onClick = { onRestartClicked() }
//            ) {
//                Text(text = "Restart", style = MaterialTheme.typography.button)
//            }
//        }
    }
}

@Composable
fun GameBoard(board: BoardSnapshot, modifier: Modifier = Modifier, onElementClick: (c: Int, r: Int) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0277BD))
            .aspectRatio(1f)
    ) {
        for (r in 0..2) {
            Row {
                for (c in 0..2) {
                    GameCell(
                        board.getCellPlayer(r, c),
                        modifier = Modifier
                            .fillMaxWidth(1f / (3 - c))
                            .aspectRatio(1f)
                            .padding(10.dp)
                            .background(Color.White)
                            .clickable(indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                onElementClick(r, c)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCell(cellState: PlayerType?, modifier: Modifier = Modifier) {
    Box(modifier) {
        when (cellState) {

            PlayerType.Cross -> Cross(Modifier.fillMaxSize())
            PlayerType.Nought -> Nought(Modifier.fillMaxSize())
            null -> {
                //do nothing
            }
        }
    }
}

@Composable
private fun Cross(modifier: Modifier = Modifier) {
    val color = Color(0xFFBF360C)
    Canvas(modifier = modifier) {
        inset(size.width / 5) {
            val strokeWidth = size.width / 5
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(size.width, 0f),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun Nought(modifier: Modifier = Modifier) {
    val color = Color(0xFF1B5E20)
    Canvas(modifier = modifier) {
        inset(size.width / 5) {
            val size: Float = size.width
            val strokeWidth = size / 5
            drawCircle(color = color, radius = size / 2, style = Stroke(width = strokeWidth))
        }
    }
}

@Preview
@Composable
fun BoardPreview() {
    val content: MutableList<MutableList<PlayerType?>> = mutableListOf(
        mutableListOf(PlayerType.Cross, PlayerType.Nought, null),
        mutableListOf(null, null, null),
        mutableListOf(PlayerType.Cross, PlayerType.Nought, PlayerType.Cross),
    )
    val board = Board(3, content).getSnapshot()
    GameBoard(board = board) { _, _ -> }
}
