package com.example.tic_tac_toe

class OptimalMoveCalculator(private val board: Board, private val player: PlayerType) {
    fun findOptimalMove(): Result? {
        var lastPlayerResult: PlayerResult? = null
        var result: Result? = null
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (!board.isEmpty(r, c)) continue
                board.putCellPlayer(r, c, player)
                val playerResult = !find(player.anouther)
                if (lastPlayerResult == null || playerResult < lastPlayerResult) {
                    lastPlayerResult = playerResult
                    result = Result(r, c)
                }
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
                mergedResult = mergedResult.best(!find(player.anouther))
                board.clearCell(r, c)
            }
        }
        return mergedResult ?: PlayerResult.Draw
    }

    data class Result(val row: Int, val column: Int)
}

private enum class PlayerResult {
    Win, Draw, Loss;
}

private operator fun PlayerResult.not(): PlayerResult {
    return when (this) {
        PlayerResult.Win -> PlayerResult.Loss
        PlayerResult.Draw -> PlayerResult.Draw
        PlayerResult.Loss -> PlayerResult.Win
    }
}

private fun PlayerResult?.best(other: PlayerResult): PlayerResult {
    return if (this == null || other < this) {
        other
    } else {
        this
    }
}
