package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.CustomStagesScreen
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.GameSettingsDialog
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PlayerProfileDialog
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StageEditorScreen
import com.example.ui.screens.StageSelectScreen
import com.example.ui.theme.ArrowPuzzleTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.ArrowGameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ArrowGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArrowPuzzleTheme {
                ArrowPuzzleApp(viewModel)
            }
        }
    }
}

@Composable
fun ArrowPuzzleApp(viewModel: ArrowGameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allProgress by viewModel.allProgress.collectAsStateWithLifecycle()
    val customStages by viewModel.customStages.collectAsStateWithLifecycle()
    val activeStage by viewModel.activeStage.collectAsStateWithLifecycle()
    val currentGrid by viewModel.currentGrid.collectAsStateWithLifecycle()
    val movesCount by viewModel.movesCount.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()
    val victoryData by viewModel.victoryData.collectAsStateWithLifecycle()
    val blockedCell by viewModel.blockedCell.collectAsStateWithLifecycle()
    val hintHighlightCell by viewModel.hintHighlightCell.collectAsStateWithLifecycle()

    // Editor states
    val editorRows by viewModel.editorRows.collectAsStateWithLifecycle()
    val editorCols by viewModel.editorCols.collectAsStateWithLifecycle()
    val editorGrid by viewModel.editorGrid.collectAsStateWithLifecycle()
    val editorTitle by viewModel.editorTitle.collectAsStateWithLifecycle()
    val selectedTool by viewModel.selectedTool.collectAsStateWithLifecycle()
    val selectedDirection by viewModel.selectedDirection.collectAsStateWithLifecycle()
    val selectedColor by viewModel.selectedColor.collectAsStateWithLifecycle()
    val editorValidationMsg by viewModel.editorValidationMessage.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Android System Back Button Handling
    BackHandler(enabled = currentScreen != AppScreen.LOBBY && currentScreen != AppScreen.SPLASH) {
        when (currentScreen) {
            AppScreen.LOGIN -> viewModel.navigateTo(AppScreen.SPLASH)
            AppScreen.STAGE_SELECT -> viewModel.navigateTo(AppScreen.LOBBY)
            AppScreen.GAME_PLAY -> viewModel.navigateTo(AppScreen.STAGE_SELECT)
            AppScreen.STAGE_EDITOR -> viewModel.navigateTo(AppScreen.LOBBY)
            AppScreen.CUSTOM_STAGES -> viewModel.navigateTo(AppScreen.LOBBY)
            else -> viewModel.navigateTo(AppScreen.LOBBY)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onGoogleLogin = { viewModel.loginWithGoogle() },
                            onContinueAsGuest = { viewModel.navigateTo(AppScreen.LOGIN) }
                        )
                    }
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            currentProfile = userProfile,
                            onLoginAsGuest = { name, avatarIndex ->
                                viewModel.updateUserProfile(
                                    name = name,
                                    avatar = avatarIndex,
                                    sound = userProfile.soundEnabled,
                                    haptics = userProfile.hapticsEnabled,
                                    glow = userProfile.glowEffectsEnabled
                                )
                                viewModel.navigateTo(AppScreen.LOBBY)
                            },
                            onGoogleLogin = { viewModel.loginWithGoogle() }
                        )
                    }
                    AppScreen.LOBBY -> {
                        LobbyScreen(
                            userProfile = userProfile,
                            allProgress = allProgress,
                            onPlayClick = { viewModel.playNextUnlockedStage() },
                            onStageSelectClick = { viewModel.navigateTo(AppScreen.STAGE_SELECT) },
                            onCreateStageClick = { viewModel.navigateTo(AppScreen.STAGE_EDITOR) },
                            onCustomStagesClick = { viewModel.navigateTo(AppScreen.CUSTOM_STAGES) },
                            onProfileClick = { showProfileDialog = true },
                            onSettingsClick = { showSettingsDialog = true }
                        )
                    }
                    AppScreen.STAGE_SELECT -> {
                        StageSelectScreen(
                            allProgress = allProgress,
                            onSelectStage = { stageId -> viewModel.startStage(stageId) },
                            onBack = { viewModel.navigateTo(AppScreen.LOBBY) }
                        )
                    }
                    AppScreen.GAME_PLAY -> {
                        GamePlayScreen(
                            stage = activeStage,
                            grid = currentGrid,
                            movesCount = movesCount,
                            elapsedSeconds = elapsedSeconds,
                            blockedCell = blockedCell,
                            hintHighlightCell = hintHighlightCell,
                            victoryData = victoryData,
                            canUndo = true,
                            onCellTapped = { r, c -> viewModel.onCellTapped(r, c) },
                            onUndo = { viewModel.undoMove() },
                            onRestart = { viewModel.restartStage() },
                            onHint = { viewModel.requestHint() },
                            onBack = { viewModel.navigateTo(AppScreen.STAGE_SELECT) },
                            onNextStage = { viewModel.playNextStage() },
                            onLobby = { viewModel.navigateTo(AppScreen.LOBBY) }
                        )
                    }
                    AppScreen.STAGE_EDITOR -> {
                        StageEditorScreen(
                            editorRows = editorRows,
                            editorCols = editorCols,
                            editorGrid = editorGrid,
                            editorTitle = editorTitle,
                            selectedTool = selectedTool,
                            selectedDirection = selectedDirection,
                            selectedColor = selectedColor,
                            validationMessage = editorValidationMsg,
                            onSetGridSize = { r, c -> viewModel.setEditorGridSize(r, c) },
                            onSetTitle = { viewModel.setEditorTitle(it) },
                            onSelectTool = { viewModel.selectEditorTool(it) },
                            onSelectDirection = { viewModel.selectEditorDirection(it) },
                            onSelectColor = { viewModel.selectEditorColor(it) },
                            onCellClicked = { r, c -> viewModel.onEditorCellClicked(r, c) },
                            onSaveStage = {
                                viewModel.validateAndSaveCustomStage {
                                    viewModel.navigateTo(AppScreen.CUSTOM_STAGES)
                                }
                            },
                            onBack = { viewModel.navigateTo(AppScreen.LOBBY) }
                        )
                    }
                    AppScreen.CUSTOM_STAGES -> {
                        CustomStagesScreen(
                            customStages = customStages,
                            onPlayCustomStage = { stage -> viewModel.startCustomStage(stage) },
                            onCreateNewStage = { viewModel.navigateTo(AppScreen.STAGE_EDITOR) },
                            onDeleteStage = { id -> viewModel.deleteCustomStage(id) },
                            onBack = { viewModel.navigateTo(AppScreen.LOBBY) }
                        )
                    }
                    AppScreen.PROFILE_STATS -> {
                        viewModel.navigateTo(AppScreen.LOBBY)
                    }
                }
            }

            // Dedicated Player Profile Modal
            if (showProfileDialog) {
                PlayerProfileDialog(
                    userProfile = userProfile,
                    totalStars = allProgress.sumOf { it.stars },
                    completedStages = allProgress.count { it.completed },
                    customStagesCount = customStages.size,
                    onSaveProfile = { name, avatar ->
                        viewModel.updateUserProfile(
                            name = name,
                            avatar = avatar,
                            sound = userProfile.soundEnabled,
                            haptics = userProfile.hapticsEnabled,
                            glow = userProfile.glowEffectsEnabled
                        )
                    },
                    onDismiss = { showProfileDialog = false }
                )
            }

            // Dedicated Game Settings Modal
            if (showSettingsDialog) {
                GameSettingsDialog(
                    userProfile = userProfile,
                    onSaveSettings = { sound, haptics, glow ->
                        viewModel.updateUserProfile(
                            name = userProfile.guestName,
                            avatar = userProfile.avatarIndex,
                            sound = sound,
                            haptics = haptics,
                            glow = glow
                        )
                    },
                    onResetProgress = { viewModel.resetAllProgress() },
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }
    }
}
