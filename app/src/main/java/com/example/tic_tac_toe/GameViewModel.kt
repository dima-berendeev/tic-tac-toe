package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

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
        repeat(5) {
            val gameFlow = gameFactory.get().launch()
            var boardSnapshot: BoardSnapshot? = null
            var playerWon: PlayerType? = null
            gameFlow.collect { gameState ->
                boardSnapshot = gameState.boardSnapshot
                val viewStateMode = when (gameState.mode) {
                    is Game.Mode.Move -> {
                        if (gameState.mode.playerType == autoPlayer) {
                            ViewState.AutoPlayerMove(gameState.mode.playerType)
                        } else {
                            val playerMoveAction: (PlayerMove) -> Unit = { playerMove -> gameState.mode.moveAction(playerMove) }
                            ViewState.RealPlayerMove(gameState.mode.playerType, playerMoveAction)
                        }
                    }
                    is Game.Mode.Draw -> {
                        ViewState.Finished(null, {})
                    }
                    is Game.Mode.Win -> {
                        playerWon = gameState.mode.playerType
                        ViewState.Finished(gameState.mode.playerType, {})
                    }
                }
                viewState.value = (ViewState(boardSnapshot, viewStateMode))

                if (gameState.mode is Game.Mode.Move && gameState.mode.playerType == autoPlayer) {
                    val calculator = OptimalMoveCalculator(gameState.boardSnapshot.createBoard(), gameState.mode.playerType)
                    delay(2000)
                    val optimalMove = calculator.findOptimalMove()!!
                    gameState.mode.moveAction(PlayerMove(optimalMove.row, optimalMove.column))
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
    data class Finished(val playerType: PlayerType?, val restartAction: () -> Unit) : Mode
}

class GameFactory {
    fun get(): Game {
        return Game { Board(3) }
    }
}
