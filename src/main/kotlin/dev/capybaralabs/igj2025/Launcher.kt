package dev.capybaralabs.igj2025

import com.raylib.Raylib.*
import com.raylib.Raylib.KeyboardKey.*

fun main() {
	initWindow(1200, 900, "Henlo!")
	setExitKey(KEY_ESCAPE)
	setTargetFPS(144)
	initAudioDevice()

	while (!windowShouldClose()) {
		beginDrawing()

		val text = "Henlo!"
		val fontSize = 100
		val textWidth = measureText(text, fontSize)
		val x = (getScreenWidth() - textWidth) / 2
		val y = (getScreenHeight() / 3)
		drawText(text, x, y, fontSize, RAYWHITE)

		endDrawing()
	}

	closeAudioDevice()
	closeWindow()
}

