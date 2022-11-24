package dev.supermic.nanosense.module.modules.misc

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.interfaces.DisplayEnum
import dev.supermic.nanosense.util.threads.defaultScope
import dev.supermic.nanosense.util.threads.onMainThreadSafeSuspend
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketKeepAlive

internal object PingSpoof : Module(
    name = "PingSpoof",
    category = Category.MISC,
    description = "Cancels or adds delay to your ping packets"
) {
    private val mode by setting("Mode", Mode.NORMAL)
    private val delay by setting("Delay", 100, 0..1000, 5)
    private val multiplier by setting("Multiplier", 1, 1..100, 1)

    private enum class Mode(override val displayName: CharSequence) : DisplayEnum {
        NORMAL("Normal"),
        CC("CC")
    }

    private val packetTimer = TickTimer()

    override fun getHudInfo(): String {
        return (delay * multiplier).toString()
    }

    init {
        onDisable {
            packetTimer.reset(-114514L)
        }

        listener<PacketEvent.Receive> {
            when (it.packet) {
                is SPacketKeepAlive -> {
                    packetTimer.reset()
                    it.cancel()
                    defaultScope.launch {
                        delay((delay * multiplier).toLong())
                        onMainThreadSafeSuspend {
                            connection.sendPacket(CPacketKeepAlive(it.packet.id))
                        }
                    }
                }
                is SPacketConfirmTransaction -> {
                    if (mode == Mode.CC && it.packet.windowId == 0 && !it.packet.wasAccepted() && !packetTimer.tickAndReset(1L)) {
                        packetTimer.reset(-114514L)
                        it.cancel()
                        defaultScope.launch {
                            delay((delay * multiplier).toLong())
                            onMainThreadSafeSuspend {
                                connection.sendPacket(CPacketConfirmTransaction(it.packet.windowId, it.packet.actionNumber, true))
                            }
                        }
                    }
                }
            }
        }
    }
}
