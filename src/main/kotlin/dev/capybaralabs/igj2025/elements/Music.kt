package dev.capybaralabs.igj2025.elements

import com.raylib.Music
import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

class MusicComponent(
	name: String,
) : Component {

	val music: Music = loadMusicStream("assets/audio/$name")

	init {
		playMusicStream(music)
	}
}

class MusicSystem : System {
	override fun update(dt: Float, entity: Entity) {

		val music = entity.findComponent(MusicComponent::class)
		if (music == null) {
			return
		}
		updateMusicStream(music.music)
	}

	// crashes the JVM when run from the IDE, accept the leak for now
//	override fun close(entity: Entity) {
//		val music = entity.findComponent(MusicComponent::class)?.music
//		if (music == null) {
//			return
//		}
//		try {
//			unloadMusicStream(music)
//		} catch (t: Throwable) {
//			//ignore
//		}
//	}
}
