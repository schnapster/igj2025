package dev.capybaralabs.igj2025.elements

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Highscore(
	val score: Float = 0f,
	val name: String,
	val ts: Instant,
)

interface HighscoreApi {
	fun highscores(): List<Highscore>
	fun saveHighscore(highscore: Highscore)
}

// AI Generated Code lol
class LocalFileHighscoreApi(
	private val dbPath: String = getDefaultDatabasePath(),
) : HighscoreApi {

	private val connection: Connection

	init {
		connection = initializeDatabase()
	}

	private fun initializeDatabase(): Connection {
		return try {
			val conn = DriverManager.getConnection("jdbc:sqlite:$dbPath")
			createTableIfNotExists(conn)
			conn
		} catch (e: SQLException) {
			// Database might be corrupted, try to recover
			println("Error initializing database: ${e.message}")
			handleCorruptedDatabase()

			// Try again with fresh database
			val conn = DriverManager.getConnection("jdbc:sqlite:$dbPath")
			createTableIfNotExists(conn)
			conn
		}
	}

	private fun handleCorruptedDatabase() {
		val dbFile = File(dbPath)

		if (dbFile.exists()) {
			// Create backup filename with timestamp
			val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
			val backupPath = "${dbPath}.corrupted_$timestamp"
			val backupFile = File(backupPath)

			// Rename corrupted file to preserve it
			if (dbFile.renameTo(backupFile)) {
				println("Corrupted database backed up to: $backupPath")
			} else {
				println("Warning: Could not backup corrupted database, deleting it")
				dbFile.delete()
			}
		}
	}

	companion object {
		private fun getDefaultDatabasePath(): String {
			val appName = "RAatWT"
			val dbFileName = "highscores.db"

			val dataDir = when {
				// Windows
				System.getProperty("os.name").lowercase().contains("win") -> {
					val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
					File(appData, appName)
				}
				// macOS
				System.getProperty("os.name").lowercase().contains("mac") -> {
					File(System.getProperty("user.home"), "Library/Application Support/$appName")
				}
				// Linux and others
				else -> {
					val xdgDataHome = System.getenv("XDG_DATA_HOME")
					if (xdgDataHome != null) {
						File(xdgDataHome, appName)
					} else {
						File(System.getProperty("user.home"), ".local/share/$appName")
					}
				}
			}

			// Create directory if it doesn't exist
			if (!dataDir.exists()) {
				dataDir.mkdirs()
			}

			return File(dataDir, dbFileName).absolutePath
		}
	}


	private fun createTableIfNotExists(conn: Connection) {
		val sql = """
			CREATE TABLE IF NOT EXISTS highscores (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				score REAL NOT NULL,
				name TEXT NOT NULL,
				ts INTEGER NOT NULL
			)
		""".trimIndent()

		conn.createStatement().use { statement ->
			statement.execute(sql)
		}
	}

	override fun highscores(): List<Highscore> {
		return try {
			val sql = "SELECT score, name, ts FROM highscores ORDER BY score DESC"
			val highscores = mutableListOf<Highscore>()

			connection.createStatement().use { statement ->
				statement.executeQuery(sql).use { resultSet ->
					while (resultSet.next()) {
						val score = resultSet.getFloat("score")
						val name = resultSet.getString("name")
						val tsEpochSeconds = resultSet.getLong("ts")
						val ts = Instant.ofEpochSecond(tsEpochSeconds)

						highscores.add(Highscore(score, name, ts))
					}
				}
			}

			highscores
		} catch (e: SQLException) {
			println("Error reading highscores: ${e.message}")
			println("Database may be corrupted. Returning empty list.")
			emptyList()
		}
	}

	override fun saveHighscore(highscore: Highscore) {
		try {
			val sql = "INSERT INTO highscores (score, name, ts) VALUES (?, ?, ?)"

			connection.prepareStatement(sql).use { statement ->
				statement.setFloat(1, highscore.score)
				statement.setString(2, highscore.name)
				statement.setLong(3, highscore.ts.epochSecond)
				statement.executeUpdate()
			}
		} catch (e: SQLException) {
			println("Error adding highscore: ${e.message}")
			println("Database may be corrupted. Highscore not saved.")
			throw e
		}
	}

	fun close() {
		connection.close()
	}
}


//class RemoteHighscoreApi : HighscoreApi {
//
//}
