package dev.capybaralabs.igj2025.elements

import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

class RotatingComponent(
	var rotationSpeed: Float, // degrees
	var clockwise: Boolean = true,
) : Component {

	var rotation = 0f
	var paused = false
}


class RotationSystem : System {

	override fun update(dt: Float, entity: Entity) {
		val rotating = entity.findComponent(RotatingComponent::class)
		if (rotating == null || rotating.paused) {
			return
		}

		val direction = if (rotating.clockwise) 1 else -1
		rotating.rotation += (rotating.rotationSpeed * dt * direction)
		rotating.rotation %= 360
		if (rotating.rotation < 0) {
			rotating.rotation += 360
		}
	}
}
