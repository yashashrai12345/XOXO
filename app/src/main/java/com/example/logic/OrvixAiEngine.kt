package com.example.logic

import com.example.model.BoardSize
import com.example.model.CellPosition
import com.example.model.Player
import kotlin.math.abs
import kotlin.random.Random

object OrvixAiEngine {

    fun computeMove(
        board: List<Player?>,
        boardSize: BoardSize,
        aiPlayer: Player = Player.O
    ): CellPosition {
        val dim = boardSize.dimension
        val winLen = boardSize.winLength
        val opponent = aiPlayer.opponent()

        val emptyCells = ArrayList<CellPosition>()
        for (r in 0 until dim) {
            for (c in 0 until dim) {
                if (GameEngine.isMoveValid(board, dim, r, c)) {
                    emptyCells.add(CellPosition(r, c))
                }
            }
        }

        if (emptyCells.isEmpty()) return CellPosition(0, 0)
        if (emptyCells.size == dim * dim) {
            // First move of the game: choose center or nearby
            val center = dim / 2
            return CellPosition(center, center)
        }

        // 1. Immediate Win Check for AI
        for (cell in emptyCells) {
            val simulated = GameEngine.setCell(board, dim, cell.row, cell.col, aiPlayer)
            if (GameEngine.checkWinningSequence(simulated, dim, winLen) != null) {
                return cell
            }
        }

        // 2. Immediate Block Check (Opponent's winning move)
        for (cell in emptyCells) {
            val simulated = GameEngine.setCell(board, dim, cell.row, cell.col, opponent)
            if (GameEngine.checkWinningSequence(simulated, dim, winLen) != null) {
                return cell
            }
        }

        // 3. For 3x3 Classic Board: Specialized Tactical Heuristics
        if (dim == 3 && winLen == 3) {
            return compute3x3Move(board, emptyCells, aiPlayer, opponent)
        }

        // 4. For larger boards (6x6, 9x9, 11x11): Deep Heuristic Evaluation
        return computeLargerBoardMove(board, dim, winLen, emptyCells, aiPlayer, opponent)
    }

    private fun compute3x3Move(
        board: List<Player?>,
        emptyCells: List<CellPosition>,
        aiPlayer: Player,
        opponent: Player
    ): CellPosition {
        val dim = 3
        val winLen = 3

        // 3a. Check if AI can create a fork (two winning paths)
        for (cell in emptyCells) {
            val sim = GameEngine.setCell(board, dim, cell.row, cell.col, aiPlayer)
            var threats = 0
            for (next in emptyCells) {
                if (next == cell) continue
                val sim2 = GameEngine.setCell(sim, dim, next.row, next.col, aiPlayer)
                if (GameEngine.checkWinningSequence(sim2, dim, winLen) != null) {
                    threats++
                }
            }
            if (threats >= 2) {
                return cell
            }
        }

        // 3b. Block opponent's fork
        val opponentForks = ArrayList<CellPosition>()
        for (cell in emptyCells) {
            val sim = GameEngine.setCell(board, dim, cell.row, cell.col, opponent)
            var threats = 0
            for (next in emptyCells) {
                if (next == cell) continue
                val sim2 = GameEngine.setCell(sim, dim, next.row, next.col, opponent)
                if (GameEngine.checkWinningSequence(sim2, dim, winLen) != null) {
                    threats++
                }
            }
            if (threats >= 2) {
                opponentForks.add(cell)
            }
        }

        if (opponentForks.size == 1) {
            return opponentForks.first()
        } else if (opponentForks.size > 1) {
            // Force opponent to defend instead of forking: find a move that creates an immediate threat
            for (cell in emptyCells) {
                val sim = GameEngine.setCell(board, dim, cell.row, cell.col, aiPlayer)
                for (next in emptyCells) {
                    if (next == cell) continue
                    val sim2 = GameEngine.setCell(sim, dim, next.row, next.col, aiPlayer)
                    if (GameEngine.checkWinningSequence(sim2, dim, winLen) != null && !opponentForks.contains(next)) {
                        return cell
                    }
                }
            }
            return opponentForks.first()
        }

        // 3c. Take Center if available
        val center = CellPosition(1, 1)
        if (emptyCells.contains(center)) {
            return center
        }

        // 3d. Take opposite corner if opponent is in corner
        val corners = listOf(
            CellPosition(0, 0), CellPosition(0, 2),
            CellPosition(2, 0), CellPosition(2, 2)
        )
        val oppositePairs = listOf(
            CellPosition(0, 0) to CellPosition(2, 2),
            CellPosition(0, 2) to CellPosition(2, 0),
            CellPosition(2, 0) to CellPosition(0, 2),
            CellPosition(2, 2) to CellPosition(0, 0)
        )
        for ((c1, c2) in oppositePairs) {
            if (GameEngine.getCell(board, 3, c1.row, c1.col) == opponent && emptyCells.contains(c2)) {
                return c2
            }
        }

        // 3e. Take any empty corner
        val availableCorners = corners.filter { emptyCells.contains(it) }
        if (availableCorners.isNotEmpty()) {
            return availableCorners.random()
        }

        // 3f. Fallback to any remaining cell
        return emptyCells.random()
    }

