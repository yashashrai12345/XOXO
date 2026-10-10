package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppScreen
import com.example.model.GameMode
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowToPlayScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.OrvixTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            OrvixTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        OrvixApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun OrvixApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.screen.collectAsStateWithLifecycle()
    val boardSize by viewModel.boardSize.collectAsStateWithLifecycle()
    val gameMode by viewModel.gameMode.collectAsStateWithLifecycle()
    val matchFormat by viewModel.matchFormat.collectAsStateWithLifecycle()
    val board by viewModel.board.collectAsStateWithLifecycle()
    val currentTurn by viewModel.currentTurn.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val roundResult by viewModel.roundResult.collectAsStateWithLifecycle()
    val matchSeries by viewModel.matchSeries.collectAsStateWithLifecycle()
    val sessionScore by viewModel.sessionScore.collectAsStateWithLifecycle()
    val soloStats by viewModel.soloStats.collectAsStateWithLifecycle()
    val twoPlayerStats by viewModel.twoPlayerStats.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    when (currentScreen) {
        AppScreen.SPLASH -> SplashScreen(
            isDarkTheme = isDarkTheme,
            onDismiss = { viewModel.dismissSplash() }
        )

        AppScreen.HOME -> HomeScreen(
            selectedSize = boardSize,
            soloStats = soloStats,
            twoPlayerStats = twoPlayerStats,
            isDarkTheme = isDarkTheme,
            onSizeSelected = { viewModel.setBoardSize(it) },
            onPlayVsAi = { viewModel.startNewGame(mode = GameMode.VS_AI) },
            onPlayTwoPlayer = { viewModel.startNewGame(mode = GameMode.TWO_PLAYER) },
            onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
            onOpenHowToPlay = { viewModel.navigateTo(AppScreen.HOW_TO_PLAY) }
        )

        AppScreen.GAME -> GameScreen(
            boardSize = boardSize,
            gameMode = gameMode,
            matchFormat = matchFormat,
            board = board,
            currentTurn = currentTurn,
            isAiThinking = isAiThinking,
            roundResult = roundResult,
            matchSeries = matchSeries,
            sessionScore = sessionScore,
            soundEnabled = soundEnabled,
            isDarkTheme = isDarkTheme,
            onCellClick = { r, c -> viewModel.onCellClicked(r, c) },
            onRestartBoard = { viewModel.restartCurrentBoard() },
            onRematch = { viewModel.rematch() },
            onToggleSound = { viewModel.toggleSound() },
            onNavigateHome = { viewModel.navigateTo(AppScreen.HOME) }
        )

        AppScreen.SETTINGS -> SettingsScreen(
            soundEnabled = soundEnabled,
            hapticsEnabled = hapticsEnabled,
            isDarkTheme = isDarkTheme,
            onToggleSound = { viewModel.toggleSound() },
            onToggleHaptics = { viewModel.toggleHaptics() },
            onToggleTheme = { viewModel.toggleTheme() },
            onResetStats = { viewModel.resetLifetimeStats() },
            onBack = { viewModel.navigateTo(AppScreen.HOME) }
        )

        AppScreen.HOW_TO_PLAY -> HowToPlayScreen(
            isDarkTheme = isDarkTheme,
            onBack = { viewModel.navigateTo(AppScreen.HOME) }
        )

        AppScreen.STATS -> SettingsScreen(
            soundEnabled = soundEnabled,
            hapticsEnabled = hapticsEnabled,
            isDarkTheme = isDarkTheme,
            onToggleSound = { viewModel.toggleSound() },
            onToggleHaptics = { viewModel.toggleHaptics() },
            onToggleTheme = { viewModel.toggleTheme() },
            onResetStats = { viewModel.resetLifetimeStats() },
            onBack = { viewModel.navigateTo(AppScreen.HOME) }
        )
    }
}
