package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoardSize
import com.example.model.SoloStats
import com.example.model.TwoPlayerStats
import com.example.ui.components.NeonMarkO
import com.example.ui.components.NeonMarkX
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonSecondaryButton
import com.example.ui.theme.LightCyanX
import com.example.ui.theme.LightVioletO
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkBorder
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonDarkSurfaceVariant
import com.example.ui.theme.NeonViolet

@Composable
fun HomeScreen(
    selectedSize: BoardSize,
    soloStats: SoloStats,
    twoPlayerStats: TwoPlayerStats,
    isDarkTheme: Boolean,
    onSizeSelected: (BoardSize) -> Unit,
    onPlayVsAi: () -> Unit,
    onPlayTwoPlayer: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHowToPlay: () -> Unit
) {
    val scrollState = rememberScrollState()
    val cyanColor = if (isDarkTheme) NeonCyan else LightCyanX
    val violetColor = if (isDarkTheme) NeonViolet else LightVioletO

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOpenHowToPlay,
                modifier = Modifier.testTag("how_to_play_button")
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "How to play",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Small glowing indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkTheme) NeonDarkSurfaceVariant else MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "OFFLINE READY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = cyanColor
                )
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Branding Logo & Title
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surface)
                .border(2.dp, cyanColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                NeonMarkX(
                    modifier = Modifier.size(28.dp),
                    isDarkTheme = isDarkTheme
                )
                Spacer(modifier = Modifier.width(4.dp))
                NeonMarkO(
                    modifier = Modifier.size(28.dp),
                    isDarkTheme = isDarkTheme
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ORVIX XO",
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Think ahead. Own the grid.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Primary Game Launch Buttons
        NeonPrimaryButton(
            text = "Play vs AI",
            icon = Icons.Default.Person,
            onClick = onPlayVsAi,
            testTag = "play_vs_ai_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

        NeonSecondaryButton(
            text = "2 Players (Pass & Play)",
            icon = Icons.Default.People,
            onClick = onPlayTwoPlayer,
            testTag = "play_2p_button"
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Board Size Selection Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BOARD GRID SIZE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedSize.winRuleText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = cyanColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BoardSize.entries.forEach { size ->
                    val isSelected = size == selectedSize
                    val itemBg = when {
                        isSelected && isDarkTheme -> Color(0xFF1E2D4E)
                        isSelected && !isDarkTheme -> Color(0xFFE0F2FE)
                        isDarkTheme -> NeonDarkSurface
                        else -> MaterialTheme.colorScheme.surface
                    }
                    val itemBorder = if (isSelected) cyanColor else if (isDarkTheme) NeonDarkBorder else MaterialTheme.colorScheme.outline

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(if (isSelected) 1.5.dp else 1.dp, itemBorder, RoundedCornerShape(12.dp))
                            .clickable { onSizeSelected(size) }
                            .testTag("size_${size.dimension}"),
                        color = itemBg
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = size.label,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) cyanColor else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${size.winLength} in row",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tactical Analytics Dashboard
        val totalBattles = soloStats.gamesPlayed + twoPlayerStats.gamesPlayed
        val totalWins = soloStats.wins + twoPlayerStats.winsX + twoPlayerStats.winsO
        val overallWinRate = if (totalBattles > 0) (totalWins * 100) / totalBattles else 0

        val (tierName, tierColor) = when {
            totalWins >= 25 -> "GRID GRANDMASTER" to Color(0xFFFFD166)
            totalWins >= 12 -> "CYBER MASTER" to violetColor
            totalWins >= 4 -> "TACTICAL DUELIST" to cyanColor
            else -> "CADET RECRUIT" to MaterialTheme.colorScheme.onSurfaceVariant
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TACTICAL ANALYTICS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = tierName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = tierColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (isDarkTheme) NeonDarkBorder else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("analytics_dashboard_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Top Analytics Row: Win Rate & Efficiency Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = null,
                                tint = cyanColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Combat Efficiency",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "$overallWinRate% Overall",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = cyanColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Proportional Analytics Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                    ) {
                        if (totalBattles > 0) {
                            val winFraction = totalWins.toFloat() / totalBattles
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(winFraction.coerceIn(0.05f, 1f))
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF0072FF),
                                                cyanColor
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = if (isDarkTheme) NeonDarkBorder else MaterialTheme.colorScheme.outlineVariant
                    )

                    // 4 Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnalyticsMetricTile(
                            label = "Battles",
                            value = totalBattles.toString(),
                            sublabel = "Total Played",
                            accentColor = MaterialTheme.colorScheme.onSurface,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsMetricTile(
                            label = "AI Wins",
                            value = soloStats.wins.toString(),
                            sublabel = "${soloStats.winRate}% vs AI",
                            accentColor = cyanColor,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsMetricTile(
                            label = "2P Games",
                            value = twoPlayerStats.gamesPlayed.toString(),
                            sublabel = "Local Pass",
                            accentColor = violetColor,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsMetricTile(
                            label = "Streak",
                            value = "${soloStats.currentStreak} 🔥",
                            sublabel = "Best: ${soloStats.bestStreak}",
                            accentColor = Color(0xFFFFD166),
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AnalyticsMetricTile(
    label: String,
    value: String,
    sublabel: String,
    accentColor: Color,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sublabel,
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
