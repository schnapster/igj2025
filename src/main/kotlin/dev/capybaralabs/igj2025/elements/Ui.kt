package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

class FpsUiSystem : System {
	override fun render(entities: Set<Entity>) {
		Raylib.drawFPS(Raylib.getScreenWidth() - 100, 20)
	}
}
