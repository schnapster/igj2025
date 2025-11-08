package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System


interface PositionComponent: Component {
	val position: Vector2
}

class SimplePositionComponent(
	override val position: Vector2,
) : PositionComponent


class DirectionComponent(
	var direction: Vector2,
) : Component

class SpeedComponent(
	var speed: Float,
) : Component

class SimpleWallComponent(
	var baseMargin: Float = 0f,
	var marginTop: Float = 0f,
	var marginBottom: Float = 0f,
	var marginLeft: Float = 0f,
	var marginRight: Float = 0f,
) : Component


class DirectionInputComponent(
	val upKey: Int = KEY_W,
	val downKey: Int = KEY_S,
	val leftKey: Int = KEY_A,
	val rightKey: Int = KEY_D,
) : Component


class DirectionInputSystem : System {
	override fun update(dt: Float, entity: Entity) {
		val direction = entity.findComponent(DirectionComponent::class)?.direction
		val input = entity.findComponent(DirectionInputComponent::class)
		if (direction == null || input == null) {
			return
		}

		direction.x = (isKeyDown(input.rightKey).toInt() - isKeyDown(input.leftKey).toInt()).toFloat()
		direction.y = (isKeyDown(input.downKey).toInt() - isKeyDown(input.upKey).toInt()).toFloat()
		val normalized = vector2Normalize(direction)
		direction.x = normalized.x
		direction.y = normalized.y
	}
}


class MoveSystem : System {
	override fun update(dt: Float, entity: Entity) {
		val position = entity.findComponent(PositionComponent::class)?.position
		val direction = entity.findComponent(DirectionComponent::class)?.direction
		val speed = entity.findComponent(SpeedComponent::class)?.speed
		if (position == null || direction == null || speed == null) {
			return
		}
		val normalizedDirection = vector2Normalize(direction)

		var borderTop = 0f;
		var borderBottom = getScreenHeight().toFloat();
		var borderLeft = 0f;
		var borderRight = getScreenWidth().toFloat();

		val wallComponent = entity.findComponent(SimpleWallComponent::class)
		if (wallComponent != null) {

			val baseMargin = wallComponent.baseMargin
			val marginTop = wallComponent.marginTop + baseMargin
			val marginBottom = wallComponent.marginBottom + baseMargin
			val marginLeft = wallComponent.marginLeft + baseMargin
			val marginRight = wallComponent.marginRight + baseMargin

			borderTop += marginTop
			borderBottom -= marginBottom
			borderLeft += marginLeft
			borderRight -= marginRight

		}

		var newPositionX = position.x + normalizedDirection.x * speed * dt
		var newPositionY = position.y + normalizedDirection.y * speed * dt

		if (newPositionX < borderLeft) {
			newPositionX = borderLeft;
		}
		if (newPositionX > borderRight) {
			newPositionX = borderRight;
		}
		if (newPositionY < borderTop) {
			newPositionY = borderTop;
		}
		if (newPositionY > borderBottom) {
			newPositionY = borderBottom;
		}

		position.x = newPositionX
		position.y = newPositionY
	}
}
