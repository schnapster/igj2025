package dev.capybaralabs.igj2025.elements

import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import kotlin.math.min

// entity should not go below its floor (y coordinate)
class ThrownComponent(
	val floor: Float,
	val onFloored: () -> Unit,
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
			println("Hit the floor")
			thrownComponent.onFloored()
		}
		position.y = min(thrownComponent.floor, position.y)
	}
}

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
