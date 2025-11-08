package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Texture
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import kotlin.math.max

class BackgroundComponent(
	val texture: Texture,
) : Component

class BackgroundRenderSystem : System {
	override fun render(entity: Entity) {
		val background = entity.findComponent(BackgroundComponent::class)
		if (background == null) {
			return
		}
		if (background.texture != null) {
			drawTextureEx(
				background.texture,
				kvector2(0f, 0f),
				0f,
				max(
					getScreenWidth().toFloat() / background.texture.width,
					getScreenHeight().toFloat() / background.texture.height,
				),
				RAYWHITE,
			)
		}
	}
}

class BackgroundEntity(texture: Texture) : Entity() {
	init {
		addComponent(BackgroundComponent(texture))
	}
}

