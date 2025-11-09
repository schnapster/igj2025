package dev.capybaralabs.igj2025.elements

import com.raylib.Raylib.*
import dev.capybaralabs.igj2025.ecs.Component
import dev.capybaralabs.igj2025.ecs.Entity
import dev.capybaralabs.igj2025.ecs.System

// Component to mark entities as frozen (cannot move)
object FrozenMovementComponent : Component

// Component to mark when SLOWMO mode is active (book throws at half speed)
object SlowmoActiveComponent : Component

// Component for temporary text that disappears after a duration
class TemporaryTextComponent(
    var duration: Float = 5f,
    var timeRemaining: Float = duration,
    var isActive: Boolean = false
) : Component

// Entity for displaying mode change notifications
class ModeNotificationEntity() : Entity() {
    init {
        addComponent(
            TextComponent(
                text = "",
                verticalOrientation = verticalOrientation.CENTER,
                horizontalOrientation = horizontalOrientation.CENTER,
                verticalMargin = -100, // Slightly above center
                horizontalMargin = 0,
                fontSize = 60,
                color = YELLOW
            )
        )
        addComponent(TemporaryTextComponent(5f, 0f, false))
    }

    fun showNotification(text: String, duration: Float = 5f) {
        val textComponent = findComponent(TextComponent::class)
        val tempComponent = findComponent(TemporaryTextComponent::class)

        if (textComponent != null && tempComponent != null) {
            textComponent.text = text
			textComponent.color = RED
            // Rough centering based on text length
            textComponent.horizontalMargin = -(text.length * 15)
            tempComponent.duration = duration
            tempComponent.timeRemaining = duration
            tempComponent.isActive = true
		}
    }
}

// System to handle temporary text removal
class TemporaryTextSystem : System {
    override fun update(dt: Float, entities: Set<Entity>) {
        val tempTextEntities = entities.filter { it.hasComponent(TemporaryTextComponent::class) }

        tempTextEntities.forEach { entity ->
            val tempText = entity.findComponent(TemporaryTextComponent::class) ?: return@forEach

            if (tempText.isActive) {
                tempText.timeRemaining -= dt

                // Hide the text when time expires
                if (tempText.timeRemaining <= 0) {
                    tempText.isActive = false
                    // Clear the text
                    entity.findComponent(TextComponent::class)?.let {
                        it.text = ""
                    }
                }
            }
        }
    }
}

// System to detect mode changes and apply effects
class ModeChangeHandlerSystem : System {
    private var previousMode: Mode = Mode.DEFAULT
    private val SPEED_INCREASE = 50f // Amount to increase enemy speed each time

    override fun update(dt: Float, entities: Set<Entity>) {
        // Find the BookUI entity to check current mode
        val bookUI = entities.filterIsInstance<BookUI>().firstOrNull() ?: return
        val bookState = bookUI.findComponent(BookStateComponent::class) ?: return
        val currentMode = bookState.currentMode

        // Check if mode has changed
        if (currentMode != previousMode) {
            println("Mode changed from $previousMode to $currentMode")

            // Handle mode exit effects
            when (previousMode) {
                Mode.FREEZE -> handleFreezeExit(entities)
                Mode.SLOWMO -> handleSlowmoExit(entities)
                else -> {}
            }

            // Handle mode entry effects
            when (currentMode) {
                Mode.SPEED -> handleSpeedActivation(entities)
                Mode.FREEZE -> handleFreezeActivation(entities)
                Mode.SLOWMO -> handleSlowmoActivation(entities)
                else -> {}
            }

            previousMode = currentMode
        }
    }

    private fun handleSpeedActivation(entities: Set<Entity>) {
        println("Speed mode activated! Increasing enemy speed.")

        // Increase speed for all enemies
        val enemies = entities.filterIsInstance<EnemyEntity>()
        enemies.forEach { enemy ->
            val speedComponent = enemy.findComponent(SpeedComponent::class)
            if (speedComponent != null) {
                val oldSpeed = speedComponent.speed
                speedComponent.speed += SPEED_INCREASE
                println("Enemy speed increased from $oldSpeed to ${speedComponent.speed}")
            }
        }

        // Show notification
        val notificationEntity = entities.filterIsInstance<ModeNotificationEntity>().firstOrNull()
        notificationEntity?.showNotification("WIZARD SPEED INCREASED", 3f)
    }

    private fun handleFreezeActivation(entities: Set<Entity>) {
        println("Freeze mode activated! Disabling cat movement.")

        // Freeze all cats
        val cats = entities.filterIsInstance<CatEntity>()
        cats.forEach { cat ->
            cat.addComponent(FrozenMovementComponent)
        }

        // Show notification
        val notificationEntity = entities.filterIsInstance<ModeNotificationEntity>().firstOrNull()
        notificationEntity?.showNotification("FREEZE!", 5f)
    }

    private fun handleFreezeExit(entities: Set<Entity>) {
        println("Freeze mode deactivated! Re-enabling cat movement.")

        // Unfreeze all cats
        val cats = entities.filterIsInstance<CatEntity>()
        cats.forEach { cat ->
            cat.removeComponent(FrozenMovementComponent)
        }
    }

    private fun handleSlowmoActivation(entities: Set<Entity>) {
        println("Slowmo mode activated! Reducing book throw speed.")

        // Add slowmo component to the book entity
        val book = entities.filterIsInstance<BookEntity>().firstOrNull()
        book?.addComponent(SlowmoActiveComponent)

        // Show notification
        val notificationEntity = entities.filterIsInstance<ModeNotificationEntity>().firstOrNull()
        notificationEntity?.showNotification("BOOK MOVES IN SLOWMO!", 5f)
    }

    private fun handleSlowmoExit(entities: Set<Entity>) {
        println("Slowmo mode deactivated! Restoring normal book throw speed.")

        // Remove slowmo component from the book entity
        val book = entities.filterIsInstance<BookEntity>().firstOrNull()
        book?.removeComponent(SlowmoActiveComponent)
    }
}
