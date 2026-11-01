package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.example.data.models.ArrowCell
import com.example.data.models.ArrowColor
import com.example.data.models.ArrowDirection
import com.example.data.models.CellType
import com.example.data.models.PuzzleStage
import com.example.data.models.StageProgress
import com.example.data.models.UserProfile
import com.example.game.generator.StageRepository
import com.example.game.solver.PuzzleEngine
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StageEditorScreen
import com.example.ui.screens.StageSelectScreen
import com.example.ui.screens.VictoryDialog
import com.example.ui.theme.ArrowPuzzleTheme
import com.example.ui.theme.DarkBackground
import com.example.viewmodel.ArrowGameViewModel
import com.example.viewmodel.VictoryData
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Renders the real Compose screens of the game to PNG files so the app can be
 * inspected in the Base44 preview, which runs in a browser and therefore cannot
 * host a native Android UI. Screens are driven through the normal Compose test
 * APIs (taps on the real board cells), so what is captured is the app's own UI.
 *
 * Output directory comes from PREVIEW_SHOTS_DIR (set by docker-compose.base44.yml).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class PreviewScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val outputDir: File
    get() = File(System.getenv("PREVIEW_SHOTS_DIR") ?: "src/test/screenshots")

  private fun capture(name: String) {
    outputDir.mkdirs()
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "${outputDir.absolutePath}/$name.png")
  }

  private fun progressFixture(): List<StageProgress> = (1..100).map { id ->
    when {
      id <= 5 -> StageProgress(id, stars = 3, bestMoves = 7 + id, bestTimeSeconds = 30 + id, unlocked = true, completed = true)
      id == 6 -> StageProgress(id, unlocked = true)
      id == 7 -> StageProgress(id, unlocked = true)
      else -> StageProgress(id)
    }
  }

  private fun editorFixture(): List<List<ArrowCell?>> = List(5) { r ->
    List(5) { c ->
      when {
        (r + c) % 3 == 0 -> ArrowCell(
          id = r * 5 + c,
          type = CellType.ARROW,
          direction = ArrowDirection.entries[(r + c) % ArrowDirection.entries.size],
          color = ArrowColor.entries[c % ArrowColor.entries.size]
        )
        r == 2 && c == 2 -> ArrowCell(id = 100, type = CellType.OBSTACLE)
        else -> null
      }
    }
  }

  @Test
  fun splash_screen() {
    composeTestRule.setContent {
      ArrowPuzzleTheme { SplashScreen(onGoogleLogin = {}, onContinueAsGuest = {}) }
    }
    capture("01-splash")
  }

  @Test
  fun login_screen() {
    composeTestRule.setContent {
      ArrowPuzzleTheme {
        LoginScreen(
          currentProfile = UserProfile(guestName = "Velocity_Pilot", avatarIndex = 0),
          onLoginAsGuest = { _, _ -> },
          onGoogleLogin = {}
        )
      }
    }
    capture("02-login")
  }

  @Test
  fun lobby_screen() {
    composeTestRule.setContent {
      ArrowPuzzleTheme {
        LobbyScreen(
          userProfile = UserProfile(guestName = "Velocity_Pilot", avatarIndex = 1),
          allProgress = progressFixture(),
          onPlayClick = {},
          onStageSelectClick = {},
          onCreateStageClick = {},
          onCustomStagesClick = {},
          onProfileClick = {},
          onSettingsClick = {}
        )
      }
    }
    capture("03-lobby")
  }

  @Test
  fun stage_select_screen() {
    composeTestRule.setContent {
      ArrowPuzzleTheme {
        StageSelectScreen(allProgress = progressFixture(), onSelectStage = {}, onBack = {})
      }
    }
    capture("04-stage-select")
  }

  /** Renders the board, then plays a real move by tapping a board cell. */
  @Test
  fun gameplay_screen_and_move() {
    val stage: PuzzleStage = StageRepository.getStage(4)
    var grid by mutableStateOf(stage.grid.map { row -> row.toList() })
    var moves by mutableStateOf(0)
    var blocked by mutableStateOf<Pair<Int, Int>?>(null)

    composeTestRule.setContent {
      ArrowPuzzleTheme {
        GamePlayScreen(
          stage = stage,
          grid = grid,
          movesCount = moves,
          elapsedSeconds = 37,
          blockedCell = blocked,
          hintHighlightCell = null,
          victoryData = null,
          canUndo = moves > 0,
          onCellTapped = { r, c ->
            when (val result = PuzzleEngine.attemptMove(grid, r, c)) {
              is PuzzleEngine.TapResult.Escaped -> {
                grid = result.newGrid
                moves += 1
                blocked = null
              }
              is PuzzleEngine.TapResult.Blocked -> blocked = result.blockedAtRow to result.blockedAtCol
              is PuzzleEngine.TapResult.NotAnArrow -> Unit
            }
          },
          onUndo = {},
          onRestart = {},
          onHint = {},
          onBack = {},
          onNextStage = {},
          onLobby = {}
        )
      }
    }
    capture("05-gameplay")

    // Find an arrow that can actually escape, and tap it through the real board UI.
    val move = grid.indices.asSequence()
      .flatMap { r -> grid[r].indices.asSequence().map { c -> r to c } }
      .firstOrNull { (r, c) -> PuzzleEngine.attemptMove(grid, r, c) is PuzzleEngine.TapResult.Escaped }

    if (move != null) {
      composeTestRule.onNodeWithTag("cell_${move.first}_${move.second}").performClick()
      capture("06-gameplay-after-move")
    }
  }

  @Test
  fun stage_editor_screen() {
    composeTestRule.setContent {
      ArrowPuzzleTheme {
        StageEditorScreen(
          editorRows = 5,
          editorCols = 5,
          editorGrid = editorFixture(),
          editorTitle = "Neon Gauntlet",
          selectedTool = ArrowGameViewModel.EditorTool.ARROW,
          selectedDirection = ArrowDirection.RIGHT,
          selectedColor = ArrowColor.CYAN,
          validationMessage = null,
          onSetGridSize = { _, _ -> },
          onSetTitle = {},
          onSelectTool = {},
          onSelectDirection = {},
          onSelectColor = {},
          onCellClicked = { _, _ -> },
          onSaveStage = {},
          onBack = {}
        )
      }
    }
    capture("07-stage-editor")
  }

  @Test
  fun victory_dialog() {
    val stage = StageRepository.getStage(2)
    composeTestRule.setContent {
      ArrowPuzzleTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
          Box(modifier = Modifier.fillMaxSize()) {
            VictoryDialog(
              data = VictoryData(stage = stage, stars = 3, moves = 9, parMoves = 12, timeSeconds = 48),
              onNextStage = {},
              onReplay = {},
              onLobby = {}
            )
          }
        }
      }
    }
    composeTestRule.waitForIdle()
    outputDir.mkdirs()
    composeTestRule.onNodeWithTag("victory_dialog")
      .captureRoboImage(filePath = "${outputDir.absolutePath}/08-victory.png")
  }
}
