package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
        repeat(5) {
            val gameFlow = gameFactory.get().launch(firstMove)
            firstMove = firstMove.anouther
            var boardSnapshot: BoardSnapshot? = null
            var playerWon: PlayerType? = null
            gameFlow.collect { round ->
                boardSnapshot = round.boardSnapshot
                when (round) {
                    is Round.Move -> {
                        if (round.playerType == autoPlayer) {
                            repeat(11) { n ->
                                val roundUiState = RoundUiState.AutoPlayerMove(round.playerType, n / 10f)
                                delay(100)
                                viewState.value = UiState(boardSnapshot, roundUiState)
                            }
                            val calculator = OptimalMoveCalculator(round.boardSnapshot.createBoard(), round.playerType)
                            val optimalMove = calculator.findOptimalMove()!!
                            round.moveAction(PlayerMove(optimalMove.row, optimalMove.column))
                        } else {
                            val playerMoveAction: (PlayerMove) -> Unit = { playerMove -> round.moveAction(playerMove) }
                            val roundUiState = RoundUiState.RealPlayerMove(round.playerType, playerMoveAction)
                            viewState.value = UiState(boardSnapshot, roundUiState)
                        }
                    }
                    is Round.Finished -> {
                        playerWon = round.playerType
                        return@collect
                    }
                }
            }
            val awaitDeferred = CompletableDeferred<Unit>()
            viewState.value = UiState(boardSnapshot, RoundUiState.Finished(playerWon, { awaitDeferred.complete(Unit) }))
            awaitDeferred.await()
        }
    }
}

@Immutable
data class UiState(
    val boardSnapshot: BoardSnapshot? = null,
    val roundState: RoundUiState? = null
) {


}

sealed interface RoundUiState {
    data class AutoPlayerMove(val playerType: PlayerType, val progress: Float) : RoundUiState
    data class RealPlayerMove(val playerType: PlayerType, val playerMoveAction: (PlayerMove) -> Unit) : RoundUiState
    data class Finished(val playerType: PlayerType?, val nextRoundAction: () -> Unit) : RoundUiState
}

class GameFactory {
    fun get(): Round {
        return Round { Board(3) }
    }
}
