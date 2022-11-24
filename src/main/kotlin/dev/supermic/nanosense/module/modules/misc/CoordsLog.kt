package dev.supermic.nanosense.module.modules.misc

import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.manager.managers.WaypointManager
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.InfoCalculator
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.math.CoordinateConverter.asString
import dev.supermic.nanosense.util.math.vector.toBlockPos
import dev.supermic.nanosense.util.text.MessageSendUtils

internal object CoordsLog : Module(
    name = "CoordsLog",
    description = "Automatically logs your coords, based on actions",
    category = Category.MISC
) {
    private val saveOnDeath = setting("Save On Death", true)
    private val autoLog = setting("Automatically Log", false)
    private val delay = setting("Delay", 15, 1..60, 1)

    private var previousCoord: String? = null
    private var savedDeath = false
    private var timer = TickTimer(TimeUnit.SECONDS)

    init {
        safeListener<TickEvent.Post> {
            if (autoLog.value && timer.tickAndReset(delay.value.toLong())) {
                val currentCoord = player.positionVector.toBlockPos().asString()

                if (currentCoord != previousCoord) {
                    WaypointManager.add("autoLogger")
                    previousCoord = currentCoord
                }
            }

            if (saveOnDeath.value) {
                savedDeath = if (player.isDead || player.health <= 0.0f) {
                    if (!savedDeath) {
                        val deathPoint = WaypointManager.add("Death - " + InfoCalculator.getServerType()).pos
                        MessageSendUtils.sendNoSpamChatMessage("You died at ${deathPoint.x}, ${deathPoint.y}, ${deathPoint.z}")
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

}
