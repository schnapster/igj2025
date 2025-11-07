package dev.capybaralabs.igj2025.elements

import dev.capybaralabs.igj2025.ecs.*

class RotatingComponent(
	val rotationSpeed: Float, // degrees
	val clockwise: Boolean = true,
) : Component {

	var rotation = 0f
}


class RotationSystem : System {

	override fun update(dt: Float, entity: Entity) {
		val rotating = entity.findComponent(RotatingComponent::class)
		if (rotating == null) {
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
