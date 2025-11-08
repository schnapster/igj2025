package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import com.raylib.Rectangle
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

class BookCollectionSystem : System {

	override fun update(dt: Float, entities: Set<Entity>) {
		val cats = entities.filterIsInstance<CatEntity>()
		val books = entities.filterIsInstance<BookEntity>()

		for (book in books) {
			// Skip if book is already attached to any cat or not on ground
			if (book.attachedToCat != null || !book.onGround) continue

			for (cat in cats) {
				// Check if sprites overlap
				if (checkSpriteOverlap(cat, book)) {
					// Attach book to this cat
					book.attachedToCat = cat
					book.onGround = false

					// Remove movement components so book stops moving
					book.removeAllComponentsOfType(SpeedComponent::class)
					book.removeAllComponentsOfType(DirectionComponent::class)
					book.removeAllComponentsOfType(GravityAffectedComponent::class)
					book.removeAllComponentsOfType(RotatingComponent::class)
					book.removeAllComponentsOfType(ThrownComponent::class)

					println("Cat collected book!")
				}
			}
		}
	}

	private fun checkSpriteOverlap(cat: CatEntity, book: BookEntity): Boolean {
		// Get sprite rectangles for both entities
		val catRect = getSpriteRect(cat)
		val bookRect = getSpriteRect(book)

		// Return true if rectangles overlap
		return catRect != null && bookRect != null && checkCollisionRecs(catRect, bookRect)
	}

	private fun getSpriteRect(entity: Entity): Rectangle? {
		// Get required components
		val position = entity.findComponent(PositionComponent::class)?.position ?: return null
		val texture = entity.findComponent(TextureComponent::class)?.texture ?: return null
		val scale = entity.findComponent(ScaleComponent::class)?.scale ?: 1.0

		// Calculate sprite dimensions with scale
		val width = texture.width * scale
		val height = texture.height * scale

		// Create rectangle centered on position (same as RelationalTextureRenderSystem)
		return krectangle(
			x = position.x - width / 2,
			y = position.y - height / 2,
			width = width,
			height = height,
		)
	}
}
