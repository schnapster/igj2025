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
				text = "Test",
				verticalOrientation = verticalOrientation.CENTER,
				horizontalOrientation = horizontalOrientation.CENTER,
				verticalMargin = 0,
				horizontalMargin = 0,
				fontSize = 50,
				color = YELLOW,
			),
		)
	}
}
