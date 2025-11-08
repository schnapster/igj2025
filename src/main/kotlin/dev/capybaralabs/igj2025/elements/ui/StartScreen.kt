package dev.capybaralabs.igj2025.elements.ui

import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.elements.TextComponent
import dev.capybaralabs.igj2025.elements.horizontalOrientation
import dev.capybaralabs.igj2025.elements.verticalOrientation

class StartScreen() : Entity() {
	init {
		addComponent(
			TextComponent(
				text = "[ESC]",
				verticalOrientation = verticalOrientation.BOTTOM,
				horizontalOrientation = horizontalOrientation.LEFT,
				verticalMargin = getScreenHeight() / 5,
				horizontalMargin = getScreenWidth() / 4 + 50,
				fontSize = 25,
				color = BROWN,
			),
		)

		addComponent(
			TextComponent(
				text = "[ENTER]",
				verticalOrientation = verticalOrientation.BOTTOM,
				horizontalOrientation = horizontalOrientation.RIGHT,
				verticalMargin = getScreenHeight() / 4 + 55,
				horizontalMargin = getScreenWidth() / 5,
				fontSize = 25,
				color = BROWN,
			),
		)
	}
}
