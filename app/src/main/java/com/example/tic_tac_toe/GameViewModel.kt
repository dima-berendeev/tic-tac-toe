package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    val viewState = MutableStateFlow(ViewState())
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
                val viewStateMode = when (round) {
                    is Round.Move -> {
                        if (round.playerType == autoPlayer) {
                            ViewState.AutoPlayerMove(round.playerType)
                        } else {
                            val playerMoveAction: (PlayerMove) -> Unit = { playerMove -> round.moveAction(playerMove) }
                            ViewState.RealPlayerMove(round.playerType, playerMoveAction)
                        }
                    }
                    is Round.Finished -> {
                        playerWon = round.playerType
                        ViewState.Finished(round.playerType, {})
                    }
                }
                viewState.value = (ViewState(boardSnapshot, viewStateMode))

                if (round is Round.Move && round.playerType == autoPlayer) {
                    val calculator = OptimalMoveCalculator(round.boardSnapshot.createBoard(), round.playerType)
                    delay(2000)
                    val optimalMove = calculator.findOptimalMove()!!
                    round.moveAction(PlayerMove(optimalMove.row, optimalMove.column))
                }
            }
            val awaitDeferred = CompletableDeferred<Unit>()
            viewState.value = ViewState(boardSnapshot, ViewState.Finished(playerWon, { awaitDeferred.complete(Unit) }))
            awaitDeferred.await()
        }
    }
}

@Immutable
data class ViewState(
    val boardSnapshot: BoardSnapshot? = null,
    val mode: Mode? = null
) {

    sealed interface Mode
    data class AutoPlayerMove(val playerType: PlayerType) : Mode
    data class RealPlayerMove(val playerType: PlayerType, val playerMoveAction: (PlayerMove) -> Unit) : Mode
    data class Finished(val playerType: PlayerType?, val nextRoundAction: () -> Unit) : Mode
}

class GameFactory {
    fun get(): Round {
        return Round { Board(3) }
    }
}
