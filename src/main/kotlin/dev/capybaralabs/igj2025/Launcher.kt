package dev.capybaralabs.igj2025

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.Scene
import dev.capybaralabs.igj2025.elements.AiInputSystem
import dev.capybaralabs.igj2025.elements.BackgroundEntity
import dev.capybaralabs.igj2025.elements.BackgroundRenderSystem
import dev.capybaralabs.igj2025.elements.BookCatchSystem
import dev.capybaralabs.igj2025.elements.BookCollectionSystem
import dev.capybaralabs.igj2025.elements.BookEntity
import dev.capybaralabs.igj2025.elements.BookFlyingSystem
import dev.capybaralabs.igj2025.elements.BookLaunchSystemCatToCat
import dev.capybaralabs.igj2025.elements.BorderSystem
import dev.capybaralabs.igj2025.elements.CatEntity
import dev.capybaralabs.igj2025.elements.CatTexturePack.Companion.ALL_CATS
import dev.capybaralabs.igj2025.elements.CatchBookEnemySystem
import dev.capybaralabs.igj2025.elements.ControlledDirectionInputComponent
import dev.capybaralabs.igj2025.elements.ControlledDirectionInputSystem
import dev.capybaralabs.igj2025.elements.DirectionAiComponent
import dev.capybaralabs.igj2025.elements.DirectionInputComponent
import dev.capybaralabs.igj2025.elements.EnemyEntity
import dev.capybaralabs.igj2025.elements.FocusCatSystem
import dev.capybaralabs.igj2025.elements.FpsUiSystem
import dev.capybaralabs.igj2025.elements.GravitySystem
import dev.capybaralabs.igj2025.elements.Highscore
import dev.capybaralabs.igj2025.elements.LocalFileHighscoreApi
import dev.capybaralabs.igj2025.elements.MoveSystem
import dev.capybaralabs.igj2025.elements.RelationalCatTextureRenderSystem
import dev.capybaralabs.igj2025.elements.RelationalTextureRenderSystem
import dev.capybaralabs.igj2025.elements.RotationSystem
import dev.capybaralabs.igj2025.elements.ScoreComponent
import dev.capybaralabs.igj2025.elements.ScoreUiSystem
import dev.capybaralabs.igj2025.elements.TextComponent
import dev.capybaralabs.igj2025.elements.TextUiSystem
import dev.capybaralabs.igj2025.elements.ThrowSystem
import dev.capybaralabs.igj2025.elements.horizontalOrientation
import dev.capybaralabs.igj2025.elements.kvector2
import dev.capybaralabs.igj2025.elements.ui.EndScreen
import dev.capybaralabs.igj2025.elements.ui.HighscoreElement
import dev.capybaralabs.igj2025.elements.ui.StartScreen
import dev.capybaralabs.igj2025.elements.verticalOrientation
import dev.capybaralabs.igj2025.system.AssetLoader
import java.io.File
import java.time.Instant

enum class ScreenState() {
	START,
	GAME,
	END
}

var screenState = ScreenState.START
lateinit var game: Scene
var ingamePointHolder: Entity = Entity()
var currentPoints: Float? = 0f
var endScreenPointHolder: TextComponent? = null
var endScreenHighscoreHolder: TextComponent? = null


lateinit var tempDir: File

private lateinit var api: LocalFileHighscoreApi
private lateinit var dbPath: String


fun main() {
//	setConfigFlags(FLAG_WINDOW_RESIZABLE)
	// getting the monitor is BROKEN on both Macs :/
	// so we cannot dynamically scale the size
//	val monitor = getCurrentMonitor()
//	val monitorHeight = getMonitorHeight(monitor)
//	val monitorWidth = getMonitorWidth(monitor)
	initWindow(1200, 900, "Henlo!")
//	toggleFullscreen()
//	toggleBorderlessWindowed()
	setExitKey(KEY_ESCAPE)
	setTargetFPS(144)
	initAudioDevice()


	tempDir = File(System.getProperty("java.io.tmpdir")) // Add this
	dbPath = File(tempDir, "test_highscores.db").absolutePath
	api = LocalFileHighscoreApi(dbPath)

	println("tempDir: $tempDir, exists: ${tempDir.exists()}, isDirectory: ${tempDir.isDirectory}")
	println("dbPath: $dbPath")

	game = setupGame()

	val startScreen = Scene()
	startScreen.addSystem(BackgroundRenderSystem())
	startScreen.addUiSystem(TextUiSystem())

	startScreen.addEntity(BackgroundEntity(backgroundTextureStartScreen))
	startScreen.addEntity(StartScreen())

	val endScreen = Scene()
	endScreen.addSystem(BackgroundRenderSystem())
	endScreen.addSystem(RelationalTextureRenderSystem())
	endScreen.addUiSystem(TextUiSystem())
	endScreen.addEntity(BackgroundEntity(backgroundTextureEndScreen))
	endScreen.addEntity(EndScreen())
	val highscore = HighscoreElement()
	val pointHolder = TextComponent(
		text = currentPoints.toString(),
		verticalOrientation = verticalOrientation.BOTTOM,
		horizontalOrientation = horizontalOrientation.RIGHT,
		verticalMargin = getScreenHeight() / 2 - 60,
		horizontalMargin = getScreenWidth() / 4 - 50,
		fontSize = 50,
		color = BLUE,
	)
	endScreenPointHolder = pointHolder
	highscore.addComponent(pointHolder)
	val highscoreHolder = TextComponent(
		text = "GET IT WORKING B*TCH",
		verticalOrientation = verticalOrientation.BOTTOM,
		horizontalOrientation = horizontalOrientation.RIGHT,
		verticalMargin = getScreenHeight() / 5,
		horizontalMargin = getScreenWidth() / 3,
		fontSize = 50,
		color = BLUE,
	)
	endScreenHighscoreHolder = highscoreHolder
	highscore.addComponent(highscoreHolder)
	endScreen.addEntity(highscore)

	while (!windowShouldClose()) {

		// state changes
		if (isKeyReleased(KEY_ENTER) && screenState == ScreenState.START) {
			screenState = ScreenState.GAME
		}
		if (isKeyReleased(KEY_ENTER) && screenState == ScreenState.END) {
			screenState = ScreenState.START
		}

		// updates
		val dt = getFrameTime()

		if (screenState == ScreenState.START) {
			startScreen.update(dt)
		}
		if (screenState == ScreenState.GAME) {
			game.update(dt)
		}
		if (screenState == ScreenState.END) {
			endScreen.update(dt)
		}

		//rendering
		beginDrawing()

		if (screenState == ScreenState.START) {
			startScreen.render()
		}
		if (screenState == ScreenState.GAME) {
			game.render()
		}
		if (screenState == ScreenState.END) {
			game.close()
			endScreen.render()
		}

		endDrawing()
	}

	game.close()
	closeAudioDevice()
	closeWindow()
}

