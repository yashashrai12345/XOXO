package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioHapticsManager
import com.example.data.PreferencesManager
import com.example.logic.GameEngine
import com.example.logic.OrvixAiEngine
import com.example.model.AppScreen
import com.example.model.BoardSize
import com.example.model.GameMode
import com.example.model.MatchFormat
import com.example.model.MatchSeriesState
import com.example.model.Player
import com.example.model.RoundResult
import com.example.model.SessionScore
import com.example.model.SoloStats
import com.example.model.TwoPlayerStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val audioManager = AudioHapticsManager(application)

    // Navigation & Configuration
    private val _screen = MutableStateFlow(AppScreen.SPLASH)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1200)
            if (_screen.value == AppScreen.SPLASH) {
                _screen.value = AppScreen.HOME
            }
        }
    }

    fun dismissSplash() {
        if (_screen.value == AppScreen.SPLASH) {
            _screen.value = AppScreen.HOME
        }
    }

    private val _boardSize = MutableStateFlow(BoardSize.THREE_BY_THREE)
    val boardSize: StateFlow<BoardSize> = _boardSize.asStateFlow()

    private val _gameMode = MutableStateFlow(GameMode.VS_AI)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private val _matchFormat = MutableStateFlow(MatchFormat.SINGLE)
    val matchFormat: StateFlow<MatchFormat> = _matchFormat.asStateFlow()

    // Game Board & Turn State
    private val _board = MutableStateFlow(GameEngine.createEmptyBoard(BoardSize.THREE_BY_THREE.dimension))
    val board: StateFlow<List<Player?>> = _board.asStateFlow()

    private val _currentTurn = MutableStateFlow(Player.X)
    val currentTurn: StateFlow<Player> = _currentTurn.asStateFlow()

    private val _startingPlayer = MutableStateFlow(Player.X)
    val startingPlayer: StateFlow<Player> = _startingPlayer.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _roundResult = MutableStateFlow<RoundResult?>(null)
    val roundResult: StateFlow<RoundResult?> = _roundResult.asStateFlow()

    private val _matchSeries = MutableStateFlow(MatchSeriesState())
    val matchSeries: StateFlow<MatchSeriesState> = _matchSeries.asStateFlow()

    // Scores & Stats
    private val _sessionScore = MutableStateFlow(SessionScore())
    val sessionScore: StateFlow<SessionScore> = _sessionScore.asStateFlow()

    private val _soloStats = MutableStateFlow(prefs.getSoloStats())
    val soloStats: StateFlow<SoloStats> = _soloStats.asStateFlow()

    private val _twoPlayerStats = MutableStateFlow(prefs.getTwoPlayerStats())
    val twoPlayerStats: StateFlow<TwoPlayerStats> = _twoPlayerStats.asStateFlow()

    // Settings
    private val _soundEnabled = MutableStateFlow(prefs.isSoundEnabled)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.isHapticsEnabled)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(prefs.isDarkTheme)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun navigateTo(destination: AppScreen) {
        audioManager.playButtonClick(_soundEnabled.value, _hapticsEnabled.value)
        _screen.value = destination
    }

    fun setBoardSize(size: BoardSize) {
        _boardSize.value = size
    }

    fun setGameMode(mode: GameMode) {
        _gameMode.value = mode
    }

    fun setMatchFormat(format: MatchFormat) {
        _matchFormat.value = format
    }

    fun startNewGame(mode: GameMode? = null, size: BoardSize? = null, format: MatchFormat? = null) {
        if (mode != null) _gameMode.value = mode
        if (size != null) _boardSize.value = size
        if (format != null) _matchFormat.value = format

        val dim = _boardSize.value.dimension
        _board.value = GameEngine.createEmptyBoard(dim)
        _startingPlayer.value = Player.X
        _currentTurn.value = Player.X
        _isAiThinking.value = false
        _roundResult.value = null

        val target = if (_matchFormat.value == MatchFormat.BEST_OF_THREE) 2 else 1
        _matchSeries.value = MatchSeriesState(targetWins = target)

        audioManager.playButtonClick(_soundEnabled.value, _hapticsEnabled.value)
        _screen.value = AppScreen.GAME
    }

    fun restartCurrentBoard() {
        val dim = _boardSize.value.dimension
        _board.value = GameEngine.createEmptyBoard(dim)
        _currentTurn.value = _startingPlayer.value
        _isAiThinking.value = false
        _roundResult.value = null
        audioManager.playButtonClick(_soundEnabled.value, _hapticsEnabled.value)
    }

    fun rematch() {
        val dim = _boardSize.value.dimension
        _board.value = GameEngine.createEmptyBoard(dim)
        _roundResult.value = null
        _isAiThinking.value = false

        // Alternate starting player in 2-player mode
        val nextStarter = if (_gameMode.value == GameMode.TWO_PLAYER) {
            _startingPlayer.value.opponent()
        } else {
            Player.X
        }
        _startingPlayer.value = nextStarter
        _currentTurn.value = nextStarter

        // If series was completed, start a fresh series
        if (_matchSeries.value.isComplete) {
            val target = if (_matchFormat.value == MatchFormat.BEST_OF_THREE) 2 else 1
            _matchSeries.value = MatchSeriesState(targetWins = target)
        }

        audioManager.playButtonClick(_soundEnabled.value, _hapticsEnabled.value)
    }

    fun onCellClicked(row: Int, col: Int) {
        // Prevent move if cell occupied, round finished, or AI is thinking
        if (_roundResult.value != null || _isAiThinking.value) return

        val currentBoard = _board.value
        val dim = _boardSize.value.dimension
        val winLen = _boardSize.value.winLength

        if (!GameEngine.isMoveValid(currentBoard, dim, row, col)) return

        val player = _currentTurn.value
        val newBoard = GameEngine.setCell(currentBoard, dim, row, col, player)
        _board.value = newBoard

        // Audio & Haptics for move
        if (player == Player.X) {
            audioManager.playMoveX(_soundEnabled.value, _hapticsEnabled.value)
        } else {
            audioManager.playMoveO(_soundEnabled.value, _hapticsEnabled.value)
        }

        // Check if move wins or draws
        val win = GameEngine.checkWinningSequence(newBoard, dim, winLen)
        if (win != null) {
            handleRoundEnd(RoundResult.Win(player, win))
            return
        }

        if (GameEngine.isBoardFull(newBoard)) {
            handleRoundEnd(RoundResult.Draw)
            return
        }

        // Advance turn
        val nextTurn = player.opponent()
        _currentTurn.value = nextTurn

        // If playing vs AI and it's AI's turn (Player O)
        if (_gameMode.value == GameMode.VS_AI && nextTurn == Player.O) {
            triggerAiMove(newBoard)
        }
    }

    private fun triggerAiMove(currentBoard: List<Player?>) {
        _isAiThinking.value = true
        viewModelScope.launch {
            // Smooth thinking delay for natural gameplay rhythm (400ms)
            delay(400)

            val aiMove = withContext(Dispatchers.Default) {
                OrvixAiEngine.computeMove(currentBoard, _boardSize.value, Player.O)
            }

            val dim = _boardSize.value.dimension
            val winLen = _boardSize.value.winLength

            if (GameEngine.isMoveValid(_board.value, dim, aiMove.row, aiMove.col)) {
                val updatedBoard = GameEngine.setCell(_board.value, dim, aiMove.row, aiMove.col, Player.O)
                _board.value = updatedBoard

                audioManager.playMoveO(_soundEnabled.value, _hapticsEnabled.value)

                val win = GameEngine.checkWinningSequence(updatedBoard, dim, winLen)
                if (win != null) {
                    _isAiThinking.value = false
                    handleRoundEnd(RoundResult.Win(Player.O, win))
                    return@launch
                }

                if (GameEngine.isBoardFull(updatedBoard)) {
                    _isAiThinking.value = false
                    handleRoundEnd(RoundResult.Draw)
                    return@launch
                }

                _currentTurn.value = Player.X
            }

            _isAiThinking.value = false
        }
    }

    private fun handleRoundEnd(result: RoundResult) {
        _roundResult.value = result

        val winner = if (result is RoundResult.Win) result.winner else null

        // Play feedback
        if (winner != null) {
            audioManager.playWin(_soundEnabled.value, _hapticsEnabled.value)
        } else {
            audioManager.playDraw(_soundEnabled.value, _hapticsEnabled.value)
        }

        // Update Session Score
        val currentSession = _sessionScore.value
        _sessionScore.value = SessionScore(
            winsX = currentSession.winsX + if (winner == Player.X) 1 else 0,
            winsO = currentSession.winsO + if (winner == Player.O) 1 else 0,
            draws = currentSession.draws + if (winner == null) 1 else 0
        )

        // Update Match Series Score (if Best of 3)
        val currentSeries = _matchSeries.value
        val newWinsX = currentSeries.winsX + if (winner == Player.X) 1 else 0
        val newWinsO = currentSeries.winsO + if (winner == Player.O) 1 else 0
        val target = currentSeries.targetWins

        val seriesWinner = when {
            newWinsX >= target -> Player.X
            newWinsO >= target -> Player.O
            else -> null
        }
        _matchSeries.value = MatchSeriesState(
            winsX = newWinsX,
            winsO = newWinsO,
            targetWins = target,
            seriesWinner = seriesWinner
        )

        // Persist to Lifetime Statistics
        if (_gameMode.value == GameMode.VS_AI) {
            _soloStats.value = prefs.recordSoloOutcome(winner)
        } else {
            _twoPlayerStats.value = prefs.recordTwoPlayerOutcome(winner)
        }
    }

    fun toggleSound() {
        val newVal = !_soundEnabled.value
        _soundEnabled.value = newVal
        prefs.isSoundEnabled = newVal
        if (newVal) audioManager.playButtonClick(true, _hapticsEnabled.value)
    }

    fun toggleHaptics() {
        val newVal = !_hapticsEnabled.value
        _hapticsEnabled.value = newVal
        prefs.isHapticsEnabled = newVal
        if (newVal) audioManager.playButtonClick(_soundEnabled.value, true)
    }

    fun toggleTheme() {
        val newVal = !_isDarkTheme.value
        _isDarkTheme.value = newVal
        prefs.isDarkTheme = newVal
        audioManager.playButtonClick(_soundEnabled.value, _hapticsEnabled.value)
    }

    fun resetLifetimeStats() {
        prefs.resetAllStats()
        _soloStats.value = prefs.getSoloStats()
        _twoPlayerStats.value = prefs.getTwoPlayerStats()
        audioManager.playButtonClick(_soundEnabled.value, _hapticsEnabled.value)
    }
}
