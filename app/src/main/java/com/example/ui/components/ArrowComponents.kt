package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.ArrowCell
import com.example.data.models.ArrowColor
import com.example.data.models.ArrowDirection
import com.example.data.models.CellType
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObstacleBg
import com.example.ui.theme.ObstacleBorder
import com.example.ui.theme.PortalAlpha
import com.example.ui.theme.PortalBeta
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarInactive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    neonColor: Color = NeonCyan,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    testTag: String = "neon_button"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Surface(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        neonColor.copy(alpha = if (enabled) glowAlpha else 0.2f),
                        neonColor.copy(alpha = if (enabled) 1f else 0.3f),
                        neonColor.copy(alpha = if (enabled) glowAlpha else 0.2f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = neonColor),
                onClick = onClick
            ),
        color = if (enabled) DarkSurfaceElevated.copy(alpha = 0.9f) else DarkSurface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = if (enabled) 6.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Box(modifier = Modifier.size(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) TextPrimary else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
            if (trailingIcon != null) {
                Box(modifier = Modifier.size(8.dp))
                trailingIcon()
            }
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Sign in with Google",
    enabled: Boolean = true,
    testTag: String = "google_login_button"
) {
    Surface(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFDADCE0).copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color(0xFF4285F4).copy(alpha = 0.25f)),
                onClick = onClick
            ),
        color = Color(0xFFFFFFFF),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Box(modifier = Modifier.size(12.dp))
            Text(
                text = text,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
fun StarRatingBar(
    stars: Int,
    maxStars: Int = 3,
    starSize: Dp = 22.dp,
    animated: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isEarned = i <= stars
            if (animated && isEarned) {
                val scale = remember { Animatable(0f) }
                LaunchedEffect(i) {
                    scale.animateTo(
                        targetValue = 1.2f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
                    )
                    scale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star $i",
                    tint = StarGold,
                    modifier = Modifier
                        .size(starSize)
                        .scale(scale.value)
                )
            } else {
                Icon(
                    imageVector = if (isEarned) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (isEarned) "Earned Star" else "Empty Star",
                    tint = if (isEarned) StarGold else StarInactive,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}

@Composable
fun ArrowCellView(
    cell: ArrowCell?,
    isBlocked: Boolean = false,
    isHighlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_hint")
    val hintScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_scale"
    )

    val blockedShake = remember { Animatable(0f) }
    LaunchedEffect(isBlocked) {
        if (isBlocked) {
            blockedShake.animateTo(-12f, tween(50, easing = LinearEasing))
            blockedShake.animateTo(12f, tween(50, easing = LinearEasing))
            blockedShake.animateTo(-8f, tween(50, easing = LinearEasing))
            blockedShake.animateTo(8f, tween(50, easing = LinearEasing))
            blockedShake.animateTo(0f, tween(50, easing = LinearEasing))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(if (isHighlighted) hintScale else 1f)
            .padding(2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        if (cell == null) {
            // Empty grid tile - no background or border for clean floating vector aesthetic
            Box(modifier = Modifier.fillMaxSize())
        } else {
            when (cell.type) {
                CellType.ARROW, CellType.ROTATOR -> {
                    val arrowColor = cell.color.toComposeColor()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        arrowColor.copy(alpha = 0.35f),
                                        DarkSurfaceElevated.copy(alpha = 0.95f)
                                    )
                                )
                            )
                            .border(
                                width = if (isHighlighted) 2.5.dp else if (isBlocked) 2.dp else 1.2.dp,
                                color = if (isBlocked) NeonMagenta else if (isHighlighted) NeonAmber else arrowColor,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize(0.72f)
                                .rotate(cell.direction.angle)
                        ) {
                            drawCyberArrow(
                                color = if (isBlocked) NeonMagenta else arrowColor,
                                isRotator = cell.type == CellType.ROTATOR
                            )
                        }
                    }
                }
                CellType.OBSTACLE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObstacleBg)
                            .border(1.5.dp, ObstacleBorder, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize(0.6f)) {
                            // Cross-hatch cyber barrier lines
                            drawLine(
                                color = Color.White.copy(alpha = 0.3f),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.3f),
                                start = Offset(size.width, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                }
                CellType.PORTAL_A, CellType.PORTAL_B -> {
                    val portalColor = if (cell.type == CellType.PORTAL_A) PortalAlpha else PortalBeta
                    val portalName = if (cell.type == CellType.PORTAL_A) "α" else "β"
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(portalColor.copy(alpha = 0.3f))
                            .border(2.dp, portalColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = portalName,
                            color = portalColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
                CellType.COLOR_GATE -> {
                    val gateColor = cell.color.toComposeColor()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(gateColor.copy(alpha = 0.2f))
                            .border(2.dp, gateColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GATE",
                            color = gateColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }
                CellType.EMPTY -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

private fun DrawScope.drawCyberArrow(color: Color, isRotator: Boolean) {
    val w = size.width
    val h = size.height

    // Geometric Futuristic Arrow Path
    val path = Path().apply {
        // Arrow tip at top center (w/2, 0)
        moveTo(w * 0.5f, h * 0.05f)
        lineTo(w * 0.92f, h * 0.48f)
        lineTo(w * 0.68f, h * 0.48f)
        lineTo(w * 0.68f, h * 0.95f)
        lineTo(w * 0.32f, h * 0.95f)
        lineTo(w * 0.32f, h * 0.48f)
        lineTo(w * 0.08f, h * 0.48f)
        close()
    }

    // Fill with semi-glow
    drawPath(
        path = path,
        color = color.copy(alpha = 0.85f),
        style = Fill
    )

    // Glowing border outline
    drawPath(
        path = path,
        color = Color.White.copy(alpha = 0.9f),
        style = Stroke(
            width = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Inner speed vector notch
    val innerPath = Path().apply {
        moveTo(w * 0.5f, h * 0.25f)
        lineTo(w * 0.65f, h * 0.45f)
        lineTo(w * 0.5f, h * 0.40f)
        lineTo(w * 0.35f, h * 0.45f)
        close()
    }
    drawPath(
        path = innerPath,
        color = Color.White.copy(alpha = 0.95f),
        style = Fill
    )
}
