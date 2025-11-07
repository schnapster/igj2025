package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Texture
import dev.capybaralabs.igj2025.ecs.*

class TextureComponent(
	val texture: Texture,
) : Component

class ScaleComponent(
	val scale: Double,
) : Component

// relational meaning that the position of the object is its center, and the
// render happens relational to it in all directions
class RelationalTextureRenderSystem : System {
	override fun render(entity: Entity) {
		val position = entity.findComponent(PositionComponent::class)?.position
		val texture = entity.findComponent(TextureComponent::class)?.texture
		if (position == null || texture == null) {
			return
		}
		val scale = entity.findComponent(ScaleComponent::class)?.scale ?: 1.0


		val rotation = entity.findComponent(RotatingComponent::class)?.rotation ?: 0f


		val textureRect = krectangle(kvector2(0, 0), texture.size())
		val targetRect = krectangle(position, texture.size() * scale)
		val textureCenter = texture.size() / 2f * scale

		drawTexturePro(texture, textureRect, targetRect, textureCenter, rotation, WHITE)
	}
}
