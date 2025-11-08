package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import kotlin.math.min

class CatEntity(
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
	directionInput: DirectionInputComponent = DirectionInputComponent(),
	val texture: Texture = CAT_TEXTURE_DEFAULT,
) : Entity() {

	companion object {
		val CAT_TEXTURE_DEFAULT: Texture = loadTexture("assets/image/cats_idle_aim.png")
		val CAT_TEXTURE_WHITE: Texture = loadTexture("assets/image/cat_idle_white.png")
		val CAT_TEXTURE_ORANGE: Texture = loadTexture("assets/image/cat_idle_red.png")
		val CAT_TEXTURE_BLUE: Texture = loadTexture("assets/image/cat_idle_blue.png")
		val CAT_HIGHLIGHT_TEXTURE_STRETCH: Texture = loadTexture("assets/image/cat_highlight_stretched.png")
		val CAT_HIGHLIGHT_TEXTURE_IDLE: Texture = loadTexture("assets/image/cat_highlight_idle.png")
	}

	private val scale = 0.3

	init {
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
		addComponent(TextureComponent(texture, CAT_HIGHLIGHT_TEXTURE_IDLE))
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
		if (isKeyReleased(KEY_TAB)
			|| isKeyReleased(KEY_M)
		) {
			val nextFocussedCat = cats
				.filterNot { it == controlledCat }
				.filterNot { it == focusedUncontrolledCat }
				.first()
			focusedUncontrolledCat.removeComponent(FocusedCatComponent)
			nextFocussedCat.addComponent(FocusedCatComponent)
		}
	}
}

