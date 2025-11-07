package dev.capybaralabs.igj2025

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.Game
import dev.capybaralabs.igj2025.elements.BookEntity
import dev.capybaralabs.igj2025.elements.BookThrowSystem
import dev.capybaralabs.igj2025.elements.CircleShapeComponent
import dev.capybaralabs.igj2025.elements.DirectionComponent
import dev.capybaralabs.igj2025.elements.DirectionInputComponent
import dev.capybaralabs.igj2025.elements.DirectionInputSystem
import dev.capybaralabs.igj2025.elements.GravitySystem
import dev.capybaralabs.igj2025.elements.MoveSystem
import dev.capybaralabs.igj2025.elements.RelationalTextureRenderSystem
import dev.capybaralabs.igj2025.elements.ScaleComponent
import dev.capybaralabs.igj2025.elements.SimplePositionComponent
import dev.capybaralabs.igj2025.elements.SpeedComponent
import dev.capybaralabs.igj2025.elements.TextureComponent
import dev.capybaralabs.igj2025.elements.ThrowSystem
import dev.capybaralabs.igj2025.elements.UiFpsSystem
import dev.capybaralabs.igj2025.elements.kvector2
import kotlin.math.max
import kotlin.math.min

fun main() {
	initWindow(1200, 900, "Henlo!")
//	toggleFullscreen()
	toggleBorderlessWindowed()
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

class CatEntity(
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),

	) : Entity() {
	companion object {
		private val CAT_TEXTURE: Texture = loadTexture("assets/image/cats_idle_aim.png")
	}

	private val scale = 0.3

	init {
		val texture = CAT_TEXTURE

		//movement
		addComponent(SimplePositionComponent(position))
		addComponent(DirectionComponent(kvector2(0, 0)))
		addComponent(SpeedComponent(400f))

		val radius = min(texture.width, texture.height) / 2f
		addComponent(CircleShapeComponent(radius))

		// input
		addComponent(DirectionInputComponent())

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(scale))
	}

	fun floor(): Float {
		return (position.y + (CAT_TEXTURE.height / 2f) * scale).toFloat()
	}
}
