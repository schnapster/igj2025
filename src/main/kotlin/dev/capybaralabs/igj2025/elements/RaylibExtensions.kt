package dev.capybaralabs.igj2025.elements

import com.raylib.Color
import com.raylib.Rectangle
import com.raylib.Texture
import com.raylib.Vector2

fun kvector2(x: Number, y: Number): Vector2 {
	return Vector2(x.toFloat(), y.toFloat())
}

operator fun Vector2.div(divisor: Number): Vector2 {
	return kvector2(
		this.x / divisor.toFloat(),
		this.y / divisor.toFloat(),
	)
}

operator fun Vector2.times(multiplier: Number): Vector2 {
	return kvector2(
		this.x * multiplier.toFloat(),
		this.y * multiplier.toFloat(),
	)
}

operator fun Vector2.plus(other: Vector2): Vector2 {
	return kvector2(
		this.x + other.x,
		this.y + other.y,
	)
}

operator fun Vector2.minus(other: Vector2): Vector2 {
	return kvector2(
		this.x - other.x,
		this.y - other.y,
	)
}



fun Vector2.copy(): Vector2 = Vector2(x, y)


fun Texture.size(): Vector2 {
	return kvector2(width, height)
}


fun kcolor(r: Number, g: Number, b: Number, a: Number): Color {
	return Color(r.toByte(), g.toByte(), b.toByte(), a.toByte())
}

fun krectangle(position: Vector2, size: Vector2): Rectangle {
	return krectangle(x = position.x, y = position.y, width = size.x, height = size.y)
}

fun krectangle(x: Number, y: Number, width: Number, height: Number): Rectangle {
	return Rectangle(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
}

fun Boolean.toInt() = if (this) 1 else 0
