package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import com.raylib.Raylib.MouseButton.MOUSE_BUTTON_LEFT
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs


class BookEntity(
	var attachedToCat: CatEntity? = null,
	var targetCat: CatEntity? = null,
	var onGround: Boolean = false,
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
		addComponent(BorderComponent(width() / 2f))
	}

	fun width(): Float {
		return (BOOK_TEXTURE.width * scale).toFloat()
	}
}

// used by the book to be rendered above a cat
class HeldByCatPositionComponent(
	val attachedCat: () -> CatEntity?,
	val bookPosition: Vector2,
) : PositionComponent {
	override val position: Vector2
		get() = attachedCat()?.handsPosition() ?: bookPosition
}

class BookLaunchComponent(
	var pullStart: Vector2? = null,
): Component

class BookLaunchSystem() : System {
	override fun update(dt: Float, entity: Entity) {

		val book = entity as? BookEntity ?: return

		val launchComponent = book.findComponent(BookLaunchComponent::class) ?: return
		val heldByCat = book.findComponent(HeldByCatPositionComponent::class) ?: return
		val cat = book.attachedToCat ?: return


		if (isMouseButtonPressed(MOUSE_BUTTON_LEFT))  {
			launchComponent.pullStart = getMousePosition()
		}
		val start = launchComponent.pullStart ?: return

		val pullEnd = getMousePosition()
		val pullVector = pullEnd - start

		if (!isMouseButtonReleased(MOUSE_BUTTON_LEFT))  {
			// just render a preview, then leave
			return
		}
		// do the throw!

		// unattach from cat
		book.position.x = heldByCat.position.x
		book.position.y = heldByCat.position.y
		book.attachedToCat = null


		// update speed & direction based on throw vector
		val speed = (abs(pullVector.x) + abs(pullVector.y)) * 2
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
			book.onGround = true
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


class FlyingComponent(
	val targetCat: CatEntity,
	val onArrive: () -> Unit,
) : Component

class BookLaunchSystemCatToCat() : System {

	override fun update(dt: Float, entities: Set<Entity>) {
		val cats = entities.filterIsInstance<CatEntity>()
		for (entity in entities) {
			update(dt, entity, cats)
		}
	}

	private fun update(dt: Float, entity: Entity, cats: List<CatEntity>) {
		val book = entity as? BookEntity ?: return

		val attachedCat = book.attachedToCat ?: return

		if (!isMouseButtonReleased(MOUSE_BUTTON_LEFT)
			&& !isKeyReleased(KEY_SPACE)
		) {
			return
		}
		// do the throw!

		// unattach from cat
		book.position.x = attachedCat.handsPosition().x
		book.position.y = attachedCat.handsPosition().y
		book.attachedToCat = null
		val targetCat = cats.filter { it != attachedCat }.random()
		book.targetCat = targetCat


		val speed = 800f
		val flyDirection = targetCat.handsPosition() - book.position

		val rotationSpeed = getRandomValue(90, 270).toFloat()
		val rotationClockwise = ThreadLocalRandom.current().nextBoolean()


		val speedComponent = book.takeComponent(SpeedComponent::class)
			?.also { it.speed = speed }
			?: SpeedComponent(speed)
		val directionComponent = book.takeComponent(DirectionComponent::class)
			?.also { it.direction = flyDirection }
			?: DirectionComponent(flyDirection)
		val rotatingComponent = book.takeComponent(RotatingComponent::class)
			?.also {
				it.rotationSpeed = rotationSpeed
				it.clockwise = rotationClockwise
				it.paused = false
			}
			?: RotatingComponent(rotationSpeed, rotationClockwise)

		val thrownComponent = FlyingComponent(targetCat) {
			rotatingComponent.paused = true
			speedComponent.speed = 0f
		}

		book.addComponents(
			speedComponent,
			directionComponent,
			rotatingComponent,
			thrownComponent,
		)
	}
}

class BookFlyingSystem() : System {
	override fun update(dt: Float, entity: Entity) {
		val book = entity as? BookEntity ?: return

		val isFlying = book.findComponent(FlyingComponent::class) ?: return

		val flyDirection = isFlying.targetCat.handsPosition() - book.position

		val directionComponent = book.takeComponent(DirectionComponent::class)
			?.also { it.direction = flyDirection }
			?: DirectionComponent(flyDirection)

		book.addComponent(directionComponent)
	}
}

class BookCatchSystem() : System {
	override fun update(dt: Float, entity: Entity) {
		val book = entity as? BookEntity ?: return

		val isFlying = book.findComponent(FlyingComponent::class) ?: return

		// we arrived yet?
		val caught = checkCollisionCircles(
			book.position, book.width() / 2f,
			isFlying.targetCat.handsPosition(), 1f,
		)

		if (caught) {
			isFlying.onArrive()
			book.removeComponent(isFlying)
			book.attachedToCat = book.targetCat
			book.targetCat = null
		}
	}
}
