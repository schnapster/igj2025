package dev.capybaralabs.igj2025.elements

import com.raylib.Color
import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

enum class verticalOrientation {
	TOP, BOTTOM, CENTER
}

enum class horizontalOrientation {
	LEFT, RIGHT, CENTER
}

class TextComponent(
	var text: String = "",
	var verticalOrientation: verticalOrientation,
	var horizontalOrientation: horizontalOrientation,
	var verticalMargin: Int,
	var horizontalMargin: Int,
	var fontSize: Int,
	var color: Color,
) : Component

class TextUiSystem() : System {

	override fun render(entity: Entity) {
		val tC = entity.findComponent(TextComponent::class) ?: return

		var posX = 0;
		var posY = 0;

		if (tC.verticalOrientation == verticalOrientation.BOTTOM) {
			posY = getScreenHeight() - (tC.verticalMargin ?: 0)
		}
		if (tC.verticalOrientation == verticalOrientation.TOP) {
			posY = tC.verticalMargin ?: 0
		}
		if (tC.verticalOrientation == verticalOrientation.CENTER) {
			posY = getScreenHeight() / 2 + tC.horizontalMargin
		}

		if (tC.horizontalOrientation == horizontalOrientation.LEFT) {
			posX = 0 + (tC.horizontalMargin ?: 0)
		}
		if (tC.horizontalOrientation == horizontalOrientation.RIGHT) {
			posX = getScreenWidth() - (tC.horizontalMargin ?: 0)
		}
		if (tC.horizontalOrientation == horizontalOrientation.CENTER) {
			posX = getScreenWidth() / 2 + tC.horizontalMargin
		}

		drawText(tC.text, posX, posY, tC.fontSize ?: 50, tC.color ?: WHITE)
	}
}
