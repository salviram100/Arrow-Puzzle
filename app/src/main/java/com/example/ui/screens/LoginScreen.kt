package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.UserProfile
import com.example.ui.components.GoogleSignInButton
import com.example.ui.components.NeonButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class AvatarOption(val icon: ImageVector, val name: String, val color: Color)

val AVATAR_OPTIONS = listOf(
    AvatarOption(Icons.Default.RocketLaunch, "Vector Pilot", NeonCyan),
    AvatarOption(Icons.Default.Speed, "Velocity Ace", NeonAmber),
    AvatarOption(Icons.Default.Bolt, "Quantum Pulse", NeonMagenta),
    AvatarOption(Icons.Default.Shield, "Cyber Sentry", NeonGreen),
    AvatarOption(Icons.Default.Games, "Arcade Master", NeonPurple),
    AvatarOption(Icons.Default.Person, "Guest Scout", Color(0xFF38BDF8))
)

@Composable
fun LoginScreen(
    currentProfile: UserProfile,
    onLoginAsGuest: (name: String, avatarIndex: Int) -> Unit,
    onGoogleLogin: () -> Unit = {}
) {
    var guestName by remember { mutableStateOf(currentProfile.guestName) }
    var selectedAvatar by remember { mutableIntStateOf(currentProfile.avatarIndex.coerceIn(0, AVATAR_OPTIONS.size - 1)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // High quality futuristic game background image
        Image(
            painter = painterResource(id = R.drawable.img_login_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient scrim for crisp text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground.copy(alpha = 0.88f),
                            DarkBackground.copy(alpha = 0.78f),
                            DarkBackground.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Text(
                    text = "VELOCITY ID ACCESS",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PLAYER LOGIN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = TextPrimary
            )

            Text(
                text = "Sign in with Google or continue as guest to play 100 stages",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Google Sign In Option
            GoogleSignInButton(
                text = "Sign in with Google",
                onClick = onGoogleLogin,
                modifier = Modifier.fillMaxWidth(),
                testTag = "login_screen_google_button"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Divider: OR GUEST PILOT
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.0.dp)
                        .background(DarkBorder)
                )
                Text(
                    text = "OR PLAY AS GUEST",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.0.dp)
                        .background(DarkBorder)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar Selector
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CHOOSE AVATAR",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AVATAR_OPTIONS.take(3).forEachIndexed { index, avatar ->
                            AvatarItem(
                                avatar = avatar,
                                isSelected = selectedAvatar == index,
                                onClick = { selectedAvatar = index }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AVATAR_OPTIONS.drop(3).forEachIndexed { i, avatar ->
                            val actualIndex = i + 3
                            AvatarItem(
                                avatar = avatar,
                                isSelected = selectedAvatar == actualIndex,
                                onClick = { selectedAvatar = actualIndex }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // GamerTag Input
            OutlinedTextField(
                value = guestName,
                onValueChange = { if (it.length <= 20) guestName = it },
                label = { Text("Guest GamerTag") },
                placeholder = { Text("e.g. Velocity_Pilot") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = NeonCyan,
                    unfocusedLabelColor = TextSecondary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurface
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = NeonCyan
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("guest_name_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Play as Guest Button
            NeonButton(
                text = "CONTINUE AS GUEST",
                onClick = {
                    val finalName = if (guestName.isBlank()) "Guest_Pilot" else guestName.trim()
                    onLoginAsGuest(finalName, selectedAvatar)
                },
                neonColor = NeonCyan,
                modifier = Modifier.fillMaxWidth(),
                testTag = "login_guest_button"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Guest progress is saved locally to your device",
                color = TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AvatarItem(
    avatar: AvatarOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(if (isSelected) avatar.color.copy(alpha = 0.25f) else DarkSurfaceElevated)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) avatar.color else DarkBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = avatar.icon,
                contentDescription = avatar.name,
                tint = if (isSelected) avatar.color else TextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = avatar.name,
            color = if (isSelected) avatar.color else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
