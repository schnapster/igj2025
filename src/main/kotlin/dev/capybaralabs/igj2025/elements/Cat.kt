package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Texture
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Entity
import kotlin.math.min

class CatEntity(
	val position: Vector2 = kvector2(getScreenWidth() / 2, getScreenHeight() - 200),
	directionInput: DirectionInputComponent = DirectionInputComponent(),
	val texture: Texture = CAT_TEXTURE_DEFAULT,
) : Entity() {

	companion object {
		val CAT_TEXTURE_DEFAULT: Texture = loadTexture("assets/image/cats_idle_aim.png")
		val CAT_TEXTURE_WHITE: Texture = loadTexture("assets/image/cat_idle_white.png")
		val CAT_TEXTURE_ORANGE: Texture = loadTexture("assets/image/cat_idle_red.png")
		val CAT_TEXTURE_BLUE: Texture = loadTexture("assets/image/cat_idle_blue.png")
	}

	private val scale = 0.3

	init {
		//movement
		addComponent(SimplePositionComponent(position))
		addComponent(DirectionComponent(kvector2(0, 0)))
		addComponent(SpeedComponent(400f))
		addComponent(
			BorderComponent(
				marginTop = ((texture.height / 2f) * scale).toFloat() + 50f,
				marginBottom = ((texture.height / 2f) * scale).toFloat(),
				marginLeft = ((texture.width / 2f) * scale).toFloat(),
				marginRight = ((texture.width / 2f) * scale).toFloat(),
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
		return (position.y + (texture.height / 2f) * scale).toFloat()
	}

	fun handsPosition(): Vector2 {
		return position + kvector2(0, -100)
	}
}
