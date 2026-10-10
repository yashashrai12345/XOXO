package com.example.logic

import com.example.model.BoardSize
import com.example.model.CellPosition
import com.example.model.Player
import com.example.model.WinningLine

object GameEngine {

    fun createEmptyBoard(dimension: Int): List<Player?> {
        return List(dimension * dimension) { null }
    }

    fun getCell(board: List<Player?>, dimension: Int, row: Int, col: Int): Player? {
        if (row !in 0 until dimension || col !in 0 until dimension) return null
        return board[row * dimension + col]
    }

    fun setCell(
        board: List<Player?>,
        dimension: Int,
        row: Int,
        col: Int,
        player: Player
    ): List<Player?> {
        val index = row * dimension + col
        if (index !in board.indices) return board
        val mutable = board.toMutableList()
        mutable[index] = player
        return mutable
    }

    fun isMoveValid(board: List<Player?>, dimension: Int, row: Int, col: Int): Boolean {
        if (row !in 0 until dimension || col !in 0 until dimension) return false
        return board[row * dimension + col] == null
    }

    fun isBoardFull(board: List<Player?>): Boolean {
        return board.none { it == null }
    }

    fun checkWinningSequence(
        board: List<Player?>,
        dimension: Int,
        winLength: Int
    ): WinningLine? {
        // 1. Horizontal check
        for (r in 0 until dimension) {
            for (c in 0..dimension - winLength) {
                val first = getCell(board, dimension, r, c) ?: continue
                var isWin = true
                val cells = ArrayList<CellPosition>(winLength)
                cells.add(CellPosition(r, c))
                for (offset in 1 until winLength) {
                    if (getCell(board, dimension, r, c + offset) != first) {
                        isWin = false
                        break
                    }
                    cells.add(CellPosition(r, c + offset))
                }
                if (isWin) {
                    return WinningLine(cells, first)
                }
            }
        }

        // 2. Vertical check
        for (c in 0 until dimension) {
            for (r in 0..dimension - winLength) {
                val first = getCell(board, dimension, r, c) ?: continue
                var isWin = true
                val cells = ArrayList<CellPosition>(winLength)
                cells.add(CellPosition(r, c))
                for (offset in 1 until winLength) {
                    if (getCell(board, dimension, r + offset, c) != first) {
                        isWin = false
                        break
                    }
                    cells.add(CellPosition(r + offset, c))
                }
                if (isWin) {
                    return WinningLine(cells, first)
                }
            }
        }

        // 3. Diagonal (top-left to bottom-right)
        for (r in 0..dimension - winLength) {
            for (c in 0..dimension - winLength) {
                val first = getCell(board, dimension, r, c) ?: continue
                var isWin = true
                val cells = ArrayList<CellPosition>(winLength)
                cells.add(CellPosition(r, c))
                for (offset in 1 until winLength) {
                    if (getCell(board, dimension, r + offset, c + offset) != first) {
                        isWin = false
                        break
                    }
                    cells.add(CellPosition(r + offset, c + offset))
                }
                if (isWin) {
                    return WinningLine(cells, first)
                }
            }
        }

        // 4. Anti-Diagonal (top-right to bottom-left)
        for (r in 0..dimension - winLength) {
            for (c in (winLength - 1) until dimension) {
                val first = getCell(board, dimension, r, c) ?: continue
                var isWin = true
                val cells = ArrayList<CellPosition>(winLength)
                cells.add(CellPosition(r, c))
                for (offset in 1 until winLength) {
                    if (getCell(board, dimension, r + offset, c - offset) != first) {
                        isWin = false
                        break
                    }
                    cells.add(CellPosition(r + offset, c - offset))
                }
                if (isWin) {
                    return WinningLine(cells, first)
                }
            }
        }

        return null
    }
}
