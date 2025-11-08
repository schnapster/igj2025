package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Entity
import kotlin.math.min


class EnemyEntity(
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
	initialSpeed: Float = 200f,
	directionAiInput: DirectionAiComponent = DirectionAiComponent(),
) : Entity() {
	companion object {
		private val ENEMY_TEXTURE: Texture = loadTexture("assets/image/enemy.png")
	}

	private val scale = 0.35

	init {
		val texture = ENEMY_TEXTURE

		//movement
		addComponent(SimplePositionComponent(position))
		addComponent(DirectionComponent(kvector2(0, 0)))
		addComponent(SpeedComponent(initialSpeed))
		addComponent(
			BorderComponent(
				marginTop = ((ENEMY_TEXTURE.height / 2f) * scale).toFloat() + 50f,
				marginBottom = ((ENEMY_TEXTURE.height / 2f) * scale).toFloat(),
				marginLeft = ((ENEMY_TEXTURE.width / 2f) * scale).toFloat(),
				marginRight = ((ENEMY_TEXTURE.width / 2f) * scale).toFloat(),
			),
		)

		val radius = min(texture.width, texture.height) / 2f
		addComponent(CircleShapeComponent(radius))

		// movement
		addComponent(directionAiInput)

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(scale))
	}
}
