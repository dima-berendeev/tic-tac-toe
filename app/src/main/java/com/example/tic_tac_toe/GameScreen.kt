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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun GameScreen() {
    val vm: GameViewModel = viewModel()
    val state by vm.viewState.collectAsState()
    val turn = state.turn ?: return

    val board = state.board ?: return
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Turn: ", style = MaterialTheme.typography.h2)
            when (turn) {
                CellState.Nought -> Nought(modifier = Modifier.size(100.dp))
                CellState.Cross -> Cross(modifier = Modifier.size(100.dp))
                CellState.Empty -> throw IllegalStateException()
            }
        }
        GameBoard(board) { r, c -> vm.onBoardCellClick(r, c) }
    }
}

@Composable
fun GameBoard(board: List<List<CellState>>, onElementClick: (c: Int, r: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .aspectRatio(1f)
    ) {
        for (r in 0..2) {
            Row {
                for (c in 0..2) {
                    GameCell(
                        board[r][c],
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
fun GameCell(cellState: CellState, modifier: Modifier = Modifier) {
    Box(modifier) {
        when (cellState) {
            CellState.Nought -> {
                Nought(Modifier.fillMaxSize())
            }
            CellState.Cross -> {
                Cross(Modifier.fillMaxSize())
            }
            CellState.Empty -> {
                //do nothing
            }
        }
    }
}

@Composable
private fun Cross(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colors.secondary
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
    val color = MaterialTheme.colors.secondary
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
    val board = listOf(
        listOf(CellState.Cross, CellState.Nought, CellState.Empty),
        listOf(CellState.Empty, CellState.Empty, CellState.Empty),
        listOf(CellState.Cross, CellState.Nought, CellState.Cross),
    )
    GameBoard(board = board, { _, _ -> })
}
