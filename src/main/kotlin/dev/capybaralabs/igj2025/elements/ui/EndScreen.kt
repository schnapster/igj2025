package dev.capybaralabs.igj2025.elements.ui

import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.elements.TextComponent
import dev.capybaralabs.igj2025.elements.horizontalOrientation
import dev.capybaralabs.igj2025.elements.verticalOrientation

class EndScreen() : Entity() {
	init {
		addComponent(
			TextComponent(
				text = "TRY AGAIN - [ENTER]",
				verticalOrientation = verticalOrientation.TOP,
				horizontalOrientation = horizontalOrientation.LEFT,
				verticalMargin = getScreenHeight() / 4 + 40,
				horizontalMargin = getScreenWidth() / 9,
				fontSize = 20,
				color = BLACK,
			),
		)

		addComponent(
			TextComponent(
				text = "[ESC]",
				verticalOrientation = verticalOrientation.TOP,
				horizontalOrientation = horizontalOrientation.RIGHT,
				verticalMargin = getScreenHeight() / 7,
				horizontalMargin = getScreenWidth() / 4 - 40,
				fontSize = 15,
				color = BLACK,
			),
		)
	}
}
