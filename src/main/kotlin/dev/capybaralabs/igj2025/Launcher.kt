package dev.capybaralabs.igj2025

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import dev.capybaralabs.igj2025.ecs.Game
import dev.capybaralabs.igj2025.elements.BookEntity
import dev.capybaralabs.igj2025.elements.BookThrowSystem
import dev.capybaralabs.igj2025.elements.CatEntity
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

	val cat = CatEntity()
	game.addEntity(cat)
	game.addEntity(BookEntity(cat))

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
