package dev.capybaralabs.igj2025.ecs

import kotlin.reflect.KClass

open class Entity {
	private val components: MutableSet<Component> = mutableSetOf()

	fun addComponent(component: Component) {
		components.add(component)
	}

	fun addComponents(vararg components: Component) {
		this.components.addAll(components)
	}

	fun removeComponent(component: Component) {
		components.remove(component)
	}

	fun <T : Component> removeAllComponentsOfType(type: KClass<T>) {
		val componentsOfType = findComponents(type)
		this.components.removeAll(componentsOfType.toSet())
	}

	fun removeComponents(vararg components: Component) {
		this.components.removeAll(components.toSet())
	}

	fun <T : Component> findComponent(type: KClass<T>): T? {
		return findComponents(type).firstOrNull()
	}

	fun <T : Component> takeComponent(type: KClass<T>): T? {
		return findComponents(type).firstOrNull()
			?.also { removeComponent(it) }
	}


	fun <T : Component> findComponents(type: KClass<T>): List<T> {
		return components.filterIsInstance(type.java)
	}

	fun <T : Component> hasComponent(type: KClass<T>): Boolean {
		return findComponent(type) != null
	}

}
