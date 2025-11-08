package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.MouseButton.MOUSE_BUTTON_LEFT
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import java.util.concurrent.ThreadLocalRandom

class BookEntity(
	var attachedToCat: CatEntity? = null,
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
): Entity() {

	companion object {
		private val BOOK_TEXTURE: Texture = loadTexture("assets/image/book_idle.png")
	}

	val scale = 0.4

	init {
		val texture = BOOK_TEXTURE
		addComponent(HeldByCatPositionComponent({ this.attachedToCat }, position))

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(scale))
		addComponent(SimpleWallComponent(((texture.width / 2f) * scale).toFloat()))


		addComponent(BookLaunchComponent())
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


class BookLaunchComponent(
	var start: Vector2? = null,
): Component

class BookLaunchSystem() : System {

	override fun update(dt: Float, entity: Entity) {

		val book = entity as? BookEntity ?: return

		val throwComponent = book.findComponent(BookLaunchComponent::class) ?: return
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
		val pullVector = end - start


		// unattach from cat
		book.position.x = heldByCat.position.x
		book.position.y = heldByCat.position.y
		book.attachedToCat = null


		// update speed & direction based on throw vector
		val speed = (pullVector.x + pullVector.y) / 100
		val launchDirection = pullVector * -1

		val rotationSpeed = getRandomValue(10, 40).toFloat()
		val rotationClockwise = ThreadLocalRandom.current().nextBoolean()


		val speedComponent = book.findComponent(SpeedComponent::class)
			?.also { it.speed = speed }
			?: SpeedComponent(speed)
		val directionComponent = book.findComponent(DirectionComponent::class)
			?.also { it.direction = launchDirection }
			?: DirectionComponent(launchDirection)
		val rotatingComponent = book.findComponent(RotatingComponent::class)
			?.also {
				it.rotationSpeed = rotationSpeed
				it.clockwise = rotationClockwise
				it.paused = false
			}
			?: RotatingComponent(rotationSpeed, rotationClockwise)

		val thrownComponent = ThrownComponent(cat.floor()) {
			rotatingComponent.paused = true
			speedComponent.speed = 0f
		}

		book.addComponents(
			speedComponent,
			directionComponent,
			GravityAffectedComponent,
			rotatingComponent,
			thrownComponent,
		)
	}
}
