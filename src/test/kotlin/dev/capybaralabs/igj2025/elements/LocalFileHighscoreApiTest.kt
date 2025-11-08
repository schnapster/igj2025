package dev.capybaralabs.igj2025.elements

import java.io.File
import java.time.Instant
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

// ai generated lol
class LocalFileHighscoreApiTest {

	@TempDir
	lateinit var tempDir: File

	private lateinit var api: LocalFileHighscoreApi
	private lateinit var dbPath: String

	@BeforeEach
	fun setup() {
		dbPath = File(tempDir, "test_highscores.db").absolutePath
		api = LocalFileHighscoreApi(dbPath)
	}

	@AfterEach
	fun cleanup() {
		api.close()
	}

	@Test
	fun `should start with empty highscores`() {
		val highscores = api.highscores()
		assertTrue(highscores.isEmpty())
	}

	@Test
	fun `should add and retrieve single highscore`() {
		val highscore = Highscore(
			score = 100.5f,
			name = "TestPlayer",
			ts = Instant.now(),
		)

		api.addHighscore(highscore)

		val highscores = api.highscores()
		assertEquals(1, highscores.size)
		assertEquals(highscore.score, highscores[0].score)
		assertEquals(highscore.name, highscores[0].name)
		assertEquals(highscore.ts.epochSecond, highscores[0].ts.epochSecond)
	}

	@Test
	fun `should add multiple highscores`() {
		val highscore1 = Highscore(100.5f, "Player1", Instant.now())
		val highscore2 = Highscore(200.0f, "Player2", Instant.now())
		val highscore3 = Highscore(150.25f, "Player3", Instant.now())

		api.addHighscore(highscore1)
		api.addHighscore(highscore2)
		api.addHighscore(highscore3)

		val highscores = api.highscores()
		assertEquals(3, highscores.size)
	}

	@Test
	fun `should return highscores sorted by score descending`() {
		val highscore1 = Highscore(100.5f, "Player1", Instant.now())
		val highscore2 = Highscore(200.0f, "Player2", Instant.now())
		val highscore3 = Highscore(150.25f, "Player3", Instant.now())

		api.addHighscore(highscore1)
		api.addHighscore(highscore2)
		api.addHighscore(highscore3)

		val highscores = api.highscores()
		assertEquals(200.0f, highscores[0].score)
		assertEquals(150.25f, highscores[1].score)
		assertEquals(100.5f, highscores[2].score)
	}

	@Test
	fun `should persist data across connections`() {
		val highscore = Highscore(100.5f, "TestPlayer", Instant.now())
		api.addHighscore(highscore)
		api.close()

		// Create new connection to same database
		val api2 = LocalFileHighscoreApi(dbPath)
		val highscores = api2.highscores()

		assertEquals(1, highscores.size)
		assertEquals(highscore.score, highscores[0].score)
		assertEquals(highscore.name, highscores[0].name)

		api2.close()
	}

	@Test
	fun `should handle corrupted database by creating backup`() {
		// Create a corrupted database file
		val dbFile = File(dbPath)
		dbFile.writeText("This is not a valid SQLite database")

		// Try to initialize API with corrupted database
		val corruptedApi = LocalFileHighscoreApi(dbPath)

		// Should successfully create new database
		val highscores = corruptedApi.highscores()
		assertTrue(highscores.isEmpty())

		// Check that backup was created
		val backupFiles = dbFile.parentFile.listFiles { _, name ->
			name.startsWith("test_highscores.db.corrupted_")
		}
		assertTrue(backupFiles != null && backupFiles.isNotEmpty(), "Backup file should exist")

		corruptedApi.close()
	}
}
