package dev.capybaralabs.igj2025.elements

import com.raylib.Image
import com.raylib.Raylib
import com.raylib.Raylib.*
import com.raylib.Rectangle
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.DEBUG
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import dev.capybaralabs.igj2025.elements.RelationalTextureRenderSystem.Companion.TRANSPARENT_MAGENTA
import dev.capybaralabs.igj2025.system.AssetLoader
import kotlin.math.min

class CatchBookEnemyComponent(onBookCatch: () -> Unit) : Component {
	val handleOnBookCatch = onBookCatch
}

class EnemyEntity(
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
	initialSpeed: Float = 100f,
	directionAiInput: DirectionAiComponent = DirectionAiComponent(),
	handleOnBookCatch: () -> Unit,
) : Entity() {
	companion object {
		private val ENEMY_TEXTURE: Texture = AssetLoader.loadTexture("assets/image/enemy.png")
		private val TOUCHAREA_TEXTURE: Texture = AssetLoader.loadTexture("assets/image/enemy_touch_area.png")
	}

	private val scale = 0.35f

	init {
		val texture = ENEMY_TEXTURE

		//movement
		addComponent(SimplePositionComponent(position))
		addComponent(DirectionComponent(kvector2(0, 0)))
		addComponent(SpeedComponent(initialSpeed))
		addComponent(
			BorderComponent(
				marginTop = ((ENEMY_TEXTURE.height / 2f) * scale) + 50f,
				marginBottom = ((ENEMY_TEXTURE.height / 2f) * scale),
				marginLeft = ((ENEMY_TEXTURE.width / 2f) * scale),
				marginRight = ((ENEMY_TEXTURE.width / 2f) * scale),
			),
		)

		val radius = min(texture.width, texture.height) / 2f
		addComponent(CircleShapeComponent(radius))

		addComponent(CatchBookEnemyComponent(handleOnBookCatch))

		// movement
		addComponent(directionAiInput)

		// rendering
		addComponent(TextureComponent(texture, TOUCHAREA_TEXTURE))
		addComponent(ScaleComponent(scale))
	}
}

class EnemyCatchBookSystem : System {

	override fun update(dt: Float, entities: Set<Entity>) {
		val targetEntities = entities.filterIsInstance<BookEntity>()
		val enemyEntities = entities.filterIsInstance<EnemyEntity>()

		for (enemy in enemyEntities) {
			for (book in targetEntities) {
				val enemyCollisionData = enemyCollionData(enemy)
				val bookCollisionData = bookCollisionData(book)
				val colliding = checkCollisionCircles(
					enemyCollisionData.center, enemyCollisionData.radius,
					bookCollisionData.center, bookCollisionData.radius,
				)

				if (colliding) {
					// Handle collision: catch the book
					enemy.findComponent(CatchBookEnemyComponent::class)?.handleOnBookCatch()
				}
			}
		}
	}

	private data class EnemyCollisionData(
		val center: Vector2,
		val radius: Float,
	)

	private data class BookCollisionData(
		val collisionRect: Rectangle,
		val collisionCenter: Vector2,
		val rotation: Float,

		val center: Vector2,
		val radius: Float,
	)

	private fun enemyCollionData(enemy: EnemyEntity): EnemyCollisionData {
		val texture = enemy.findComponent(TextureComponent::class)!!.texture
		val position = enemy.findComponent(PositionComponent::class)!!.position

		val enemyRenderData = RelationalTextureRenderSystem.calculateRenderData(enemy, position, texture)
		drawCircleV(position, 5f, MAGENTA)

		val offset = kvector2(220, -220) * enemyRenderData.scale
		val radius = 200 / 2 * enemyRenderData.scale

		return EnemyCollisionData(position + offset, radius)
	}

	private fun bookCollisionData(book: BookEntity): BookCollisionData {
		val texture = book.findComponent(TextureComponent::class)!!.texture
		val position = book.findComponent(PositionComponent::class)!!.position

		val bookRenderData = RelationalTextureRenderSystem.calculateRenderData(book, position, texture)
		val targetRect = bookRenderData.targetRect
		val textureCenter = bookRenderData.textureCenter

		val bookSizeFactor = 0.85f
		val bookCollisionRect = krectangle(targetRect.position(), targetRect.size() * bookSizeFactor)
		val bookCollisionCenter = textureCenter * bookSizeFactor


		return BookCollisionData(
			bookCollisionRect, bookCollisionCenter, bookRenderData.rotation,
			position, 50f,
		)
	}

	override fun render(entity: Entity) {
		// DEBUG render collisions
		if (!DEBUG) return


		val enemy = entity as? EnemyEntity
		if (enemy != null) {
			val (center, radius) = enemyCollionData(enemy)
			drawCircleV(center, radius, TRANSPARENT_MAGENTA)
		}

		val book = entity as? BookEntity
		if (book != null) {
			val (collisionRect, collisionCenter, rotation, center, radius) = bookCollisionData(book)
			drawRectanglePro(collisionRect, collisionCenter, rotation, TRANSPARENT_MAGENTA)
			drawCircleV(center, radius, MAGENTA)
		}
	}

	private val imageCache = mutableMapOf<Texture, Image>()
	private fun loadImage(texture: Texture): Image {
		return imageCache.computeIfAbsent(texture, Raylib::loadImageFromTexture)
	}

	override fun close(entities: Set<Entity>) {
		imageCache.values.forEach { unloadImage(it) }
		imageCache.clear()
	}
}
