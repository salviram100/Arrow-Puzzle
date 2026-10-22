package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.models.StageProgress
import com.example.game.generator.StageRepository
import com.example.ui.components.StarRatingBar
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

@Composable
fun StageSelectScreen(
    allProgress: List<StageProgress>,
    onSelectStage: (Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedZoneIndex by remember { mutableIntStateOf(0) }
    val zones = StageRepository.ZONES
    val currentZone = zones[selectedZoneIndex]

    val zoneStages = remember(selectedZoneIndex) {
        (currentZone.startStage..currentZone.endStage).toList()
    }

    val totalEarnedStars = allProgress.sumOf { it.stars }
    val zoneEarnedStars = allProgress
        .filter { it.stageId in currentZone.startStage..currentZone.endStage }
        .sumOf { it.stars }

    val zoneColor = when (selectedZoneIndex) {
        0 -> NeonCyan
        1 -> NeonAmber
        2 -> NeonGreen
        3 -> NeonPurple
        else -> NeonMagenta
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Lobby",
                            tint = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "100 STAGES",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Select any unlocked level",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Total Stars Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StarGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalEarnedStars / 300",
                            color = StarGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Zone Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedZoneIndex,
                containerColor = DarkBackground,
                contentColor = TextPrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedZoneIndex]),
                        color = zoneColor,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                zones.forEachIndexed { index, zone ->
                    val isSelected = selectedZoneIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedZoneIndex = index },
                        text = {
                            Text(
                                text = "Z${zone.index}: ${zone.name}",
                                color = if (isSelected) zoneColor else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Zone Info Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, zoneColor.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentZone.description,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Stages ${currentZone.startStage} - ${currentZone.endStage}",
                            color = zoneColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "⭐ $zoneEarnedStars / 60",
                        color = StarGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // 20 Stages Grid for this zone (4 columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(zoneStages) { stageId ->
                    val progress = allProgress.find { it.stageId == stageId }
                    val isUnlocked = stageId == 1 || (progress != null && progress.unlocked)
                    val stars = progress?.stars ?: 0
                    val isCompleted = progress?.completed ?: false

                    StageGridCard(
                        stageId = stageId,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        stars = stars,
                        zoneColor = zoneColor,
                        onClick = {
                            if (isUnlocked) onSelectStage(stageId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StageGridCard(
    stageId: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    stars: Int,
    zoneColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) DarkSurfaceElevated else DarkSurface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isUnlocked && isCompleted) 1.5.dp else if (isUnlocked) 1.dp else 0.5.dp,
            color = if (isCompleted) StarGold else if (isUnlocked) zoneColor else DarkBorder.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("stage_card_$stageId")
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$stageId",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "$stageId",
                        color = if (isCompleted) TextPrimary else zoneColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    StarRatingBar(
                        stars = stars,
                        maxStars = 3,
                        starSize = 13.dp
                    )
                }
            }
        }
    }
}
