package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.MouseButton.MOUSE_BUTTON_LEFT
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.CatEntity
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

class BookEntity(
	var attachedToCat: CatEntity? = null,
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
): Entity() {

	companion object {
		private val BOOK_TEXTURE: Texture = loadTexture("assets/image/book_idle.png")
	}


	init {
		val texture = BOOK_TEXTURE
		addComponent(HeldByCatPositionComponent({ this.attachedToCat }, position))

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(0.4))


		addComponent(BookThrowComponent())
	}

}

// used by the book to be rendered above a cat
class HeldByCatPositionComponent(
	val attachedCat: () -> CatEntity?,
	val bookPosition: Vector2,
) : PositionComponent {
	override val position: Vector2
		get() = attachedCat()?.position?.let { it + kvector2(0, -100) } ?: bookPosition
}



class BookThrowComponent(
	var start: Vector2? = null,
): Component

class BookThrowSystem(): System {

	override fun update(dt: Float, entity: Entity) {

		val book = entity as? BookEntity ?: return

		val throwComponent = book.findComponent(BookThrowComponent::class) ?: return
		val heldByCat = book.findComponent(HeldByCatPositionComponent::class) ?: return
		val cat = book.attachedToCat ?: return


		if (isMouseButtonPressed(MOUSE_BUTTON_LEFT))  {
			throwComponent.start = getMousePosition()
		}

		if (!isMouseButtonReleased(MOUSE_BUTTON_LEFT))  {
			// TODO: render preview if start != null?
			return
		}

		// do the throw!
		val start = throwComponent.start ?: return
		val end = getMousePosition()
		val throwVector = end - start


		// unattach from cat
		book.position.x = heldByCat.position.x
		book.position.y = heldByCat.position.y
		book.attachedToCat = null


		// update speed & direction based on throw vector
		val speed = (throwVector.x + throwVector.y) / 100
		book.addComponent(SpeedComponent(speed))
		book.addComponent(DirectionComponent(throwVector * -1))
		book.addComponent(ThrownComponent(cat.floor())) // do not fall below from where it was thrown
		book.addComponent(GravityAffectedComponent)
	}
}
