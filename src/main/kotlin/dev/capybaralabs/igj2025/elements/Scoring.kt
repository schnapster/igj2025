package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

class ScoreComponent(
	var score: Float = 0f,
) : Component

class ScoreUiSystem() : System {

	override fun render(entity: Entity) {
		val scoreComponent = entity.findComponent(ScoreComponent::class) ?: return

		val score = (scoreComponent.score / 100).toInt()

		drawText("Score: $score", 20, 20, 50, GREEN)
	}
}
