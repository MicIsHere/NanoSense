package dev.supermic.nanosense.module.modules.misc

import dev.supermic.nanosense.event.events.GuiEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.manager.managers.WaypointManager
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.InfoCalculator
import dev.supermic.nanosense.util.math.CoordinateConverter.asString
import dev.supermic.nanosense.util.text.MessageSendUtils
import net.minecraft.client.gui.GuiGameOver

internal object AutoRespawn : Module(
    name = "AutoRespawn",
    description = "Automatically respawn after dying",
    category = Category.MISC
) {
    private val respawn by setting("Respawn", true)
    private val deathCoords by setting("Save Death Coords", true)
    private val antiGlitchScreen by setting("Anti Glitch Screen", true)

    init {
        safeListener<GuiEvent.Displayed> {
            if (it.screen !is GuiGameOver) return@safeListener

            if (deathCoords && player.health <= 0.0f) {
                WaypointManager.add("Death - " + InfoCalculator.getServerType())
                MessageSendUtils.sendChatMessage("You died at ${player.position.asString()}")
            }

            if (respawn || antiGlitchScreen && player.health > 0.0f) {
                player.respawnPlayer()
                it.screen = null
            }
        }
    }
}