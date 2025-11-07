package dev.capybaralabs.igj2025.ecs

import dev.capybaralabs.igj2025.ecs.Component
import kotlin.reflect.KClass

open class Entity {
	private val components: MutableSet<Component> = mutableSetOf()

	fun addComponent(component: Component) {
		components.add(component)
	}

	fun <T : Component> findComponent(type: KClass<T>): T? {
		return findComponents(type).firstOrNull()
	}

	fun <T : Component> findComponents(type: KClass<T>): List<T> {
		return components.filterIsInstance(type.java)
	}

	fun <T : Component> hasComponent(type: KClass<T>): Boolean {
		return findComponent(type) != null
	}

}
