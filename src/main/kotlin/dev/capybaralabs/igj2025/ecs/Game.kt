package dev.capybaralabs.igj2025.ecs

class Game(
	initialEntites: Set<Entity> = setOf(),
	initialSystems: Set<System> = setOf(),
	initialUiSystems: Set<System> = setOf(),
) {

	private val entities: MutableSet<Entity> = initialEntites.toMutableSet()
	private val systems: MutableSet<System> = initialSystems.toMutableSet()
	private val uiSystems: MutableSet<System> = initialUiSystems.toMutableSet()


	fun addEntity(entity: Entity) {
		entities += entity
	}
	fun addEntities(vararg toAdd: Entity) {
		entities += toAdd
	}

	fun removeEntity(entity: Entity) {
		entities -= entity
	}

	fun addSystem(system: System) {
		systems += system
	}

	fun addUiSystem(system: System) {
		uiSystems += system
	}


	fun update(dt: Float) {
		for (system in systems.toSet()) {
			system.update(dt, entities.toSet())
		}
		for (uiSystem in uiSystems.toSet()) {
			uiSystem.update(dt, entities.toSet())
		}
	}

	fun render() {
		for (system in systems.toSet()) {
			system.render(entities.toSet())
		}
		for (uiSystem in uiSystems.toSet()) {
			uiSystem.render(entities.toSet())
		}
	}

	fun close() {
		for (system in systems.toSet()) {
			system.close(entities.toSet())
		}
		for (uiSystem in uiSystems.toSet()) {
			uiSystem.close(entities.toSet())
		}
	}
}
