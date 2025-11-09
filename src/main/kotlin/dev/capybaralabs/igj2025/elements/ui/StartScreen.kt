package dev.capybaralabs.igj2025.elements.ui

import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.elements.HorizontalOrientation
import dev.capybaralabs.igj2025.elements.TextComponent
import dev.capybaralabs.igj2025.elements.VerticalOrientation

class StartScreen() : Entity() {
	init {
		addComponent(
			TextComponent(
				text = { "[ESC]" },
				verticalOrientation = VerticalOrientation.BOTTOM,
				horizontalOrientation = HorizontalOrientation.LEFT,
				verticalMargin = getScreenHeight() / 5,
				horizontalMargin = getScreenWidth() / 4 + 50,
				fontSize = 25,
				color = BROWN,
			),
		)

		addComponent(
			TextComponent(
				text = { "[ENTER]" },
				verticalOrientation = VerticalOrientation.BOTTOM,
				horizontalOrientation = HorizontalOrientation.RIGHT,
				verticalMargin = getScreenHeight() / 4 + 55,
				horizontalMargin = getScreenWidth() / 5,
				fontSize = 25,
				color = BROWN,
			),
		)
	}
}
