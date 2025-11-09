package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Rectangle
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

class TextureComponent(
	val texture: Texture,
	val highlight: Texture? = null,
) : Component

class ScaleComponent(
	val scale: Float,
) : Component


data class TextureRenderData(
	val textureRect: Rectangle,
	val targetRect: Rectangle,
	val textureCenter: Vector2,
	val rotation: Float,
	val scale: Float,
)

// relational meaning that the position of the object is its center, and the
// render happens relational to it in all directions
open class RelationalTextureRenderSystem : System {

	companion object {
		val TRANSPARENT_MAGENTA = kcolor(255, 0, 255, 128)

		fun calculateRenderData(
			entity: Entity,
			position: Vector2,
			texture: Texture,
		): TextureRenderData {
			val scale = entity.findComponent(ScaleComponent::class)?.scale ?: 1f
			val rotation = entity.findComponent(RotatingComponent::class)?.rotation ?: 0f

			val textureRect = krectangle(kvector2(0, 0), texture.size())
			val targetRect = krectangle(position, texture.size() * scale)
			val textureCenter = texture.size() / 2f * scale

			return TextureRenderData(
				textureRect = textureRect,
				targetRect = targetRect,
				textureCenter = textureCenter,
				rotation = rotation,
				scale = scale,
			)
		}
	}

	override fun render(entity: Entity) {
		val position = entity.findComponent(PositionComponent::class)?.position
		val texture = entity.findComponent(TextureComponent::class)
		if (position == null || texture == null) {
			return
		}
		if (entity is CatEntity) return // handled by RelationalCatTextureRenderSystem
		if (entity is EnemyEntity) return // handled by RelationalEnemyTextureRenderSystem


		render(entity, position, texture.texture, texture.highlight)
	}


	fun render(entity: Entity, position: Vector2, texture: Texture, highlight: Texture?) {
		val (textureRect, targetRect, textureCenter, rotation, scale) = calculateRenderData(entity, position, texture)

		if (highlight != null) {
			drawTexturePro(highlight, textureRect, targetRect, textureCenter, rotation, WHITE)
		}

		drawTexturePro(texture, textureRect, targetRect, textureCenter, rotation, WHITE)
	}
}

class RelationalEnemyTextureRenderSystem : RelationalTextureRenderSystem() {
	override fun render(entity: Entity) {
		val enemy = entity as? EnemyEntity
		val position = entity.findComponent(PositionComponent::class)?.position
		if (position == null || enemy == null) {
			return
		}

		val texture: Texture
		val highlight: Texture
		if (enemy.isFlipped()) {
			texture = EnemyEntity.ENEMY_TEXTURE_FLIPPED
			highlight = EnemyEntity.TOUCHAREA_TEXTURE_FLIPPED
		} else {
			texture = EnemyEntity.ENEMY_TEXTURE
			highlight = EnemyEntity.TOUCHAREA_TEXTURE
		}
		render(entity, position, texture, highlight)
	}
}


class RelationalCatTextureRenderSystem : RelationalTextureRenderSystem() {

	override fun render(entities: Set<Entity>) {
		val book = entities.filterIsInstance<BookEntity>().first()
		for (entity in entities) {
			render(entity, book)
		}
	}

	private fun render(entity: Entity, book: BookEntity) {
		val cat = entity as? CatEntity
		val position = entity.findComponent(PositionComponent::class)?.position
		if (position == null || cat == null) {
			return
		}

		var texture = entity.texturePack.idle
		var highlight: Texture? = entity.texturePack.idleHighlight
		val isFlying = book.findComponent(FlyingComponent::class)
		if (isFlying != null && isFlying.targetCat == cat) {
			texture = entity.texturePack.stretched
			highlight = entity.texturePack.stretchedHighlight
		}

		val shouldHighlight = entity.hasComponent(FocusedCatComponent::class)
		if (!shouldHighlight) {
			highlight = null
		}

		render(cat, position, texture, highlight)
	}
}

