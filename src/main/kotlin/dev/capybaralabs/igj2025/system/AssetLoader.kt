package dev.capybaralabs.igj2025.system

import com.raylib.Image
import com.raylib.Raylib.*
import com.raylib.Texture
import java.nio.ByteBuffer

object AssetLoader {

	fun loadTexture(path: String): Texture {
		return loadTextureFromImage(loadImage(path))
	}

//	fun loadMusicStream(path: String): Music {
//		val fileData = loadFileData(path)
//
//		val memory = ByteBuffer.allocateDirect(fileData.size)
//		memory.put(fileData)
//		memory.flip() // lmao
//
//
//		return loadMusicStreamFromMemory(getFileType(path), memory, fileData.size)
//	}

//	fun loadSound(path: String): Sound {
//		val fileData = loadFileData(path)
//
//		val memory = ByteBuffer.allocateDirect(fileData.size)
//		memory.put(fileData)
//		memory.flip() // lmao
//
//
//		val wave = Wave(MemorySegment.ofBuffer(memory))
//
//		return loadSoundFromWave(wave)
//	}


	fun loadImage(path: String): Image {
		val fileData = loadFileData(path)

		val memory = ByteBuffer.allocateDirect(fileData.size)
		memory.put(fileData)
		memory.flip() // lmao


		return loadImageFromMemory(getFileType(path), memory, fileData.size)
	}

	private fun loadFileData(path: String): ByteArray {
		// JVM resource loader expects a leading slash
		val thePath = if (path.startsWith("/")) {
			path
		} else {
			"/$path"
		}
		try {
			val resourceAsStream = AssetLoader::class.java.getResourceAsStream(thePath)
				?: throw RuntimeException("No asset $path found")
			return resourceAsStream.use { it.readBytes() }
		} catch (e: Exception) {
			e.printStackTrace()
			throw e
		}
	}

	private fun getFileType(f: String): String {
		return f.substring(f.lastIndexOf("."), f.length)
	}
}
