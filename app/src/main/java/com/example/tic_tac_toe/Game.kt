package com.example.tic_tac_toe

class Game {
    private val boardSize = 3
    private var board = Board(boardSize)
    private var mode: Mode = Mode.NoughtMove
    private var calculator = OptimalMoveCalculator(board,PlayerType.Cross)

    fun getBoardSnapshot(): List<List<PlayerType?>> {
        return MutableList(boardSize) { r ->
            MutableList(boardSize) { c ->
                board.getCellPlayer(r, c)
            }
        }
    }

    fun getMode(): Mode {
        return mode
    }

    fun reset() {
        board = Board(boardSize)
        mode = Mode.NoughtMove
        calculator = OptimalMoveCalculator(board,PlayerType.Cross)
    }

    fun makeMoveAutomatically(){
        if(board.isDraw()) return
        val result = calculator.findOptimalMove()?:throw IllegalStateException()
        makeMove(result.row,result.column)
    }

    fun makeMove(r: Int, c: Int) {
        if (!board.isEmpty(r, c)) return
        when (mode) {
            Mode.CrossMove -> {
                board.putCellPlayer(r, c, PlayerType.Cross)
            }
            Mode.NoughtMove -> {
                board.putCellPlayer(r, c, PlayerType.Nought)
            }
            else -> {
                // game is finished
                return
            }
        }
        mode = when {
            board.isWin() -> {
                when (mode) {
                    Mode.CrossMove -> Mode.CrossWin
                    Mode.NoughtMove -> Mode.NoughtWin
                    else -> throw IllegalStateException()
                }
            }
            board.isDraw() -> {
                Mode.Draw
            }
            else -> {
                when (mode) {
                    Mode.CrossMove -> Mode.NoughtMove
                    Mode.NoughtMove -> Mode.CrossMove
                    else -> throw IllegalStateException()
                }
            }
        }
    }

    enum class Mode {
        CrossMove,
        NoughtMove,
        CrossWin,
        NoughtWin,
        Draw;

        fun isFinished(): Boolean {
            return this != CrossMove && this != NoughtMove
        }
    }
}
