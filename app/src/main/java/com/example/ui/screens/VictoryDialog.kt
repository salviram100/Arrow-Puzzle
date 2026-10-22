package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.NeonButton
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarInactive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VictoryData
import kotlinx.coroutines.delay

@Composable
fun VictoryDialog(
    data: VictoryData,
    onNextStage: () -> Unit,
    onReplay: () -> Unit,
    onLobby: () -> Unit
) {
    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    LaunchedEffect(data.stars) {
        delay(200)
        if (data.stars >= 1) {
            star1Scale.animateTo(1.3f, spring(dampingRatio = 0.5f, stiffness = 500f))
            star1Scale.animateTo(1.0f, spring(dampingRatio = 0.8f, stiffness = 600f))
        }
        if (data.stars >= 2) {
            delay(150)
            star2Scale.animateTo(1.3f, spring(dampingRatio = 0.5f, stiffness = 500f))
            star2Scale.animateTo(1.0f, spring(dampingRatio = 0.8f, stiffness = 600f))
        }
        if (data.stars >= 3) {
            delay(150)
            star3Scale.animateTo(1.3f, spring(dampingRatio = 0.5f, stiffness = 500f))
            star3Scale.animateTo(1.0f, spring(dampingRatio = 0.8f, stiffness = 600f))
        }
    }

    val ratingText = when (data.stars) {
        3 -> "PERFECT CLEAR! 🌟🌟🌟"
        2 -> "GREAT JOB! 🌟🌟"
        else -> "STAGE CLEARED! 🌟"
    }

    val ratingColor = when (data.stars) {
        3 -> StarGold
        2 -> NeonCyan
        else -> NeonGreen
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(26.dp),
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                Brush.verticalGradient(listOf(StarGold, NeonCyan, DarkBorder))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(26.dp))
                .testTag("victory_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Victory Header Title
                Text(
                    text = "VICTORY!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    color = StarGold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = data.stage.title,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // 3 Animated Stars Showcase
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star 1
                    StarVictoryItem(
                        isEarned = data.stars >= 1,
                        scale = if (data.stars >= 1) star1Scale.value else 0.85f,
                        size = 46.dp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    // Star 2 (Middle - larger)
                    StarVictoryItem(
                        isEarned = data.stars >= 2,
                        scale = if (data.stars >= 2) star2Scale.value else 0.85f,
                        size = 60.dp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    // Star 3
                    StarVictoryItem(
                        isEarned = data.stars >= 3,
                        scale = if (data.stars >= 3) star3Scale.value else 0.85f,
                        size = 46.dp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ratingColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ratingColor.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Text(
                        text = ratingText,
                        color = ratingColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Breakdown Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatColumn(label = "MOVES", value = "${data.moves}", subtext = "Par: ${data.parMoves}")
                        StatColumn(label = "TIME", value = "${data.timeSeconds}s", subtext = "Total")
                        StatColumn(label = "STARS", value = "${data.stars} / 3", subtext = "Earned")
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Actions: Next Stage, Replay, Lobby
                NeonButton(
                    text = if (data.stage.isCustom || data.stage.id >= 100) "BACK TO STAGES" else "NEXT STAGE",
                    onClick = onNextStage,
                    neonColor = NeonCyan,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = NeonCyan
                        )
                    },
                    testTag = "victory_next_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeonButton(
                        text = "REPLAY",
                        onClick = onReplay,
                        neonColor = NeonAmber,
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        testTag = "victory_replay_button"
                    )

                    NeonButton(
                        text = "LOBBY",
                        onClick = onLobby,
                        neonColor = NeonMagenta,
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        testTag = "victory_lobby_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun StarVictoryItem(
    isEarned: Boolean,
    scale: Float,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .scale(scale)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isEarned) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = null,
            tint = if (isEarned) StarGold else StarInactive,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String, subtext: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = subtext,
            color = TextSecondary,
            fontSize = 10.sp
        )
    }
}
