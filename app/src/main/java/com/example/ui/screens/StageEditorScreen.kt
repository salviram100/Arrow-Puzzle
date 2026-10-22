package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.data.models.ArrowCell
import com.example.data.models.ArrowColor
import com.example.data.models.ArrowDirection
import com.example.ui.components.ArrowCellView
import com.example.ui.components.NeonButton
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ArrowGameViewModel

@Composable
fun StageEditorScreen(
    editorRows: Int,
    editorCols: Int,
    editorGrid: List<List<ArrowCell?>>,
    editorTitle: String,
    selectedTool: ArrowGameViewModel.EditorTool,
    selectedDirection: ArrowDirection,
    selectedColor: ArrowColor,
    validationMessage: String?,
    onSetGridSize: (rows: Int, cols: Int) -> Unit,
    onSetTitle: (String) -> Unit,
    onSelectTool: (ArrowGameViewModel.EditorTool) -> Unit,
    onSelectDirection: (ArrowDirection) -> Unit,
    onSelectColor: (ArrowColor) -> Unit,
    onCellClicked: (r: Int, c: Int) -> Unit,
    onSaveStage: () -> Unit,
    onBack: () -> Unit
) {
    val totalArrows = remember(editorGrid) {
        editorGrid.sumOf { row -> row.count { it?.isArrow() == true } }
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "STAGE CREATOR",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Design & validate custom puzzles",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Grid size picker chips (3x3 to 6x6)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(3, 4, 5, 6).forEach { size ->
                        val isSelected = editorRows == size && editorCols == size
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.25f) else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSetGridSize(size, size) }
                        ) {
                            Text(
                                text = "${size}x${size}",
                                color = if (isSelected) NeonCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stage Title input
            OutlinedTextField(
                value = editorTitle,
                onValueChange = onSetTitle,
                label = { Text("Stage Title") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("editor_title_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Editor Board
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(listOf(DarkBorder, NeonCyan, DarkBorder))
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (r in 0 until editorRows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (c in 0 until editorCols) {
                                    val cell = editorGrid.getOrNull(r)?.getOrNull(c)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clickable { onCellClicked(r, c) }
                                            .testTag("editor_cell_${r}_$c")
                                    ) {
                                        ArrowCellView(cell = cell)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tools Palette (Arrow, Blocker, Portal A, Portal B, Eraser)
            Text(
                text = "TOOL PALETTE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ToolChip(
                    name = "Arrow",
                    isSelected = selectedTool == ArrowGameViewModel.EditorTool.ARROW,
                    color = NeonCyan,
                    onClick = { onSelectTool(ArrowGameViewModel.EditorTool.ARROW) },
                    modifier = Modifier.weight(1f)
                )
                ToolChip(
                    name = "Barrier",
                    isSelected = selectedTool == ArrowGameViewModel.EditorTool.OBSTACLE,
                    color = NeonAmber,
                    onClick = { onSelectTool(ArrowGameViewModel.EditorTool.OBSTACLE) },
                    modifier = Modifier.weight(1f)
                )
                ToolChip(
                    name = "Portal α",
                    isSelected = selectedTool == ArrowGameViewModel.EditorTool.PORTAL_A,
                    color = NeonPurple,
                    onClick = { onSelectTool(ArrowGameViewModel.EditorTool.PORTAL_A) },
                    modifier = Modifier.weight(1f)
                )
                ToolChip(
                    name = "Portal β",
                    isSelected = selectedTool == ArrowGameViewModel.EditorTool.PORTAL_B,
                    color = NeonMagenta,
                    onClick = { onSelectTool(ArrowGameViewModel.EditorTool.PORTAL_B) },
                    modifier = Modifier.weight(1f)
                )
                ToolChip(
                    name = "Erase",
                    isSelected = selectedTool == ArrowGameViewModel.EditorTool.ERASE,
                    color = Color(0xFFEF4444),
                    onClick = { onSelectTool(ArrowGameViewModel.EditorTool.ERASE) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Direction Chooser (if tool == ARROW)
            if (selectedTool == ArrowGameViewModel.EditorTool.ARROW) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ARROW DIRECTION",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ArrowDirection.entries.forEach { dir ->
                        val isSelected = selectedDirection == dir
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else DarkBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectDirection(dir) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dir.symbol,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Color chooser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArrowColor.entries.forEach { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color.toComposeColor())
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onSelectColor(color) }
                        )
                    }
                }
            }

            // Validation Message Display
            if (validationMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (validationMessage.contains("✅")) NeonGreen.copy(alpha = 0.15f) else Color(0x33EF4444),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (validationMessage.contains("✅")) NeonGreen else Color(0xFFEF4444)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationMessage,
                        color = if (validationMessage.contains("✅")) NeonGreen else Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save & Validate Button
            NeonButton(
                text = "VALIDATE & SAVE STAGE",
                onClick = onSaveStage,
                neonColor = NeonGreen,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                },
                testTag = "save_custom_stage_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ToolChip(
    name: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) color else DarkBorder
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = name,
            color = if (isSelected) color else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
