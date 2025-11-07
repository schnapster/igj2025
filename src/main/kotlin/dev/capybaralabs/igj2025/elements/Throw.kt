package dev.capybaralabs.igj2025.elements

import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import kotlin.math.min

// entity should not go below its floor (y coordinate)
class ThrownComponent(
	val floor: Float,
) : Component

class ThrowSystem : System {

	override fun update(dt: Float, entity: Entity) {
		val thrownComponent = entity.findComponent(ThrownComponent::class)
		val position = entity.findComponent(PositionComponent::class)?.position
		val speedComponent = entity.findComponent(SpeedComponent::class)
		if (thrownComponent == null || position == null || speedComponent == null) {
			return
		}
		if (position.y >= thrownComponent.floor && speedComponent.speed != 0f) {
			// we hit the floor, stop moving.
			println("Hit the floor, stopping")
			speedComponent.speed = 0f
		}
		position.y = min(thrownComponent.floor, position.y)
	}
}

//class AirDragSystem : System {
//
//	override fun update(dt: Float, entity: Entity) {
//		val thrownComponent = entity.findComponent(ThrownComponent::class)
//		val direction = entity.findComponent(DirectionComponent::class)
//		val speedComponent = entity.findComponent(SpeedComponent::class)
//		if (thrownComponent == null || direction == null || speedComponent == null) {
//			return
//		}
//
//		var velocity = direction.direction * speedComponent.speed * dt
//		val drag = 0.1 //must be between 0 and 1!
//
//		velocity *= (1 - drag) * dt
//		speedComponent.speed = velocity.length()
//		direction.direction = Raylib.vector2Normalize(velocity)
//	}
//}


object GravityAffectedComponent : Component

class GravitySystem() : System {

	companion object {
		const val GRAVITY = 420
	}

	override fun update(dt: Float, entity: Entity) {
		entity.findComponent(GravityAffectedComponent::class) ?: return

		val direction = entity.findComponent(DirectionComponent::class) ?: return
		direction.direction.y += GRAVITY * dt
	}
}
