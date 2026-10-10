package com.example

import com.example.logic.GameEngine
import com.example.logic.OrvixAiEngine
import com.example.model.BoardSize
import com.example.model.CellPosition
import com.example.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `test 3x3 horizontal win detection`() {
        val dim = 3
        var board = GameEngine.createEmptyBoard(dim)
        board = GameEngine.setCell(board, dim, 0, 0, Player.X)
        board = GameEngine.setCell(board, dim, 0, 1, Player.X)
        board = GameEngine.setCell(board, dim, 0, 2, Player.X)

        val win = GameEngine.checkWinningSequence(board, dim, 3)
        assertNotNull(win)
        assertEquals(Player.X, win?.winner)
        assertEquals(3, win?.cells?.size)
    }

    @Test
    fun `test 3x3 diagonal win detection`() {
        val dim = 3
        var board = GameEngine.createEmptyBoard(dim)
        board = GameEngine.setCell(board, dim, 0, 0, Player.O)
        board = GameEngine.setCell(board, dim, 1, 1, Player.O)
        board = GameEngine.setCell(board, dim, 2, 2, Player.O)

        val win = GameEngine.checkWinningSequence(board, dim, 3)
        assertNotNull(win)
        assertEquals(Player.O, win?.winner)
    }

    @Test
    fun `test 6x6 win detection with 4 in a row`() {
        val dim = 6
        var board = GameEngine.createEmptyBoard(dim)
        board = GameEngine.setCell(board, dim, 2, 1, Player.X)
        board = GameEngine.setCell(board, dim, 2, 2, Player.X)
        board = GameEngine.setCell(board, dim, 2, 3, Player.X)
        board = GameEngine.setCell(board, dim, 2, 4, Player.X)

        val win = GameEngine.checkWinningSequence(board, dim, 4)
        assertNotNull(win)
        assertEquals(Player.X, win?.winner)
        assertEquals(4, win?.cells?.size)
    }

    @Test
    fun `test AI prioritizes immediate winning move`() {
        val dim = 3
        var board = GameEngine.createEmptyBoard(dim)
        // AI is O. Give AI 2 in a row on row 1
        board = GameEngine.setCell(board, dim, 1, 0, Player.O)
        board = GameEngine.setCell(board, dim, 1, 1, Player.O)
        // Opponent is X. Give opponent 2 in a row on row 0
        board = GameEngine.setCell(board, dim, 0, 0, Player.X)
        board = GameEngine.setCell(board, dim, 0, 1, Player.X)

        // AI should take (1, 2) to win rather than blocking (0, 2)
        val move = OrvixAiEngine.computeMove(board, BoardSize.THREE_BY_THREE, Player.O)
        assertEquals(CellPosition(1, 2), move)
    }

    @Test
    fun `test AI blocks opponent immediate winning move`() {
        val dim = 3
        var board = GameEngine.createEmptyBoard(dim)
        // Opponent X has (0, 0) and (0, 1). AI must block at (0, 2)
        board = GameEngine.setCell(board, dim, 0, 0, Player.X)
        board = GameEngine.setCell(board, dim, 0, 1, Player.X)
        board = GameEngine.setCell(board, dim, 2, 2, Player.O)

        val move = OrvixAiEngine.computeMove(board, BoardSize.THREE_BY_THREE, Player.O)
        assertEquals(CellPosition(0, 2), move)
    }
}
