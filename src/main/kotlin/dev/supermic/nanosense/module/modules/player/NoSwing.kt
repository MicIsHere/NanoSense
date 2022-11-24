package dev.supermic.nanosense.module.modules.player

import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.events.RunGameLoopEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.SwingMode
import net.minecraft.network.play.client.CPacketAnimation

internal object NoSwing : Module(
    name = "NoSwing",
    category = Category.PLAYER,
    description = "Cancels server or client swing animation"
) {
    private val mode by setting("Mode", SwingMode.CLIENT)

    init {
        listener<PacketEvent.Send> {
            if (mode == SwingMode.PACKET && it.packet is CPacketAnimation) it.cancel()
        }

        safeListener<RunGameLoopEvent.Render> {
            player.isSwingInProgress = false
            player.swingProgressInt = 0
            player.swingProgress = 0.0f
            player.prevSwingProgress = 0.0f
        }
    }
}