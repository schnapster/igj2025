package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import dev.capybaralabs.igj2025.elements.BookUITexturePack.Companion.BOOKUI_TP
import dev.capybaralabs.igj2025.system.AssetLoader

data class BookUITexturePack(
	val base: Texture,
	val p1: Texture,
	val p2: Texture,
	val p3: Texture,
	val p4: Texture,
	val p5: Texture,
	val p6: Texture,
	val iconFreeze: Texture,
	val iconSlowmo: Texture,
	val iconSpeed: Texture,
	val iconTeleport: Texture,
) {
	companion object {
		val BASE: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_base.png") }
		val P1: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_p1.png") }
		val P2: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_p2.png") }
		val P3: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_p3.png") }
		val P4: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_p4.png") }
		val P5: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_p5.png") }
		val P6: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_p6.png") }
		val ICON_FREEZE: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_icon-freeze.png") }
		val ICON_SLOWMO: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_icon-slowmo.png") }
		val ICON_SPEED: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_icon-speed.png") }
		val ICON_TELEPORT: Texture by lazy { AssetLoader.loadTexture("assets/image/bookui/book_ui_icon-teleport.png") }

		val BOOKUI_TP by lazy { BookUITexturePack(BASE, P1, P2, P3, P4, P5, P6, ICON_FREEZE, ICON_SLOWMO, ICON_SPEED, ICON_TELEPORT) }
	}
}

class ScoreComponent(
	var score: Int = 0,
) : Component

class ScoreUiSystem() : System {

	override fun render(entity: Entity) {
		val scoreComponent = entity.findComponent(ScoreComponent::class) ?: return

		val score = scoreComponent.score

		drawText("$score", 40, getScreenHeight() - 150, 60, YELLOW)
	}
}

class InGameUi() : Entity() {
	init {
		addComponent(
			TextComponent(
				text = { "Move - [ARROW KEYS]" },
				verticalOrientation = VerticalOrientation.BOTTOM,
				horizontalOrientation = HorizontalOrientation.LEFT,
				verticalMargin = 75,
				horizontalMargin = 20,
				fontSize = 20,
				color = YELLOW,
			),
		)
		addComponent(
			TextComponent(
				text = { "Select Cat to throw to - [TAB]" },
				verticalOrientation = VerticalOrientation.BOTTOM,
				horizontalOrientation = HorizontalOrientation.LEFT,
				verticalMargin = 50,
				horizontalMargin = 20,
				fontSize = 20,
				color = YELLOW,
			),
		)
		addComponent(
			TextComponent(
				text = { "Throw - [SPACE]" },
				verticalOrientation = VerticalOrientation.BOTTOM,
				horizontalOrientation = HorizontalOrientation.LEFT,
				verticalMargin = 25,
				horizontalMargin = 20,
				fontSize = 20,
				color = YELLOW,
			),
		)
	}
}


open class BookUiSystem : System {
	private var currentPageTexture: Texture? = null
	private var nextIconTexture: Texture? = null
	private var lastScore: Int = 0

	// Array for efficient page texture lookup
	private val pageTextures: Array<Texture?> by lazy {
		arrayOf(
			null, // page 0 - no texture
			BOOKUI_TP.p1,
			BOOKUI_TP.p2,
			BOOKUI_TP.p3,
			BOOKUI_TP.p4,
			BOOKUI_TP.p5,
			BOOKUI_TP.p6,
		)
	}

	override fun render(entity: Entity) {
		if (entity !is BookUI) return

		val position = entity.findComponent(PositionComponent::class)?.position ?: return
		val texture = entity.findComponent(TextureComponent::class) ?: return

		render(entity, position, texture.texture)
		nextIconTexture?.let { render(entity, position, it) }
		currentPageTexture?.let { render(entity, position, it) }
	}

	fun render(entity: Entity, position: Vector2, texture: Texture) {
		val scale = entity.findComponent(ScaleComponent::class)?.scale ?: 1f
		val rotation = entity.findComponent(RotatingComponent::class)?.rotation ?: 0f

		val textureRect = krectangle(kvector2(0, 0), texture.size())
		val targetRect = krectangle(position, texture.size() * scale)
		val textureCenter = texture.size() / 2f * scale

		drawTexturePro(texture, textureRect, targetRect, textureCenter, rotation, WHITE)
	}

	override fun update(dt: Float, entities: Set<Entity>) {
		// Find the BookEntity to track its score
		val bookEntity = entities.filterIsInstance<BookEntity>().firstOrNull()
		val bookUI = entities.filterIsInstance<BookUI>().firstOrNull()

		if (bookEntity != null && bookUI != null) {
			updateScoreAndPages(bookEntity, bookUI)
		}

		// Update UI for all BookUI entities
		entities.filterIsInstance<BookUI>().forEach { bookUIEntity ->
			updateBookUI(bookUIEntity)
		}
	}

	private fun updateScoreAndPages(bookEntity: BookEntity, bookUI: BookUI) {
		val scoreComponent = bookEntity.findComponent(ScoreComponent::class) ?: return
		val state = bookUI.findComponent(BookStateComponent::class) ?: return

		val currentScore = scoreComponent.score

		// Check if score has increased
		if (currentScore > lastScore) {
			val pointsGained = currentScore - lastScore
			lastScore = currentScore

			// Update the currentPoints to track actual score
			state.currentPoints = currentScore

			// Turn pages for each point gained
			for (i in 1..pointsGained) {
				state.currentPage++

				// If we've gone past page 6, cycle back to page 1 and randomize mode
				if (state.currentPage > 6) {
					state.currentPage = 1
					state.currentMode = state.nextMode

					// Randomly select a new mode (excluding DEFAULT)
					val availableModes = Mode.values().filter { it != state.currentMode && it != Mode.TELEPORT }
					state.nextMode = availableModes.random()

					println("Cycled to page 1 with new mode: ${state.nextMode}")
				} else if(currentScore == 1){
					// Randomly select a new mode (excluding DEFAULT)
					val availableModes = Mode.values().filter { it != state.currentMode && it != Mode.TELEPORT }
					state.nextMode = availableModes.random()
				}
			}

			println("Score: $currentScore, Page: ${state.currentPage}, Mode: ${state.nextMode}")
		}
	}

	private fun updateBookUI(bookUI: Entity) {
		val state = bookUI.findComponent(BookStateComponent::class) ?: return

		// Update page texture using array lookup
		currentPageTexture = pageTextures.getOrNull(state.currentPage)

		// Update icon texture based on mode
		nextIconTexture = when (state.nextMode) {
			Mode.FREEZE -> BOOKUI_TP.iconFreeze
			Mode.SLOWMO -> BOOKUI_TP.iconSlowmo
			Mode.SPEED -> BOOKUI_TP.iconSpeed
			Mode.TELEPORT -> BOOKUI_TP.iconTeleport
			Mode.DEFAULT -> null
		}
	}
}

enum class Mode {
	DEFAULT, FREEZE, SLOWMO, SPEED, TELEPORT
}

class BookStateComponent(
	var currentPoints: Int = 0,
	var currentPage: Int = 0,
	var currentMode: Mode = Mode.DEFAULT,
	var nextMode: Mode = Mode.DEFAULT,
) : Component

class BookUI() : Entity() {
	init {
		val texture: BookUITexturePack = BOOKUI_TP

		addComponent(TextureComponent(texture.base))
		addComponent(
			SimplePositionComponent(
				kvector2(130, 120),
			),
		)
		addComponent(ScaleComponent(0.5f))
		addComponent(BookStateComponent(0, 0, Mode.DEFAULT, nextMode = Mode.DEFAULT))
	}
}
