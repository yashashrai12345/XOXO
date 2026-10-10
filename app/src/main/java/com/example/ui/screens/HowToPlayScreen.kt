package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightCyanX
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkBorder
import com.example.ui.theme.NeonDarkSurface

@Composable
fun HowToPlayScreen(
    isDarkTheme: Boolean,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val cyanColor = if (isDarkTheme) NeonCyan else LightCyanX
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("how_to_play_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "How to Play",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        RuleCard(
            title = "Core Objective",
            icon = Icons.Default.Star,
            iconTint = cyanColor,
            isDarkTheme = isDarkTheme,
            content = "Players take turns placing their neon mark (X or O) on an empty grid cell. The first player to align the required consecutive marks wins the round!"
        )

        Spacer(modifier = Modifier.height(16.dp))

        RuleCard(
            title = "Board Configurations & Win Rules",
            icon = Icons.Default.GridOn,
            iconTint = cyanColor,
            isDarkTheme = isDarkTheme,
            content = """
                • 3×3 Classic: Connect 3 marks in a row.
                • 6×6 Tactical: Connect 4 marks in a row.
                • 9×9 Strategy: Connect 5 marks in a row.
                • 11×11 Master: Connect 5 marks in a row.
                
                Lines can be horizontal, vertical, or diagonal in either direction.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(16.dp))

        RuleCard(
            title = "Game Modes",
            icon = Icons.Default.SportsEsports,
            iconTint = cyanColor,
            isDarkTheme = isDarkTheme,
            content = """
                • Solo vs AI: Challenge the tactical Orvix computer engine. The AI evaluates lines, forks, and blocks dynamically.
                • 2 Players: Pass-and-play with a friend on one device. Starting player alternates every round.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(16.dp))

        RuleCard(
            title = "Rounds, Draws & Rematches",
            icon = Icons.Default.Info,
            iconTint = cyanColor,
            isDarkTheme = isDarkTheme,
            content = """
                • Victory: Achieved immediately when a player aligns the required consecutive marks.
                • Draw: Occurs when all cells on the grid are occupied with no winner.
                • Rematches: Easily play round after round while tracking ongoing session scores.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RuleCard(
    title: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    isDarkTheme: Boolean,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isDarkTheme) NeonDarkBorder else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
