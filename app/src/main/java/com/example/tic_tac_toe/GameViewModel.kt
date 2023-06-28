package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {
    val viewState = MutableStateFlow(UiState())
    private val gameFactory: GameFactory = GameFactory()
    private val autoPlayer = PlayerType.Nought

    init {
        viewModelScope.launch {
            launchMainCycle()
        }
    }

    private suspend fun launchMainCycle() {
        var firstMove = PlayerType.Nought
        var crossScore = 0
        var noughtScore = 0
        while(true) {
            val gameFlow = gameFactory.get().launch(firstMove)
            firstMove = firstMove.another
            var uiBoard: UiBoard? = null
            var playerWon: PlayerType? = null
            fun updateRound(roundUiState: RoundUiState){
                viewState.value = UiState(uiBoard, UiScore(cross = crossScore, nought = noughtScore),roundUiState)
            }
            gameFlow.collect { round ->
                uiBoard = round.boardSnapshot.toUiBoardContent().let { UiBoardImpl(3, it) }

                when (round) {
                    is Round.Move -> {

                        updateRound( RoundUiState.AutoPlayerMove(round.playerType, 1f))
                        if (round.playerType == autoPlayer) {

                            val calculator = withContext(Dispatchers.Default) {
                                OptimalMoveCalculator(round.boardSnapshot.createBoard(), round.playerType)
                            }

                            val calculatorResult = calculator.findOptimalMove()

                            // show all variants
                            getAllMovesFlow(round.boardSnapshot, calculatorResult.allMoves).collect { uiBoardWithForecast ->
                                uiBoard = uiBoardWithForecast
                                updateRound( RoundUiState.AutoPlayerMove(round.playerType, 1f))
                                delay(20)
                            }
                            delay(1000)

                            round.moveAction(calculatorResult.optimalMove!!)
                        } else {
                            val playerMoveAction: (PlayerMove) -> Unit = { playerMove -> round.moveAction(playerMove) }
                            val roundUiState = RoundUiState.RealPlayerMove(round.playerType, playerMoveAction)
                            updateRound(roundUiState)
                        }
                    }
                    is Round.Finished -> {
                        playerWon = round.playerType
                        when(playerWon){
                            PlayerType.Cross -> crossScore++
                            PlayerType.Nought -> noughtScore++
                            null -> {
                                crossScore++
                                noughtScore++
                            }
                        }
                        return@collect
                    }
                }
            }
            val awaitDeferred = CompletableDeferred<Unit>()
            updateRound(RoundUiState.Finished(playerWon, { awaitDeferred.complete(Unit) }))
            awaitDeferred.await()
        }
    }

    private fun BoardSnapshot.toUiBoardContent() = MutableList(3) { row ->
        MutableList(3) { col ->
            when (getCellPlayer(row, col)) {
                PlayerType.Cross -> UiBoard.Cell.Cross
                PlayerType.Nought -> UiBoard.Cell.Nought
                null -> UiBoard.Cell.Empty
            }
        }
    }

    private suspend fun getAllMovesFlow(
        boardSnapshot: BoardSnapshot,
        allMoves: List<List<PlayerRoundResult?>>
    ): Flow<UiBoard> = flow {
        val mutableUiBoard = boardSnapshot.toUiBoardContent()
        fun makeUiBoardCopy() = List(3) { row -> List(3) { col -> mutableUiBoard[row][col] } }
        for (row in 0..2) {
            for (col in 0..2) {
                val playerRoundResult = allMoves[row][col] ?: continue
                mutableUiBoard[row][col] = UiBoard.Cell.Possible(playerRoundResult)
                emit(UiBoardImpl(3, makeUiBoardCopy()))
            }
        }
    }
}

@Immutable
data class UiState(
    val uiBoard: UiBoard? = null,
    val scoreUiScore:UiScore? = null,
    val roundState: RoundUiState? = null
)

data class UiScore(val cross:Int, val nought:Int)

@Immutable
interface UiBoard {
    fun getCell(row: Int, col: Int): Cell

    sealed interface Cell {
        object Empty : Cell
        object Cross : Cell
        object Nought : Cell
        data class Possible(val playerRoundResult: PlayerRoundResult) : Cell
    }
}

private class UiBoardImpl(private val size: Int, private val list: List<List<UiBoard.Cell>>) : UiBoard {
    override fun getCell(row: Int, col: Int): UiBoard.Cell {
        return list[row][col]
    }
}

sealed interface RoundUiState {
    data class AutoPlayerMove(val playerType: PlayerType, val progress: Float) : RoundUiState
    data class RealPlayerMove(val playerType: PlayerType, val playerMoveAction: (PlayerMove) -> Unit) : RoundUiState
    data class Finished(val playerType: PlayerType?, val nextRoundAction: () -> Unit) : RoundUiState
}

class GameFactory {
    fun get(): Round {
        return Round { Board(size = 3) }
    }
}
