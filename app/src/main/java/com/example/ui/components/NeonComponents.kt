package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.Player
import com.example.ui.theme.LightCyanX
import com.example.ui.theme.LightVioletO
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkBorder
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet

@Composable
fun NeonMarkX(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    isWinning: Boolean = false
) {
    val cyanColor = if (isDarkTheme) NeonCyan else LightCyanX
    val baseColor = if (isWinning) NeonGreen else cyanColor

    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.16f
        val glowWidth = size.minDimension * 0.32f
        val pad = size.minDimension * 0.22f

        val start1 = Offset(pad, pad)
        val end1 = Offset(size.width - pad, size.height - pad)
        val start2 = Offset(size.width - pad, pad)
        val end2 = Offset(pad, size.height - pad)

        // Outer glow layer
        if (isDarkTheme) {
            val glowAlpha = if (isWinning) 0.5f else 0.35f
            drawLine(
                color = baseColor.copy(alpha = glowAlpha),
                start = start1,
                end = end1,
                strokeWidth = glowWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = baseColor.copy(alpha = glowAlpha),
                start = start2,
                end = end2,
                strokeWidth = glowWidth,
                cap = StrokeCap.Round
            )
        }

        // Core line
        drawLine(
            color = baseColor,
            start = start1,
            end = end1,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = baseColor,
            start = start2,
            end = end2,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Highlight line
        if (isDarkTheme) {
            val highlightWidth = strokeWidth * 0.35f
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = start1,
                end = end1,
                strokeWidth = highlightWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = start2,
                end = end2,
                strokeWidth = highlightWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun NeonMarkO(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    isWinning: Boolean = false
) {
    val violetColor = if (isDarkTheme) NeonViolet else LightVioletO
    val baseColor = if (isWinning) NeonGreen else violetColor

    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.16f
        val glowWidth = size.minDimension * 0.32f
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.minDimension - size.minDimension * 0.44f) / 2f

        // Outer glow layer
        if (isDarkTheme) {
            val glowAlpha = if (isWinning) 0.5f else 0.35f
            drawCircle(
                color = baseColor.copy(alpha = glowAlpha),
                radius = radius,
                center = center,
                style = Stroke(width = glowWidth)
            )
        }

        // Core ring
        drawCircle(
            color = baseColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Highlight ring
        if (isDarkTheme) {
            val highlightWidth = strokeWidth * 0.35f
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = radius,
                center = center,
                style = Stroke(width = highlightWidth)
            )
        }
    }
}

@Composable
fun NeonCell(
    row: Int,
    col: Int,
    player: Player?,
    isWinning: Boolean,
    isAiThinking: Boolean,
    isDarkTheme: Boolean,
    cellSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cell_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val surfaceColor = when {
        isWinning && isDarkTheme -> Color(0xFF0D3B36)
        isWinning && !isDarkTheme -> Color(0xFFDCFCE7)
        isDarkTheme -> NeonDarkSurface
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isWinning -> NeonGreen
        player == Player.X -> if (isDarkTheme) NeonCyan.copy(alpha = 0.5f) else LightCyanX.copy(alpha = 0.5f)
        player == Player.O -> if (isDarkTheme) NeonViolet.copy(alpha = 0.5f) else LightVioletO.copy(alpha = 0.5f)
        isDarkTheme -> NeonDarkBorder
        else -> MaterialTheme.colorScheme.outline
    }

    val borderWidth = if (isWinning) 2.5.dp else 1.dp
    val scaleModifier = if (isWinning) Modifier.scale(pulseScale) else Modifier

    Box(
        modifier = modifier
            .size(cellSize)
            .padding(2.dp)
            .then(scaleModifier)
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                enabled = player == null && !isAiThinking,
                role = Role.Button,
                onClick = onClick
            )
            .testTag("cell_${row}_${col}"),
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            when (player) {
                Player.X -> NeonMarkX(
                    modifier = Modifier.fillMaxSize(0.85f),
                    isDarkTheme = isDarkTheme,
                    isWinning = isWinning
                )
                Player.O -> NeonMarkO(
                    modifier = Modifier.fillMaxSize(0.85f),
                    isDarkTheme = isDarkTheme,
                    isWinning = isWinning
                )
            }
        }
    }
}

@Composable
fun NeonPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "primary_button"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0072FF),
                            Color(0xFF00C6FF)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0x66FFFFFF),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun NeonSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "secondary_button"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6B21A8),
                            Color(0xFFA855F7)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0x66FFFFFF),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun TurnIndicatorBadge(
    currentTurn: Player,
    gameMode: GameMode,
    isAiThinking: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cyanColor = if (isDarkTheme) NeonCyan else LightCyanX
    val violetColor = if (isDarkTheme) NeonViolet else LightVioletO

    val activeColor = if (currentTurn == Player.X) cyanColor else violetColor
    val activeLabel = when {
        isAiThinking -> "AI is Thinking…"
        gameMode == GameMode.VS_AI && currentTurn == Player.X -> "Your Turn (X)"
        gameMode == GameMode.VS_AI && currentTurn == Player.O -> "AI Turn (O)"
        currentTurn == Player.X -> "Player X's Turn"
        else -> "Player O's Turn"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, activeColor, RoundedCornerShape(14.dp)),
        color = if (isDarkTheme) NeonDarkSurface else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isAiThinking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = violetColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(activeColor.copy(alpha = 0.15f))
                            .border(1.dp, activeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentTurn == Player.X) {
                            NeonMarkX(
                                modifier = Modifier.size(16.dp),
                                isDarkTheme = isDarkTheme
                            )
                        } else {
                            NeonMarkO(
                                modifier = Modifier.size(16.dp),
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = activeLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Mode indicator pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(activeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (gameMode == GameMode.VS_AI) "Solo vs AI" else "2P Pass & Play",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = activeColor
                )
            }
        }
    }
}

@Composable
fun SeriesScoreBadge(
    winsX: Int,
    winsO: Int,
    targetWins: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cyanColor = if (isDarkTheme) NeonCyan else LightCyanX
    val violetColor = if (isDarkTheme) NeonViolet else LightVioletO

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDarkTheme) Color(0xFF11172A) else Color(0xFFE2E8F0))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player X dots
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "X:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = cyanColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            for (i in 0 until targetWins) {
                val isFilled = i < winsX
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(1.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) cyanColor else Color.Transparent)
                        .border(1.dp, cyanColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
        }

        Text(
            text = "Best of 3 (First to $targetWins)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Player O dots
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (i in 0 until targetWins) {
                val isFilled = i < winsO
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(1.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) violetColor else Color.Transparent)
                        .border(1.dp, violetColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "O",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = violetColor
            )
        }
    }
}
