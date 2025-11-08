package dev.capybaralabs.igj2025

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import dev.capybaralabs.igj2025.ecs.Scene
import dev.capybaralabs.igj2025.elements.AiInputSystem
import dev.capybaralabs.igj2025.elements.BookCatchSystem
import dev.capybaralabs.igj2025.elements.BookCollectionSystem
import dev.capybaralabs.igj2025.elements.BookEntity
import dev.capybaralabs.igj2025.elements.BookFlyingSystem
import dev.capybaralabs.igj2025.elements.BookLaunchSystemCatToCat
import dev.capybaralabs.igj2025.elements.BorderSystem
import dev.capybaralabs.igj2025.elements.CatEntity
import dev.capybaralabs.igj2025.elements.CatEntity.Companion.CAT_TEXTURE_BLUE
import dev.capybaralabs.igj2025.elements.CatEntity.Companion.CAT_TEXTURE_ORANGE
import dev.capybaralabs.igj2025.elements.ControlledDirectionInputComponent
import dev.capybaralabs.igj2025.elements.ControlledDirectionInputSystem
import dev.capybaralabs.igj2025.elements.DirectionAiComponent
import dev.capybaralabs.igj2025.elements.DirectionInputComponent
import dev.capybaralabs.igj2025.elements.EnemyEntity
import dev.capybaralabs.igj2025.elements.FocusCatSystem
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

	val game = Scene()

	game.addSystem(MoveSystem())
	game.addSystem(BorderSystem())
	game.addSystem(ControlledDirectionInputSystem())
	game.addSystem(AiInputSystem())

	game.addSystem(RelationalTextureRenderSystem())

	spawnThreeCatsWasdSwitcherAndBook(game)

//	game.addSystem(BookLaunchSystem())
	game.addSystem(BookLaunchSystemCatToCat())
	game.addSystem(BookFlyingSystem())
	game.addSystem(BookCatchSystem())
	game.addSystem(FocusCatSystem())

	game.addSystem(GravitySystem())
	game.addSystem(BookCollectionSystem())
	game.addSystem(ThrowSystem())
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

fun spawnThreeCatsWasdSwitcherAndBook(scene: Scene) {
	val catA = CatEntity(position = kvector2(getScreenWidth() / 5, getScreenHeight() - 200), texture = CAT_TEXTURE_BLUE)
	val catB = CatEntity(position = kvector2(getScreenWidth() / 5 * 4, getScreenHeight() - 200), texture = CAT_TEXTURE_ORANGE)
	val catC = CatEntity(position = kvector2(getScreenWidth() / 2, getScreenHeight() / 3 - 200))

	val cats = setOf(catA, catB, catC)

	val book = BookEntity(catA)

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
	)
	scene.addEntities(*cats.toTypedArray(), book, enemy)
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
