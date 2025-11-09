package dev.capybaralabs.igj2025.elements

import com.raylib.Color
import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

enum class VerticalOrientation {
	TOP, BOTTOM, CENTER
}

enum class HorizontalOrientation {
	LEFT, RIGHT, CENTER
}

class TextComponent(
	var text: () -> String = { "" },
	var verticalOrientation: VerticalOrientation,
	var horizontalOrientation: HorizontalOrientation,
	var verticalMargin: Int,
	var horizontalMargin: Int,
	var fontSize: Int,
	var color: Color,
) : Component

class TextUiSystem() : System {

	override fun render(entity: Entity) {
		val components = entity.findComponents(TextComponent::class)

		for (tC in components) {
			var posX = 0
			var posY = 0

			if (tC.verticalOrientation == VerticalOrientation.BOTTOM) {
				posY = getScreenHeight() - ((tC.verticalMargin) + tC.fontSize)
			}
			if (tC.verticalOrientation == VerticalOrientation.TOP) {
				posY = tC.verticalMargin
			}
			if (tC.verticalOrientation == VerticalOrientation.CENTER) {
				posY = getScreenHeight() / 2 + tC.horizontalMargin
			}

			if (tC.horizontalOrientation == HorizontalOrientation.LEFT) {
				posX = 0 + (tC.horizontalMargin)
			}
			if (tC.horizontalOrientation == HorizontalOrientation.RIGHT) {
				posX = getScreenWidth() - (tC.horizontalMargin)
			}
			if (tC.horizontalOrientation == HorizontalOrientation.CENTER) {
				posX = getScreenWidth() / 2 + tC.horizontalMargin
			}

			drawText(tC.text.invoke(), posX, posY, tC.fontSize, tC.color)
		}
	}
}