    private fun computeLargerBoardMove(
        board: List<Player?>,
        dim: Int,
        winLen: Int,
        emptyCells: List<CellPosition>,
        aiPlayer: Player,
        opponent: Player
    ): CellPosition {
        // Find cells adjacent to occupied marks to keep search fast and focused
        val activeCandidates = emptyCells.filter { cell ->
            hasAdjacentMark(board, dim, cell.row, cell.col, distance = 2)
        }.ifEmpty { emptyCells }

        var maxScore = Double.NEGATIVE_INFINITY
        val scoredMoves = ArrayList<Pair<CellPosition, Double>>()

        val center = dim / 2.0

        for (cell in activeCandidates) {
            val r = cell.row
            val c = cell.col

            // Offensive score: evaluation of AI's lines through (r, c)
            val offense = evaluatePositionForPlayer(board, dim, winLen, r, c, aiPlayer, opponent)
            // Defensive score: evaluation of Opponent's lines through (r, c)
            val defense = evaluatePositionForPlayer(board, dim, winLen, r, c, opponent, aiPlayer)

            // Slight weight to center position for open-board control
            val distToCenter = abs(r - center) + abs(c - center)
            val centerBonus = (dim - distToCenter) * 0.5

            // Total tactical score: defense weighted slightly higher to avoid sneaky opponent wins
            val totalScore = (offense * 1.0) + (defense * 1.15) + centerBonus

            scoredMoves.add(cell to totalScore)
            if (totalScore > maxScore) {
                maxScore = totalScore
            }
        }

        // To add varied, natural, but high-quality decisions:
        // Pick among top candidate moves within 5% of max score
        val topCandidates = scoredMoves.filter { it.second >= maxScore * 0.95 }
        return topCandidates.randomOrNull()?.first ?: activeCandidates.first()
    }

    private fun hasAdjacentMark(
        board: List<Player?>,
        dim: Int,
        row: Int,
        col: Int,
        distance: Int
    ): Boolean {
        for (dr in -distance..distance) {
            for (dc in -distance..distance) {
                if (dr == 0 && dc == 0) continue
                val r = row + dr
                val c = col + dc
                if (r in 0 until dim && c in 0 until dim) {
                    if (GameEngine.getCell(board, dim, r, c) != null) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun evaluatePositionForPlayer(
        board: List<Player?>,
        dim: Int,
        winLen: Int,
        row: Int,
        col: Int,
        targetPlayer: Player,
        otherPlayer: Player
    ): Double {
        var score = 0.0
        val directions = listOf(
            0 to 1,   // Horizontal
            1 to 0,   // Vertical
            1 to 1,   // Diagonal
            1 to -1   // Anti-diagonal
        )

        for ((dr, dc) in directions) {
            // Count consecutive marks in positive direction
            var count = 1
            var openEnds = 0

            var rPos = row + dr
            var cPos = col + dc
            var openPos = false
            while (rPos in 0 until dim && cPos in 0 until dim) {
                val mark = GameEngine.getCell(board, dim, rPos, cPos)
                if (mark == targetPlayer) {
                    count++
                } else if (mark == null) {
                    openPos = true
                    break
                } else {
                    break
                }
                rPos += dr
                cPos += dc
            }
            if (openPos) openEnds++

            // Count consecutive marks in negative direction
            var rNeg = row - dr
            var cNeg = col - dc
            var openNeg = false
            while (rNeg in 0 until dim && cNeg in 0 until dim) {
                val mark = GameEngine.getCell(board, dim, rNeg, cNeg)
                if (mark == targetPlayer) {
                    count++
                } else if (mark == null) {
                    openNeg = true
                    break
                } else {
                    break
                }
                rNeg -= dr
                cNeg -= dc
            }
            if (openNeg) openEnds++

            // Weight sequences based on proximity to winLength
            if (count >= winLen) {
                score += 10000.0
            } else if (count == winLen - 1) {
                // Threat 1 away from victory
                score += if (openEnds >= 2) 5000.0 else if (openEnds == 1) 1200.0 else 0.0
            } else if (count == winLen - 2) {
                // Developing threat 2 away from victory
                score += if (openEnds >= 2) 400.0 else if (openEnds == 1) 120.0 else 0.0
            } else if (count == winLen - 3 && count > 1) {
                score += if (openEnds >= 2) 80.0 else 20.0
            } else {
                score += count * 5.0 * openEnds
            }
        }

        return score
    }
}
