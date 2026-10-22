package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.GameRepository
import com.example.data.models.ArrowCell
import com.example.data.models.ArrowColor
import com.example.data.models.ArrowDirection
import com.example.data.models.CellType
import com.example.data.models.FlyingArrowAnimation
import com.example.data.models.PuzzleStage
import com.example.data.models.StageProgress
import com.example.data.models.UserProfile
import com.example.game.audio.SoundEffectsManager
import com.example.game.generator.StageRepository
import com.example.game.solver.PuzzleEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    LOGIN,
    LOBBY,
    STAGE_SELECT,
    GAME_PLAY,
    STAGE_EDITOR,
    CUSTOM_STAGES,
    PROFILE_STATS
}

data class VictoryData(
    val stage: PuzzleStage,
    val stars: Int,
    val moves: Int,
    val parMoves: Int,
    val timeSeconds: Int,
    val isNewBest: Boolean = false
)

class ArrowGameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val soundManager = SoundEffectsManager(application)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())
        viewModelScope.launch {
            repository.initDefaultProgressIfNeeded()
        }
    }

    val allProgress: StateFlow<List<StageProgress>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customStages: StateFlow<List<PuzzleStage>> = repository.customStages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active Game State
    private val _activeStage = MutableStateFlow<PuzzleStage?>(null)
    val activeStage: StateFlow<PuzzleStage?> = _activeStage.asStateFlow()

    private val _currentGrid = MutableStateFlow<List<List<ArrowCell?>>>(emptyList())
    val currentGrid: StateFlow<List<List<ArrowCell?>>> = _currentGrid.asStateFlow()

    private val _movesCount = MutableStateFlow(0)
    val movesCount: StateFlow<Int> = _movesCount.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _hintsUsed = MutableStateFlow(0)
    val hintsUsed: StateFlow<Int> = _hintsUsed.asStateFlow()

    private val _undoStack = MutableStateFlow<List<List<List<ArrowCell?>>>>(emptyList())
    val canUndo: StateFlow<Boolean> = MutableStateFlow(false)

    private val _flyingArrows = MutableStateFlow<List<FlyingArrowAnimation>>(emptyList())
    val flyingArrows: StateFlow<List<FlyingArrowAnimation>> = _flyingArrows.asStateFlow()

    private val _blockedCell = MutableStateFlow<Pair<Int, Int>?>(null)
    val blockedCell: StateFlow<Pair<Int, Int>?> = _blockedCell.asStateFlow()

    private val _hintHighlightCell = MutableStateFlow<Pair<Int, Int>?>(null)
    val hintHighlightCell: StateFlow<Pair<Int, Int>?> = _hintHighlightCell.asStateFlow()

    private val _victoryData = MutableStateFlow<VictoryData?>(null)
    val victoryData: StateFlow<VictoryData?> = _victoryData.asStateFlow()

    private var timerJob: Job? = null

    // Level Editor State
    private val _editorRows = MutableStateFlow(4)
    val editorRows: StateFlow<Int> = _editorRows.asStateFlow()

    private val _editorCols = MutableStateFlow(4)
    val editorCols: StateFlow<Int> = _editorCols.asStateFlow()

    private val _editorGrid = MutableStateFlow<List<List<ArrowCell?>>>(
        List(4) { MutableList(4) { null } }
    )
    val editorGrid: StateFlow<List<List<ArrowCell?>>> = _editorGrid.asStateFlow()

    private val _editorTitle = MutableStateFlow("My Arrow Maze")
    val editorTitle: StateFlow<String> = _editorTitle.asStateFlow()

    private val _selectedTool = MutableStateFlow(EditorTool.ARROW)
    val selectedTool: StateFlow<EditorTool> = _selectedTool.asStateFlow()

    private val _selectedDirection = MutableStateFlow(ArrowDirection.UP)
    val selectedDirection: StateFlow<ArrowDirection> = _selectedDirection.asStateFlow()

    private val _selectedColor = MutableStateFlow(ArrowColor.CYAN)
    val selectedColor: StateFlow<ArrowColor> = _selectedColor.asStateFlow()

    private val _editorValidationMessage = MutableStateFlow<String?>(null)
    val editorValidationMessage: StateFlow<String?> = _editorValidationMessage.asStateFlow()

    enum class EditorTool {
        ARROW, OBSTACLE, PORTAL_A, PORTAL_B, ERASE
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun startStage(stageId: Int) {
        val stage = StageRepository.getStage(stageId)
        loadStage(stage)
    }

    fun startCustomStage(stage: PuzzleStage) {
        loadStage(stage)
    }

    fun playNextUnlockedStage() {
        val progress = allProgress.value
        val nextStageId = (1..100).firstOrNull { id ->
            val p = progress.find { it.stageId == id }
            p == null || !p.completed
        } ?: 1
        startStage(nextStageId)
    }

    private fun loadStage(stage: PuzzleStage) {
        _activeStage.value = stage
        _currentGrid.value = stage.grid.map { it.toList() }
        _movesCount.value = 0
        _elapsedSeconds.value = 0
        _hintsUsed.value = 0
        _undoStack.value = emptyList()
        _flyingArrows.value = emptyList()
        _blockedCell.value = null
        _hintHighlightCell.value = null
        _victoryData.value = null
        _currentScreen.value = AppScreen.GAME_PLAY

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value++
            }
        }
    }

    fun onCellTapped(r: Int, c: Int) {
        if (_victoryData.value != null) return
        val grid = _currentGrid.value
        val stage = _activeStage.value ?: return

        if (r !in grid.indices || c !in grid[r].indices) return
        val cell = grid[r][c] ?: return

        // Clear hint highlight
        _hintHighlightCell.value = null

        when (val result = PuzzleEngine.attemptMove(grid, r, c)) {
            is PuzzleEngine.TapResult.Escaped -> {
                // Save undo
                _undoStack.value = _undoStack.value + listOf(grid)

                // Add flying animation
                val anim = FlyingArrowAnimation(
                    id = cell.id,
                    startRow = r,
                    startCol = c,
                    direction = cell.direction,
                    color = cell.color
                )
                _flyingArrows.value = _flyingArrows.value + anim

                // Update grid & moves
                _currentGrid.value = result.newGrid
                _movesCount.value++
                soundManager.playFlySound()

                // Check victory condition
                val remaining = result.newGrid.sumOf { row -> row.count { it?.isArrow() == true } }
                if (remaining == 0) {
                    onStageCompleted(stage)
                }
            }
            is PuzzleEngine.TapResult.Blocked -> {
                _blockedCell.value = r to c
                _movesCount.value++
                soundManager.playBlockedSound()
                viewModelScope.launch {
                    delay(400)
                    if (_blockedCell.value == (r to c)) {
                        _blockedCell.value = null
                    }
                }
            }
            PuzzleEngine.TapResult.NotAnArrow -> {
                soundManager.playTapSound()
            }
        }
    }

    private fun onStageCompleted(stage: PuzzleStage) {
        timerJob?.cancel()
        val moves = _movesCount.value
        val par = stage.parMoves
        val hints = _hintsUsed.value
        val timeSec = _elapsedSeconds.value

        // Star calculation:
        // 3 Stars: moves <= par && hints == 0
        // 2 Stars: moves <= par + 3
        // 1 Star: cleared
        val stars = when {
            moves <= par && hints == 0 -> 3
            moves <= par + 3 -> 2
            else -> 1
        }

        viewModelScope.launch {
            soundManager.playVictoryFanfare()
            if (!stage.isCustom) {
                repository.saveStageProgress(stage.id, stars, moves, timeSec)
            }
            _victoryData.value = VictoryData(
                stage = stage,
                stars = stars,
                moves = moves,
                parMoves = par,
                timeSeconds = timeSec
            )
        }
    }

    fun undoMove() {
        val stack = _undoStack.value
        if (stack.isNotEmpty()) {
            val previous = stack.last()
            _undoStack.value = stack.dropLast(1)
            _currentGrid.value = previous
            _hintHighlightCell.value = null
            soundManager.playTapSound()
        }
    }

    fun restartStage() {
        val stage = _activeStage.value ?: return
        loadStage(stage)
    }

    fun requestHint() {
        val grid = _currentGrid.value
        val solution = PuzzleEngine.solve(grid)
        if (solution != null && solution.isNotEmpty()) {
            val nextMove = solution.first()
            _hintHighlightCell.value = nextMove
            _hintsUsed.value++
            soundManager.playStarSound(2)
        }
    }

    fun playNextStage() {
        val current = _activeStage.value ?: return
        if (current.isCustom) {
            _currentScreen.value = AppScreen.CUSTOM_STAGES
        } else {
            val nextId = current.id + 1
            if (nextId <= 100) {
                startStage(nextId)
            } else {
                _currentScreen.value = AppScreen.STAGE_SELECT
            }
        }
    }

    // Level Editor actions
    fun setEditorGridSize(rows: Int, cols: Int) {
        _editorRows.value = rows.coerceIn(3, 7)
        _editorCols.value = cols.coerceIn(3, 7)
        _editorGrid.value = List(rows) { MutableList(cols) { null } }
        _editorValidationMessage.value = null
    }

    fun setEditorTitle(title: String) {
        _editorTitle.value = title
    }

    fun selectEditorTool(tool: EditorTool) {
        _selectedTool.value = tool
    }

    fun selectEditorDirection(dir: ArrowDirection) {
        _selectedDirection.value = dir
    }

    fun selectEditorColor(color: ArrowColor) {
        _selectedColor.value = color
    }

    fun onEditorCellClicked(r: Int, c: Int) {
        val rows = _editorRows.value
        val cols = _editorCols.value
        val grid = _editorGrid.value.map { it.toMutableList() }.toMutableList()

        when (_selectedTool.value) {
            EditorTool.ARROW -> {
                val current = grid[r][c]
                if (current != null && current.type == CellType.ARROW) {
                    // Cycle direction or update
                    val nextDir = current.direction.nextClockwise(allowDiagonals = true)
                    grid[r][c] = current.copy(direction = nextDir, color = _selectedColor.value)
                } else {
                    grid[r][c] = ArrowCell(
                        id = r * cols + c + 1,
                        type = CellType.ARROW,
                        direction = _selectedDirection.value,
                        color = _selectedColor.value
                    )
                }
            }
            EditorTool.OBSTACLE -> {
                grid[r][c] = ArrowCell(
                    id = r * cols + c + 1,
                    type = CellType.OBSTACLE,
                    direction = ArrowDirection.UP,
                    color = ArrowColor.CYAN
                )
            }
            EditorTool.PORTAL_A -> {
                grid[r][c] = ArrowCell(
                    id = r * cols + c + 1,
                    type = CellType.PORTAL_A,
                    direction = ArrowDirection.UP,
                    color = ArrowColor.PURPLE
                )
            }
            EditorTool.PORTAL_B -> {
                grid[r][c] = ArrowCell(
                    id = r * cols + c + 1,
                    type = CellType.PORTAL_B,
                    direction = ArrowDirection.UP,
                    color = ArrowColor.PURPLE
                )
            }
            EditorTool.ERASE -> {
                grid[r][c] = null
            }
        }
        _editorGrid.value = grid
        _editorValidationMessage.value = null
        soundManager.playTapSound()
    }

    fun validateAndSaveCustomStage(onSuccess: () -> Unit) {
        val grid = _editorGrid.value
        val totalArrows = grid.sumOf { row -> row.count { it?.isArrow() == true } }

        if (totalArrows == 0) {
            _editorValidationMessage.value = "⚠️ Please place at least one arrow!"
            soundManager.playBlockedSound()
            return
        }

        if (!PuzzleEngine.isSolvable(grid)) {
            _editorValidationMessage.value = "⚠️ Puzzle is not solvable! Check arrow directions and blockers."
            soundManager.playBlockedSound()
            return
        }

        viewModelScope.launch {
            val author = userProfile.value.guestName
            repository.saveCustomStage(
                title = _editorTitle.value,
                author = author,
                rows = _editorRows.value,
                cols = _editorCols.value,
                grid = grid,
                parMoves = totalArrows
            )
            soundManager.playStarSound(3)
            _editorValidationMessage.value = "✅ Stage Verified and Saved Successfully!"
            delay(600)
            onSuccess()
        }
    }

    fun deleteCustomStage(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomStage(id)
            soundManager.playTapSound()
        }
    }

    // Profile & Settings
    fun loginWithGoogle() {
        viewModelScope.launch {
            val current = userProfile.value
            val googleName = if (current.guestName == "Velocity_Pilot" || current.guestName.isBlank()) "Google_Pilot" else current.guestName
            val updated = current.copy(guestName = googleName)
            repository.saveUserProfile(updated)
            soundManager.playFlySound()
            navigateTo(AppScreen.LOBBY)
        }
    }

    fun updateUserProfile(name: String, avatar: Int, sound: Boolean, haptics: Boolean, glow: Boolean) {
        viewModelScope.launch {
            val updated = UserProfile(
                guestName = name.ifBlank { "Velocity_Pilot" },
                avatarIndex = avatar,
                soundEnabled = sound,
                hapticsEnabled = haptics,
                glowEffectsEnabled = glow
            )
            repository.saveUserProfile(updated)
            soundManager.soundEnabled = sound
            soundManager.hapticsEnabled = haptics
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            soundManager.playBlockedSound()
        }
    }
}
