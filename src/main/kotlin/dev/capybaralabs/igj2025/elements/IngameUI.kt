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

		val score = scoreComponent.score.toInt()
//		val score = (scoreComponent.score / 100).toInt()

		drawText("$score", 40, getScreenHeight() - 150, 60, YELLOW)
	}
}

class InGameUi() : Entity() {
	init {
		addComponent(
			TextComponent(
				text = "Move - [WASD]",
				verticalOrientation = verticalOrientation.BOTTOM,
				horizontalOrientation = horizontalOrientation.LEFT,
				verticalMargin = 75,
				horizontalMargin = 20,
				fontSize = 20,
				color = YELLOW,
			),
		)
		addComponent(
			TextComponent(
				text = "Move - [WASD]",
				verticalOrientation = verticalOrientation.BOTTOM,
				horizontalOrientation = horizontalOrientation.LEFT,
				verticalMargin = 75,
				horizontalMargin = 20,
				fontSize = 20,
				color = YELLOW,
			),
		)
		addComponent(
			TextComponent(
				text = "Select Cat to throw to - [M]",
				verticalOrientation = verticalOrientation.BOTTOM,
				horizontalOrientation = horizontalOrientation.LEFT,
				verticalMargin = 50,
				horizontalMargin = 20,
				fontSize = 20,
				color = YELLOW,
			),
		)
		addComponent(
			TextComponent(
				text = "Throw - [SPACE]",
				verticalOrientation = verticalOrientation.BOTTOM,
				horizontalOrientation = horizontalOrientation.LEFT,
				verticalMargin = 25,
				horizontalMargin = 20,
				fontSize = 20,
				color = YELLOW,
			),
		)
	}
}
