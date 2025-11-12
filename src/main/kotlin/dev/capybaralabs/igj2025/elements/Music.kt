package dev.capybaralabs.igj2025.elements

import com.raylib.Music
import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System
import dev.capybaralabs.igj2025.system.AssetLoader
import dev.capybaralabs.igj2025.system.MusicStreamData

class MusicComponent(
	name: String,
) : Component {

	private val musicData: MusicStreamData = AssetLoader.loadMusicStream("assets/audio/$name")
	val music: Music = musicData.music

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
