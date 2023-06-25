package com.example.tic_tac_toe

import org.junit.Assert.*
import org.junit.Test

class OptimalMoveCalculatorTest {
    @Test
    fun test2() {
        val board = Board(3)
        board.putCellPlayer(0, 1, PlayerType.Nought)
        board.putCellPlayer(0, 0, PlayerType.Cross)
        board.putCellPlayer(1, 1, PlayerType.Nought)
        val calculator = OptimalMoveCalculator(board, PlayerType.Cross)
        val (row, column) = calculator.findOptimalMove()!!
        assertEquals(2 to 1, row to column)
    }

    @Test
    fun test1() {
        assertTrue(PlayerRoundResult.Win < PlayerRoundResult.Draw)
        assertTrue(PlayerRoundResult.Draw < PlayerRoundResult.Loss)
        assertTrue(PlayerRoundResult.Win < PlayerRoundResult.Loss)
        assertTrue(PlayerRoundResult.Draw > PlayerRoundResult.Win)
    }
}