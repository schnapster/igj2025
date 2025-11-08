package dev.capybaralabs.igj2025

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import dev.capybaralabs.igj2025.ecs.Game
import dev.capybaralabs.igj2025.elements.BookEntity
import dev.capybaralabs.igj2025.elements.BookThrowSystem
import dev.capybaralabs.igj2025.elements.CatEntity
import dev.capybaralabs.igj2025.elements.DirectionInputComponent
import dev.capybaralabs.igj2025.elements.DirectionInputSystem
import dev.capybaralabs.igj2025.elements.GravitySystem
import dev.capybaralabs.igj2025.elements.MoveSystem
import dev.capybaralabs.igj2025.elements.RelationalTextureRenderSystem
import dev.capybaralabs.igj2025.elements.RotationSystem
import dev.capybaralabs.igj2025.elements.ThrowSystem
import dev.capybaralabs.igj2025.elements.UiFpsSystem
import dev.capybaralabs.igj2025.elements.kvector2
import kotlin.math.max

fun main() {
	initWindow(1200, 900, "Henlo!")
//	toggleFullscreen()
//	toggleBorderlessWindowed()
	setExitKey(KEY_ESCAPE)
	setTargetFPS(144)
	initAudioDevice()

	val game = Game()

	game.addSystem(MoveSystem())
	game.addSystem(DirectionInputSystem())

	game.addSystem(RelationalTextureRenderSystem())

	spawnTwoCatsWasdAndArrowsAndBook(game)

	game.addSystem(BookThrowSystem())
	game.addSystem(GravitySystem())
	game.addSystem(ThrowSystem())
//	game.addSystem(AirDragSystem())
	game.addSystem(RotationSystem())

	game.addUiSystem(UiFpsSystem())

	while (!windowShouldClose()) {
		// updates
		val dt = getFrameTime()
		game.update(dt)

		//rendering
		beginDrawing()
		renderBackground()

		game.render()

		endDrawing()
	}

	game.close()
	closeAudioDevice()
	closeWindow()
}


fun spawnTwoCatsWasdAndArrowsAndBook(game: Game) {
	val wasdCat = CatEntity(
		position = kvector2(getScreenWidth() / 5, getScreenHeight() - 200),
		directionInput = DirectionInputComponent(
			KEY_W,
			KEY_S,
			KEY_A,
			KEY_D,
		),
	)
	game.addEntity(wasdCat)

	val arrowCat = CatEntity(
		position = kvector2(getScreenWidth() / 5 * 4, getScreenHeight() - 200),
		directionInput = DirectionInputComponent(
			KEY_UP,
			KEY_DOWN,
			KEY_LEFT,
			KEY_RIGHT,
		),
	)
	game.addEntity(arrowCat)

	game.addEntity(BookEntity(wasdCat))
}


private val backgroundTexture by lazy {
	loadTexture("assets/image/gj_bg.png")
}
fun renderBackground() {
	drawTextureEx(
		backgroundTexture,
		kvector2(0f, 0f),
		0f,
		max(
			getScreenWidth().toFloat() / backgroundTexture.width,
			getScreenHeight().toFloat() / backgroundTexture.height,
		),
		RAYWHITE,
	)
}
