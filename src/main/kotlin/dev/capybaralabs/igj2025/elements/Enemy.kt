package dev.capybaralabs.igj2025.elements

import com.raylib.Image
import com.raylib.Raylib
import com.raylib.Raylib.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
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
			val enemyTexture = enemy.findComponent(TextureComponent::class) ?: continue
			val touchAreaTexture = enemyTexture.highlight ?: continue
			val enemyPos = enemy.position
			val enemyScale = enemy.findComponent(ScaleComponent::class)?.scale ?: continue

			for (book in targetEntities) {
				val bookTexture = book.findComponent(TextureComponent::class)?.texture ?: continue
				val bookPos = book.position
				val bookScale = book.findComponent(ScaleComponent::class)?.scale ?: continue

				// Check pixel-perfect collision
				if (checkPixelPerfectCollision(
						touchAreaTexture, enemyPos, enemyScale,
						bookTexture, bookPos, bookScale,
					)
				) {
					// Handle collision: catch the book
					enemy.findComponent(CatchBookEnemyComponent::class)?.handleOnBookCatch()
				}
			}
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


	private fun checkPixelPerfectCollision(
		texture1: Texture, pos1: Vector2, scale1: Float,
		texture2: Texture, pos2: Vector2, scale2: Float,
	): Boolean {
		// Calculate bounding boxes (centered sprites)
		val width1 = (texture1.width * scale1)
		val height1 = (texture1.height * scale1)
		val rect1 = krectangle(
			pos1.x - width1 / 2f,
			pos1.y - height1 / 2f,
			width1,
			height1,
		)

		val width2 = (texture2.width * scale2)
		val height2 = (texture2.height * scale2)
		val rect2 = krectangle(
			pos2.x - width2 / 2f,
			pos2.y - height2 / 2f,
			width2,
			height2,
		)

		// First check if bounding boxes overlap
		if (!checkCollisionRecs(rect1, rect2)) {
			return false
		}

		// Get the overlapping region
		val collision = getCollisionRec(rect1, rect2)
		if (collision.width <= 0 || collision.height <= 0) {
			return false
		}

		// Load images for pixel checking
		val image1 = loadImage(texture1)
		val image2 = loadImage(texture2)

		// Check pixels in the overlapping region
		val startX = collision.x.toInt()
		val startY = collision.y.toInt()
		val endX = (collision.x + collision.width).toInt()
		val endY = (collision.y + collision.height).toInt()

		for (y in startY until endY) {
			for (x in startX until endX) {
				// Convert world coordinates to texture coordinates
				val tex1X = ((x - rect1.x) / scale1).toInt()
				val tex1Y = ((y - rect1.y) / scale1).toInt()
				val tex2X = ((x - rect2.x) / scale2).toInt()
				val tex2Y = ((y - rect2.y) / scale2).toInt()

				// Check bounds
				if (tex1X >= 0 && tex1X < texture1.width && tex1Y >= 0 && tex1Y < texture1.height &&
					tex2X >= 0 && tex2X < texture2.width && tex2Y >= 0 && tex2Y < texture2.height
				) {

					// Get pixel colors
					val color1 = getImageColor(image1, tex1X, tex1Y)
					val color2 = getImageColor(image2, tex2X, tex2Y)

					// Check if both pixels have opacity > 0
					if (color1.a > 0 && color2.a > 0) {
						return true
					}
				}
			}
		}

		return false
	}

}
