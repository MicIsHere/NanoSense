package dev.supermic.nanosense.module.modules.combat

import dev.supermic.nanosense.event.events.ConnectionEvent
import dev.supermic.nanosense.event.events.EntityEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.text.MessageSendUtils.sendServerMessage

internal object AutoKit : Module(
    name = "AutoKit",
    description = "Do /kit automatically",
    category = Category.COMBAT
) {
    private val kitName by setting("Kit Name", "")

    private var shouldSend = false
    private val timer = TickTimer()

    init {
        listener<ConnectionEvent.Connect> {
            shouldSend = true
            timer.reset(3000L)
        }

        safeListener<EntityEvent.Death> {
            if (it.entity == player) {
                shouldSend = true
                timer.reset(1500L)
            }
        }

        safeListener<TickEvent.Post> {
            if (player.isDead) {
                shouldSend = true
            } else if (shouldSend && timer.tick(0)) {
                val name = kitName
                if (name.isNotBlank()) {
                    MessageSendUtils.sendServerMessage("/kit $name")
                }
                shouldSend = false
            }
        }
    }
}
