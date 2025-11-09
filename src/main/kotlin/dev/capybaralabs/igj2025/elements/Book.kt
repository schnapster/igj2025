package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import com.raylib.Raylib.MouseButton.MOUSE_BUTTON_LEFT
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import dev.capybaralabs.igj2025.system.AssetLoader
import java.util.concurrent.ThreadLocalRandom


class BookEntity(
	val defaultCat: CatEntity, // fallback cat for operations that require a cat
	var attachedToCat: CatEntity? = defaultCat,
	var targetCat: CatEntity? = null,
	var onGround: Boolean = false,
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
): Entity() {

	companion object {
		private val BOOK_TEXTURE: Texture =
			AssetLoader.loadTexture("assets/image/book_idle.png")
	}

	val scale = 0.4f

	init {
		val texture = BOOK_TEXTURE
		addComponent(HeldByCatPositionComponent({ this.attachedToCat }, position))

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(scale))
		addComponent(BorderComponent(width() / 2f))

		addComponent(ScoreComponent())
	}

	fun width(): Float {
		return (BOOK_TEXTURE.width * scale)
	}

	fun controlledCat(): CatEntity {
		return attachedToCat ?: targetCat ?: defaultCat
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
		val targetCat = cats.first { it.hasComponent(FocusedCatComponent::class) }
		book.targetCat = targetCat

		// Check if SLOWMO mode is active
		val isSlowmo = book.hasComponent(SlowmoActiveComponent::class)
		val speed = if (isSlowmo) 100f else 800f // Half speed in SLOWMO mode
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

		val throwStart = book.position.copy()
		val thrownComponent = FlyingComponent(targetCat) {
			rotatingComponent.paused = true
			speedComponent.speed = 0f
			val throwEnd = book.position.copy()
			val score = 1
//			val score = (throwStart - throwEnd).length()
			println("Score increased by $score")
			book.findComponent(ScoreComponent::class)?.score += score
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
	override fun update(dt: Float, entities: Set<Entity>) {
		val cats = entities.filterIsInstance<CatEntity>()
		entities.filterIsInstance<BookEntity>().forEach { book ->
			update(dt, book, cats, entities)
		}
	}

	fun update(dt: Float, book: BookEntity, cats: List<CatEntity>, allEntities: Set<Entity>) {
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

			// remove frozen movement component when successfull throw happened
			var wasUnfrozen = false
			cats.forEach { cat ->
				val freezeComponent = cat.findComponent(FrozenMovementComponent::class)
				if (freezeComponent != null) {
					cat.removeComponent(freezeComponent)
					wasUnfrozen = true
				}
			}

			// Show unfreeze notification if cats were unfrozen
			if (wasUnfrozen) {
				val notificationEntity = allEntities.filterIsInstance<ModeNotificationEntity>().firstOrNull()
				notificationEntity?.showNotification("UNFROZEN!", 3f)
			}
		}
	}
}
