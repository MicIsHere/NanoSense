package dev.supermic.nanosense.module.modules.misc

import com.mojang.authlib.GameProfile
import dev.supermic.nanosense.event.events.ConnectionEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeParallelListener
import dev.supermic.nanosense.manager.managers.WaypointManager
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.EntityUtils.flooredPosition
import dev.supermic.nanosense.util.EntityUtils.isFakeOrSelf
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.math.CoordinateConverter.asString
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.threads.onMainThread
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.util.math.BlockPos

internal object LogoutLogger : Module(
    name = "LogoutLogger",
    category = Category.MISC,
    description = "Logs when a player leaves the game"
) {
    private val saveWaypoint by setting("Save Waypoint", true)
    private val print by setting("Print To Chat", true)

    private val loggedPlayers = LinkedHashMap<GameProfile, BlockPos>()
    private val timer = TickTimer(TimeUnit.SECONDS)

    init {
        listener<ConnectionEvent.Disconnect> {
            onMainThread {
                loggedPlayers.clear()
            }
        }

        safeParallelListener<TickEvent.Post> {
            for (loadedPlayer in world.playerEntities) {
                if (loadedPlayer !is EntityOtherPlayerMP) continue
                if (loadedPlayer.isFakeOrSelf) continue

                val info = connection.getPlayerInfo(loadedPlayer.gameProfile.id) ?: continue
                loggedPlayers[info.gameProfile] = loadedPlayer.flooredPosition
            }

            if (timer.tickAndReset(1L)) {
                loggedPlayers.entries.removeIf { (profile, pos) ->
                    @Suppress("SENSELESS_COMPARISON")
                    if (connection.getPlayerInfo(profile.id) == null) {
                        if (saveWaypoint) WaypointManager.add(pos, "${profile.name} Logout Spot")
                        if (print) MessageSendUtils.sendNoSpamChatMessage("${profile.name} logged out at ${pos.asString()}")
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }
}