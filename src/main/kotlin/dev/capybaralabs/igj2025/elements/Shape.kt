package dev.capybaralabs.igj2025.elements

import com.raylib.Vector2
import dev.capybaralabs.igj2025.ecs.Component

sealed interface ShapeComponent : Component

class RectangleShapeComponent(
	val size: Vector2,
) : ShapeComponent

class CircleShapeComponent(
	val radius: Float,
) : ShapeComponent

