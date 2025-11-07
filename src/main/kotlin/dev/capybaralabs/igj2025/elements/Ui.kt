package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib
import dev.capybaralabs.igj2025.ecs.*

class UiFpsSystem : System {
	override fun render(entities: Set<Entity>) {
		Raylib.drawFPS(Raylib.getScreenWidth() - 100, 20)
	}
}
