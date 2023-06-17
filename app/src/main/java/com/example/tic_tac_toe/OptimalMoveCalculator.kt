package com.example.tic_tac_toe

class OptimalMoveCalculator(private val board: Board, private val player: PlayerType) {
    fun findOptimalMove(): Result? {
        var lastPlayerResult: PlayerResult? = null
        var result: Result? = null
        val rr = MutableList(board.size){
            MutableList<PlayerResult?>(board.size){
                null
            }
        }
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (!board.isEmpty(r, c)) continue
                board.putCellPlayer(r, c, player)
                val playerResult = !find(player.other)
                if (lastPlayerResult == null || playerResult < lastPlayerResult) {
                    lastPlayerResult = playerResult
                    result = Result(r, c)
                }
                rr[r][c] = playerResult
                board.clearCell(r, c)
            }
        }
        return result
    }

    private fun find(player: PlayerType): PlayerResult {
        if (board.isWin()) {
            return PlayerResult.Loss
        }
        var mergedResult: PlayerResult? = null
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (!board.isEmpty(r, c)) continue
                board.putCellPlayer(r, c, player)
                mergedResult = mergedResult.best(!find(player.other))
                board.clearCell(r, c)
            }
        }
        return mergedResult ?: PlayerResult.Draw
    }

    data class Result(val row: Int,val column: Int)
}

enum class PlayerResult {
    Win, Draw, Loss;
}

operator fun PlayerResult.not(): PlayerResult {
    return when (this) {
        PlayerResult.Win -> PlayerResult.Loss
        PlayerResult.Draw -> PlayerResult.Draw
        PlayerResult.Loss -> PlayerResult.Win
    }
}

fun PlayerResult?.best(other: PlayerResult): PlayerResult {
    return if (this == null || other < this) {
        other
    } else {
        this
    }
}

enum class PlayerType {
    Cross, Nought
}

val PlayerType.other
    get() = when (this) {
        PlayerType.Cross -> PlayerType.Nought
        PlayerType.Nought -> PlayerType.Cross
    }
