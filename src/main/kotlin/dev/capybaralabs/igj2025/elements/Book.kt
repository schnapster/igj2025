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
	var attachedCat: CatEntity? = null,
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
): Entity() {

	companion object {
		private val BOOK_TEXTURE: Texture = loadTexture("assets/image/book_idle.png")
	}


	init {
		val texture = BOOK_TEXTURE
		addComponent(CatRelativePositionComponent({ this.attachedCat }, position))

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(0.4))


		addComponent(BookThrowComponent())
	}

}

// used by the book to be rendered above a cat
class CatRelativePositionComponent(
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


		// update the position to be the cat: TODO need to offset just like the render?
		book.position.x = book.attachedCat?.position?.x ?: book.position.x
		book.position.y = book.attachedCat?.position?.y ?: book.position.y
		book.attachedCat = null


		// update speed & direction based to throw vector
		val speed = (throwVector.x + throwVector.y) / 100
		book.addComponent(SpeedComponent(speed.toInt()))
		book.addComponent(DirectionComponent(throwVector * -1))

	}
}
