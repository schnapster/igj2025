package dev.capybaralabs.igj2025.elements.ui

import com.raylib.Raylib.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.elements.ScaleComponent
import dev.capybaralabs.igj2025.elements.SimplePositionComponent
import dev.capybaralabs.igj2025.elements.TextureComponent
import dev.capybaralabs.igj2025.elements.kvector2
import dev.capybaralabs.igj2025.system.AssetLoader

class HighscoreElement() : Entity() {
	companion object {
		private val HIGHSCORE_TEXTURE: Texture =
			AssetLoader.loadTexture("assets/image/highscore_default.png")
	}

	val scale = 0.6
	val position: Vector2 = kvector2(getScreenWidth() / 2 - 80, getScreenHeight() / 3 + 50)

	init {
		val texture = HIGHSCORE_TEXTURE

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(scale))
		addComponent(SimplePositionComponent(position))
	}
}
