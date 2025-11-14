package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.GamepadButton.*
import com.raylib.Raylib.KeyboardKey.*
import com.raylib.Raylib.MouseButton.MOUSE_BUTTON_RIGHT
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import dev.capybaralabs.igj2025.elements.CatTexturePack.Companion.CAT_DEFAULT
import dev.capybaralabs.igj2025.system.AssetLoader
import kotlin.math.min

data class CatTexturePack(
	val idle: Texture,
	val stretched: Texture,
	val idleHighlight: Texture = CAT_HIGHLIGHT_TEXTURE_IDLE,
	val stretchedHighlight: Texture = CAT_HIGHLIGHT_TEXTURE_STRETCH,
) {

	companion object {
		val CAT_IDLE_DEFAULT: Texture = AssetLoader.loadTexture("assets/image/cat_idle_default.png")
		val CAT_IDLE_WHITE: Texture = AssetLoader.loadTexture("assets/image/cat_idle_white.png")
		val CAT_IDLE_ORANGE: Texture = AssetLoader.loadTexture("assets/image/cat_idle_red.png")
		val CAT_IDLE_BLUE: Texture = AssetLoader.loadTexture("assets/image/cat_idle_blue.png")

		val CAT_STRETCH_DEFAULT: Texture = AssetLoader.loadTexture("assets/image/cat_stretched_default.png")
		val CAT_STRETCH_WHITE: Texture = AssetLoader.loadTexture("assets/image/cat_stretched_white.png")
		val CAT_STRETCH_ORANGE: Texture = AssetLoader.loadTexture("assets/image/cat_stretched_red.png")
		val CAT_STRETCH_BLUE: Texture = AssetLoader.loadTexture("assets/image/cat_stretched_blue.png")

		val CAT_HIGHLIGHT_TEXTURE_STRETCH: Texture = AssetLoader.loadTexture("assets/image/cat_highlight_stretched.png")
		val CAT_HIGHLIGHT_TEXTURE_IDLE: Texture = AssetLoader.loadTexture("assets/image/cat_highlight_idle.png")


		val CAT_DEFAULT = CatTexturePack(CAT_IDLE_DEFAULT, CAT_STRETCH_DEFAULT)
		val CAT_WHITE = CatTexturePack(CAT_IDLE_WHITE, CAT_STRETCH_WHITE)
		val CAT_ORANGE = CatTexturePack(CAT_IDLE_ORANGE, CAT_STRETCH_ORANGE)
		val CAT_BLUE = CatTexturePack(CAT_IDLE_BLUE, CAT_STRETCH_BLUE)

		val ALL_CATS = listOf(CAT_DEFAULT, CAT_WHITE, CAT_ORANGE, CAT_BLUE)
	}
}

class CatEntity(
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
	directionInput: DirectionInputComponent = DirectionInputComponent(),
	val texturePack: CatTexturePack = CAT_DEFAULT,
) : Entity() {

	private val scale = 0.3f

	init {
		val texture = texturePack.idle

		//movement
		addComponent(SimplePositionComponent(position))
		addComponent(DirectionComponent(kvector2(0, 0)))
		addComponent(SpeedComponent(400f))
		addComponent(
			BorderComponent(
				marginTop = ((texture.height / 2f) * scale).toFloat() + 50f,
				marginBottom = ((texture.height / 2f) * scale).toFloat(),
				marginLeft = ((texture.width / 2f) * scale).toFloat(),
				marginRight = ((texture.width / 2f) * scale).toFloat(),
			),
		)

		val radius = min(texture.width, texture.height) / 2f
		addComponent(CircleShapeComponent(radius))

		// input
		addComponent(directionInput)

		// rendering
		addComponent(ScaleComponent(scale))
	}

	fun handsPosition(): Vector2 {
		return position + kvector2(0, -100)
	}
}

object FocusedCatComponent : Component

class FocusCatSystem() : System {

	override fun update(dt: Float, entities: Set<Entity>) {
		val cats = entities.filterIsInstance<CatEntity>()
		for (entity in entities) {
			update(dt, entity, cats)
		}
	}

	private fun update(dt: Float, entity: Entity, cats: List<CatEntity>) {
		val book = entity as? BookEntity ?: return
		val isFlying = book.hasComponent(FlyingComponent::class)
		if (isFlying) return // no updates during flying


		val controlledCat = book.controlledCat()
		// ensure there is exactly one focussed cat, and it is not the controlled one
		var focusedUncontrolledCat = cats
			.filterNot { it == controlledCat }
			.find { it.hasComponent(FocusedCatComponent::class) }
		if (focusedUncontrolledCat == null) {
			focusedUncontrolledCat = cats.filterNot { it == controlledCat }.random()
		}

		cats.filterNot { it == focusedUncontrolledCat }.forEach { it.removeComponent(FocusedCatComponent) }
		focusedUncontrolledCat.addComponent(FocusedCatComponent)

		// on tab press, alternate focus between the non-controlled cats
		val switchKey = isKeyReleased(KEY_TAB)
			|| isKeyReleased(KEY_M)
			|| isMouseButtonReleased(MOUSE_BUTTON_RIGHT)
			|| isGamepadButtonReleased(0, GAMEPAD_BUTTON_RIGHT_FACE_RIGHT)
		if (switchKey) {
			val nextFocussedCat = cats
				.filterNot { it == controlledCat }
				.filterNot { it == focusedUncontrolledCat }
				.first()
			focusedUncontrolledCat.removeComponent(FocusedCatComponent)
			nextFocussedCat.addComponent(FocusedCatComponent)
		}
	}
}

