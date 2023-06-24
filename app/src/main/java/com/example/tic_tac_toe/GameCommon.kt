package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable

data class PlayerMove(val row: Int, val col: Int)

enum class PlayerType {
    Cross, Nought
}

val PlayerType.other
    get() = when (this) {
        PlayerType.Cross -> PlayerType.Nought
        PlayerType.Nought -> PlayerType.Cross
    }

@Immutable
interface BoardSnapshot {
    val size: Int
    fun getCellPlayer(row: Int, col: Int): PlayerType?
}