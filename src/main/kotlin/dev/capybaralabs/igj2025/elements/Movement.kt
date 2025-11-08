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

class DirectionInputComponent(
	val upKey: Int = KEY_W,
	val downKey: Int = KEY_S,
	val leftKey: Int = KEY_A,
	val rightKey: Int = KEY_D,
) : Component

class DirectionAiComponent(
	val players: List<Entity> = listOf(),
	val targetObject: Entity = Entity(),
	val simple: Boolean = true,
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

class AiInputSystem : System {
	override fun update(dt: Float, entity: Entity) {
		val direction = entity.findComponent(DirectionComponent::class)?.direction
		val position = entity.findComponent(PositionComponent::class)?.position
		val players = entity.findComponent(DirectionAiComponent::class)?.players
		val simple = entity.findComponent(DirectionAiComponent::class)?.simple
		val targetObject = entity.findComponent(DirectionAiComponent::class)?.targetObject

		if (direction == null || position == null || players == null || targetObject == null || simple == null) {
			return
		}

		var targetPosition = Vector2()

		val playerWithTarget = targetObject.findComponent(HeldByCatPositionComponent::class)?.attachedCat()

		// if no player with target go for target directly
		if (playerWithTarget == null) {
			val positionOfTarget = targetObject.findComponent(PositionComponent::class)?.position
			if (positionOfTarget == null) {
				return
			}
			targetPosition = positionOfTarget
		} else {
			if (simple) {
				targetPosition = playerWithTarget.position
			} else {
				// prioritize player by distance to main player
				var targetedPlayersSorted = players.filterNot { playerWithTarget == it }.sortedBy { player ->
					val playerPosition = player.findComponent(PositionComponent::class)?.position
					val mainPlayerPosition = playerWithTarget.findComponent(PositionComponent::class)?.position

					vector2Distance(playerPosition, mainPlayerPosition)
				}

				var shuffleBag = mutableListOf<Entity>()
				var i = 1
				for (player in targetedPlayersSorted) {
					for (j in 0 until i) {
						shuffleBag.add(player)
					}
					i++
				}

				val targetedPlayer = shuffleBag.random()
				targetPosition = targetedPlayer.findComponent(PositionComponent::class)?.position ?: targetPosition
			}
		}

		val directionX = targetPosition.x - position.x
		val directionY = targetPosition.y - position.y

		direction.x = directionX
		direction.y = directionY
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
		position.x += normalizedDirection.x * speed * dt
		position.y += normalizedDirection.y * speed * dt
	}
}


class BorderComponent(
	var baseMargin: Float = 0f,
	var marginTop: Float = 0f,
	var marginBottom: Float = 0f,
	var marginLeft: Float = 0f,
	var marginRight: Float = 0f,
) : Component

class BorderSystem : System {
	override fun update(dt: Float, entity: Entity) {
		val position = entity.findComponent(PositionComponent::class)?.position
		val borderComponent = entity.findComponent(BorderComponent::class)

		if (position == null || borderComponent == null) {
			return
		}

		var borderTop = 0f
		var borderBottom = getScreenHeight().toFloat()
		var borderLeft = 0f
		var borderRight = getScreenWidth().toFloat()

		val baseMargin = borderComponent.baseMargin
		val marginTop = borderComponent.marginTop + baseMargin
		val marginBottom = borderComponent.marginBottom + baseMargin
		val marginLeft = borderComponent.marginLeft + baseMargin
		val marginRight = borderComponent.marginRight + baseMargin

		borderTop += marginTop
		borderBottom -= marginBottom
		borderLeft += marginLeft
		borderRight -= marginRight

		if (position.x < borderLeft) {
			position.x = borderLeft;
		}
		if (position.x > borderRight) {
			position.x = borderRight;
		}
		if (position.y < borderTop) {
			position.y = borderTop;
		}
		if (position.y > borderBottom) {
			position.y = borderBottom;
		}
	}
}
