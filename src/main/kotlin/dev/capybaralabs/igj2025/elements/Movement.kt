package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*
import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.*


interface PositionComponent: Component {
	val position: Vector2
}

class SimplePositionComponent(
	override val position: Vector2,
) : PositionComponent


class DirectionComponent(
	val direction: Vector2,
) : Component

class SpeedComponent(
	val speed: Int,
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

		position.x += direction.x * speed * dt
		position.y += direction.y * speed * dt
	}
}
