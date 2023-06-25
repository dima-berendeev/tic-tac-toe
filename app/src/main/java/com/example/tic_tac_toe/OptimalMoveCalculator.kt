package com.example.tic_tac_toe

class OptimalMoveCalculator(private val board: Board, private val player: PlayerType) {
    fun findOptimalMove(): Result {
        val allMoves = MutableList(board.size) {
            MutableList<PlayerRoundResult?>(board.size) { null }
        }
        var lastPlayerRoundResult: PlayerRoundResult? = null
        var bestMove: PlayerMove? = null
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (!board.isEmpty(r, c)) continue
                board.putCellPlayer(r, c, player)
                val playerResult = !find(player.another)
                allMoves[r][c] = playerResult
                if (lastPlayerRoundResult == null || playerResult < lastPlayerRoundResult) {
                    lastPlayerRoundResult = playerResult
                    bestMove = PlayerMove(r, c)
                }
                board.clearCell(r, c)
            }
        }
        return Result(bestMove, allMoves)
    }

    private fun find(player: PlayerType): PlayerRoundResult {
        if (board.isWin()) {
            return PlayerRoundResult.Loss
        }
        var mergedResult: PlayerRoundResult? = null
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (!board.isEmpty(r, c)) continue
                board.putCellPlayer(r, c, player)
                mergedResult = mergedResult.best(!find(player.another))
                board.clearCell(r, c)
            }
        }
        return mergedResult ?: PlayerRoundResult.Draw
    }

    data class Result(val optimalMove: PlayerMove?, val allMoves: List<List<PlayerRoundResult?>>)
}

enum class PlayerRoundResult {
    Win, Draw, Loss;
}

private operator fun PlayerRoundResult.not(): PlayerRoundResult {
    return when (this) {
        PlayerRoundResult.Win -> PlayerRoundResult.Loss
        PlayerRoundResult.Draw -> PlayerRoundResult.Draw
        PlayerRoundResult.Loss -> PlayerRoundResult.Win
    }
}

private fun PlayerRoundResult?.best(other: PlayerRoundResult): PlayerRoundResult {
    return if (this == null || other < this) {
        other
    } else {
        this
    }
}
