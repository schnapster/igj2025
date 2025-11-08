package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Entity
import kotlin.math.min

class CatEntity(
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
	directionInput: DirectionInputComponent = DirectionInputComponent(),

	) : Entity() {
	companion object {
		private val CAT_TEXTURE: Texture = loadTexture("assets/image/cats_idle_aim.png")
	}

	private val scale = 0.3

	init {
		val texture = CAT_TEXTURE

		//movement
		addComponent(SimplePositionComponent(position))
		addComponent(DirectionComponent(kvector2(0, 0)))
		addComponent(SpeedComponent(400f))
		addComponent(
			BorderComponent(
				marginTop = ((CAT_TEXTURE.height / 2f) * scale).toFloat() + 50f,
				marginBottom = ((CAT_TEXTURE.height / 2f) * scale).toFloat(),
				marginLeft = ((CAT_TEXTURE.width / 2f) * scale).toFloat(),
				marginRight = ((CAT_TEXTURE.width / 2f) * scale).toFloat(),
			),
		)

		val radius = min(texture.width, texture.height) / 2f
		addComponent(CircleShapeComponent(radius))

		// input
		addComponent(directionInput)

		// rendering
		addComponent(TextureComponent(texture))
		addComponent(ScaleComponent(scale))
	}

	fun floor(): Float {
		return (position.y + (CAT_TEXTURE.height / 2f) * scale).toFloat()
	}

	fun handsPosition(): Vector2 {
		return position + kvector2(0, -100)
	}
}
