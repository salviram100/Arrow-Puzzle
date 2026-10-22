package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ArrowCell
import com.example.data.models.PuzzleStage
import com.example.ui.components.ArrowCellView
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VictoryData

@Composable
fun GamePlayScreen(
    stage: PuzzleStage?,
    grid: List<List<ArrowCell?>>,
    movesCount: Int,
    elapsedSeconds: Int,
    blockedCell: Pair<Int, Int>?,
    hintHighlightCell: Pair<Int, Int>?,
    victoryData: VictoryData?,
    canUndo: Boolean,
    onCellTapped: (row: Int, col: Int) -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onHint: () -> Unit,
    onBack: () -> Unit,
    onNextStage: () -> Unit,
    onLobby: () -> Unit
) {
    if (stage == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Loading stage...", color = TextPrimary)
        }
        return
    }

    val remainingArrows = remember(grid) {
        grid.sumOf { row -> row.count { it?.isArrow() == true } }
    }

    val rows = grid.size
    val cols = if (rows > 0) grid[0].size else 1

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val movesColor = when {
        movesCount <= stage.parMoves -> NeonGreen
        movesCount <= stage.parMoves + 3 -> NeonAmber
        else -> TextSecondary
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top HUD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (stage.isCustom) stage.title else "STAGE ${stage.id}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stage.zoneName,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = NeonAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score & Par Status Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Moves & Par
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MOVES: ",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$movesCount",
                            color = movesColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = " / ${stage.parMoves} (Par)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Remaining Arrows Counter
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$remainingArrows Arrows Left",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Timer
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeFormatted,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Central Game Grid
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(listOf(DarkBorder, NeonCyan.copy(alpha = 0.5f), DarkBorder))
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(16.dp, RoundedCornerShape(20.dp))
                        .testTag("game_board")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (c in 0 until cols) {
                                    val cell = grid.getOrNull(r)?.getOrNull(c)
                                    val isBlocked = blockedCell == (r to c)
                                    val isHighlighted = hintHighlightCell == (r to c)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clickable(enabled = cell?.isArrow() == true) {
                                                onCellTapped(r, c)
                                            }
                                            .testTag("cell_${r}_$c")
                                    ) {
                                        ArrowCellView(
                                            cell = cell,
                                            isBlocked = isBlocked,
                                            isHighlighted = isHighlighted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Star Rating Hint Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⭐ 3 Stars: ≤ ${stage.parMoves} moves  •  ⭐ 2 Stars: ≤ ${stage.parMoves + 3} moves",
                        color = StarGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Bottom Action Controls: Hint, Undo, Restart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hint Button
                GameActionButton(
                    icon = Icons.Default.Lightbulb,
                    label = "HINT",
                    color = NeonAmber,
                    onClick = onHint,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_hint"
                )

                // Undo Button
                GameActionButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = "UNDO",
                    color = NeonCyan,
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_undo"
                )

                // Restart Button
                GameActionButton(
                    icon = Icons.Default.Refresh,
                    label = "RESTART",
                    color = NeonMagenta,
                    onClick = onRestart,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_restart"
                )
            }
        }

        // Victory Dialog Popup
        if (victoryData != null) {
            VictoryDialog(
                data = victoryData,
                onNextStage = onNextStage,
                onReplay = onRestart,
                onLobby = onLobby
            )
        }
    }
}

@Composable
private fun GameActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "action_button"
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) DarkSurfaceElevated else DarkSurface.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) color.copy(alpha = 0.5f) else DarkBorder.copy(alpha = 0.3f)
        ),
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) color else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (enabled) TextPrimary else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
