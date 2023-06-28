package com.example.tic_tac_toe

import android.content.res.Resources.Theme
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun GameScreen() {
    val vm: GameViewModel = viewModel()
    val state by vm.viewState.collectAsState()
    val mode = state.roundState ?: return
    val board = state.uiBoard ?: return
    val score = state.scoreUiScore ?: return
    Column {
        GameHeader(mode)
        val alpha = if (state.roundState is RoundUiState.Finished) 0.5f else 1f
        GameBoard(board, modifier = Modifier.alpha(alpha)) { r, c ->
            if (mode is RoundUiState.RealPlayerMove) {
                mode.playerMoveAction(PlayerMove(r, c))
            }
        }
        Score(score)
    }
}

@Composable
fun ColumnScope.Score(score:UiScore) {
    Row(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp)) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h1.copy(fontWeight = FontWeight.ExtraBold)) {
            Text(text = score.cross.toString())
            Text(text = " : ")
            Text(text = score.nought.toString())
        }
    }
}

@Composable
private fun GameHeader(roundState: RoundUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (roundState) {
            is RoundUiState.AutoPlayerMove -> {
                Text(text = "Computer's move", style = MaterialTheme.typography.h4)
                LinearProgressIndicator(progress = roundState.progress)
            }
            is RoundUiState.RealPlayerMove -> {
                Text(text = "Your move", style = MaterialTheme.typography.h4)
            }
            is RoundUiState.Finished -> {
                when (roundState.playerType) {
                    PlayerType.Cross -> Text(text = "Cross won", style = MaterialTheme.typography.h4)
                    PlayerType.Nought -> Text(text = "Nought won", style = MaterialTheme.typography.h4)
                    null -> Text(text = "Draw", style = MaterialTheme.typography.h4)
                }
                Button(
                    onClick = { roundState.nextRoundAction() }
                ) {
                    Text(text = "Next round", style = MaterialTheme.typography.button)
                }
            }
        }
    }
}

@Composable
fun GameBoard(board: UiBoard, modifier: Modifier = Modifier, onElementClick: (c: Int, r: Int) -> Unit) {
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
                        board.getCell(r, c),
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameCell(cellState: UiBoard.Cell, modifier: Modifier = Modifier) {
    Box(modifier) {
        AnimatedContent(
            targetState = cellState,
            transitionSpec = { scaleIn() with scaleOut() }) { contentState ->
            when (contentState) {
                UiBoard.Cell.Cross -> Cross(Modifier.fillMaxSize())
                UiBoard.Cell.Empty -> {
                    Box(modifier.fillMaxSize())
                }
                UiBoard.Cell.Nought -> Nought(Modifier.fillMaxSize())
                is UiBoard.Cell.Possible -> {
                    val color = when (contentState.playerRoundResult) {
                        PlayerRoundResult.Win -> Color.Magenta
                        PlayerRoundResult.Draw -> Color.Gray
                        PlayerRoundResult.Loss -> Color.Black
                    }
                    Possible(Modifier.fillMaxSize(), color)
                }
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

@Composable
private fun Possible(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        inset(size.width / 5) {
            val size: Float = size.width
            drawCircle(color = color, radius = size / 4f, style = Fill)
        }
    }
}

//@Preview
//@Composable
//fun BoardPreview() {
//    val content: MutableList<MutableList<PlayerType?>> = mutableListOf(
//        mutableListOf(PlayerType.Cross, PlayerType.Nought, null),
//        mutableListOf(null, null, null),
//        mutableListOf(PlayerType.Cross, PlayerType.Nought, PlayerType.Cross),
//    )
//    val board = Board(3, content).getSnapshot()
//    GameBoard(board = board) { _, _ -> }
//}