fun spawnThreeCatsWasdSwitcherAndBook(scene: Scene) {
	val texturePacks = ALL_CATS.shuffled().take(3).toMutableList()

	val catA = CatEntity(position = kvector2(getScreenWidth() / 5, getScreenHeight() - 200), texturePack = texturePacks.removeFirst())
	val catB = CatEntity(position = kvector2(getScreenWidth() / 5 * 4, getScreenHeight() - 200), texturePack = texturePacks.removeFirst())
	val catC = CatEntity(position = kvector2(getScreenWidth() / 2, getScreenHeight() / 3 - 200), texturePack = texturePacks.removeFirst())

	val cats = setOf(catA, catB, catC)

	val book = BookEntity(catA)
	ingamePointHolder = book

	val controller = ControlledDirectionInputComponent(cats) {
		book.controlledCat()
	}
	book.addComponent(controller)
	book.addComponent(DirectionInputComponent())

	val enemy = EnemyEntity(
		position = kvector2(getScreenWidth() / 5 * 4, getScreenHeight() - 200),
		directionAiInput = DirectionAiComponent(
			cats,
			book,
		),
		handleOnBookCatch = { onBookCatch() },
	)
	scene.addEntities(*cats.toTypedArray(), book, enemy)
}

private fun setupGame(): Scene {
	val game = Scene()

	game.addSystem(MoveSystem())
	game.addSystem(BorderSystem())
	game.addSystem(ControlledDirectionInputSystem())
	game.addSystem(AiInputSystem())

	game.addSystem(BackgroundRenderSystem())
	game.addEntity(BackgroundEntity(backgroundTextureGame))
	game.addSystem(RelationalTextureRenderSystem())
	game.addSystem(RelationalCatTextureRenderSystem())

	spawnThreeCatsWasdSwitcherAndBook(game)

//	game.addSystem(BookLaunchSystem())
	game.addSystem(BookLaunchSystemCatToCat())
	game.addSystem(BookFlyingSystem())
	game.addSystem(BookCatchSystem())
	game.addSystem(CatchBookEnemySystem())
	game.addSystem(FocusCatSystem())

	game.addSystem(GravitySystem())
	game.addSystem(BookCollectionSystem())
	game.addSystem(ThrowSystem())
	game.addSystem(RotationSystem())

	game.addUiSystem(FpsUiSystem())
	game.addUiSystem(ScoreUiSystem())

	return game
}

private fun onBookCatch() {
	currentPoints = ingamePointHolder.findComponent(ScoreComponent::class)?.score
	val highscores = api.highscores()
	var newHighscore = 0f
	if (highscores != null) {
		if (highscores.isNotEmpty()) {
			val lastHighscore = highscores.first()
			newHighscore = lastHighscore.score
		}
		if (currentPoints != null && newHighscore < currentPoints!!) {

			newHighscore = currentPoints!!

			val highscore = Highscore(
				score = currentPoints!!,
				name = "TestPlayer",
				ts = Instant.now(),
			)
			api.addHighscore(highscore)
		}
		endScreenHighscoreHolder?.text = newHighscore.toInt().toString()
	}
	endScreenPointHolder?.text = currentPoints?.toInt().toString()
	screenState = ScreenState.END
	game = setupGame()
}

private val backgroundTextureGame by lazy {
	AssetLoader.loadTexture("assets/image/gj_bg.png")
}

private val backgroundTextureStartScreen by lazy {
	AssetLoader.loadTexture("assets/image/startscreenBG.png")
}

private val backgroundTextureEndScreen by lazy {
	AssetLoader.loadTexture("assets/image/endScreen_BG.png")
}
