package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoardSize
import com.example.model.CellPosition
import com.example.model.GameMode
import com.example.model.MatchFormat
import com.example.model.MatchSeriesState
import com.example.model.Player
import com.example.model.RoundResult
import com.example.model.SessionScore
import com.example.ui.components.NeonCell
import com.example.ui.components.NeonMarkO
import com.example.ui.components.NeonMarkX
import com.example.ui.components.SeriesScoreBadge
import com.example.ui.components.TurnIndicatorBadge
import com.example.ui.theme.LightCyanX
import com.example.ui.theme.LightVioletO
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkBorder
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import kotlin.math.min

@Composable
fun GameScreen(
    boardSize: BoardSize,
    gameMode: GameMode,
    matchFormat: MatchFormat,
    board: List<Player?>,
    currentTurn: Player,
    isAiThinking: Boolean,
    roundResult: RoundResult?,
    matchSeries: MatchSeriesState,
    sessionScore: SessionScore,
    soundEnabled: Boolean,
    isDarkTheme: Boolean,
    onCellClick: (Int, Int) -> Unit,
    onRestartBoard: () -> Unit,
    onRematch: () -> Unit,
    onToggleSound: () -> Unit,
    onNavigateHome: () -> Unit
) {
    BackHandler {
        onNavigateHome()
    }

    val cyanColor = if (isDarkTheme) NeonCyan else LightCyanX
    val violetColor = if (isDarkTheme) NeonViolet else LightVioletO

    val winningCells = (roundResult as? RoundResult.Win)?.line?.cells.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Game Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateHome,
                modifier = Modifier.testTag("game_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to home",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Mode & Grid Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = boardSize.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = cyanColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (gameMode == GameMode.VS_AI) "Solo vs AI" else "2 Players",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (soundEnabled) "Mute sound" else "Unmute sound",
                        tint = if (soundEnabled) cyanColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onRestartBoard,
                    modifier = Modifier.testTag("restart_board_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart board",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Session Score Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    if (isDarkTheme) NeonDarkBorder else MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreItem(
                label = if (gameMode == GameMode.VS_AI) "You (X)" else "Player X",
                score = sessionScore.winsX,
                color = cyanColor
            )
            ScoreItem(
                label = "Draws",
                score = sessionScore.draws,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ScoreItem(
                label = if (gameMode == GameMode.VS_AI) "AI (O)" else "Player O",
                score = sessionScore.winsO,
                color = violetColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Best of 3 indicator if enabled
        if (matchFormat == MatchFormat.BEST_OF_THREE) {
            SeriesScoreBadge(
                winsX = matchSeries.winsX,
                winsO = matchSeries.winsO,
                targetWins = matchSeries.targetWins,
                isDarkTheme = isDarkTheme
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Current Turn Indicator
        TurnIndicatorBadge(
            currentTurn = currentTurn,
            gameMode = gameMode,
            isAiThinking = isAiThinking,
            isDarkTheme = isDarkTheme
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Responsive Game Grid Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val availableSize = min(maxWidth.value, maxHeight.value).dp
                val dim = boardSize.dimension

                // Dynamically calculate cell size according to board dimensions
                val calculatedCellSize = when (dim) {
                    3 -> (availableSize.value / 3.4f).dp
                    6 -> (availableSize.value / 6.5f).dp
                    9 -> (availableSize.value / 9.5f).dp
                    else -> (availableSize.value / 11.5f).dp
                }

                val hScrollState = rememberScrollState()
                val vScrollState = rememberScrollState()

                Box(
                    modifier = Modifier
                        .then(if (dim >= 9) Modifier.horizontalScroll(hScrollState) else Modifier)
                        .then(if (dim >= 9) Modifier.verticalScroll(vScrollState) else Modifier)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkTheme) Color(0xFF070B16) else Color(0xFFE2E8F0))
                        .border(
                            1.dp,
                            if (isDarkTheme) NeonDarkBorder else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        for (r in 0 until dim) {
                            Row {
                                for (c in 0 until dim) {
                                    val index = r * dim + c
                                    val player = board.getOrNull(index)
                                    val isWinningCell = winningCells.any { it.row == r && it.col == c }

                                    NeonCell(
                                        row = r,
                                        col = c,
                                        player = player,
                                        isWinning = isWinningCell,
                                        isAiThinking = isAiThinking,
                                        isDarkTheme = isDarkTheme,
                                        cellSize = calculatedCellSize,
                                        onClick = { onCellClick(r, c) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Quick Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Target: ${boardSize.winRuleText}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = onRestartBoard,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("clear_board_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clean Board", fontSize = 13.sp)
            }
        }
    }

    // Round Completion Overlay / Dialog
    if (roundResult != null) {
        val isWin = roundResult is RoundResult.Win
        val winner = (roundResult as? RoundResult.Win)?.winner

        val titleText = when {
            winner == Player.X && gameMode == GameMode.VS_AI -> "Victory! You Win!"
            winner == Player.O && gameMode == GameMode.VS_AI -> "AI Takes The Round!"
            winner == Player.X -> "Player X Wins!"
            winner == Player.O -> "Player O Wins!"
            else -> "It's a Draw!"
        }

        val subtitleText = when {
            matchSeries.isComplete && matchSeries.seriesWinner == Player.X -> "Player X Won the Best of 3 Series! 🏆"
            matchSeries.isComplete && matchSeries.seriesWinner == Player.O -> "Player O Won the Best of 3 Series! 🏆"
            matchFormat == MatchFormat.BEST_OF_THREE -> "Series: X (${matchSeries.winsX}) - O (${matchSeries.winsO})"
            winner != null -> "Completed ${boardSize.winLength} in a row!"
            else -> "No moves remaining on the grid."
        }

        val bannerColor = when {
            winner == Player.X -> cyanColor
            winner == Player.O -> violetColor
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

        AlertDialog(
            onDismissRequest = { /* Require deliberate button tap */ },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(bannerColor.copy(alpha = 0.15f))
                            .border(1.5.dp, bannerColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when (winner) {
                            Player.X -> NeonMarkX(modifier = Modifier.size(24.dp), isDarkTheme = isDarkTheme)
                            Player.O -> NeonMarkO(modifier = Modifier.size(24.dp), isDarkTheme = isDarkTheme)
                            null -> Text("🤝", fontSize = 24.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = titleText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Text(
                    text = subtitleText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = onRematch,
                    colors = ButtonDefaults.buttonColors(containerColor = bannerColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("rematch_button")
                ) {
                    Text(
                        text = if (matchSeries.isComplete) "Next Match" else "Rematch",
                        color = if (isDarkTheme) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onNavigateHome,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home")
                }
            },
            containerColor = if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ScoreItem(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = score.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
