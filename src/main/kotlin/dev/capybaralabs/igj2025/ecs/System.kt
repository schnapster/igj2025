package dev.capybaralabs.igj2025.ecs

interface System {
	fun update(dt: Float, entity: Entity) {}
	fun update(dt: Float, entities: Set<Entity>) {
		for (entity in entities) {
			update(dt, entity)
		}
	}

	fun render(entity: Entity) {}
	fun render(entities: Set<Entity>) {
		for (entity in entities) {
			render(entity)
		}
	}

	fun close(entity: Entity) {}
	fun close(entities: Set<Entity>) {
		for (entity in entities) {
			close(entity)
		}
	}
}
