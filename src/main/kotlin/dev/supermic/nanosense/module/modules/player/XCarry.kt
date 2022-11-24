package dev.supermic.nanosense.module.modules.player

import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.accessor.windowID
import dev.supermic.nanosense.util.inventory.slot.craftingSlots
import dev.supermic.nanosense.util.inventory.slot.hasAnyItem
import net.minecraft.network.play.client.CPacketCloseWindow

internal object XCarry : Module(
    name = "XCarry",
    category = Category.PLAYER,
    description = "Store items in crafting slots"
) {
    init {
        safeListener<PacketEvent.Send> {
            if (it.packet is CPacketCloseWindow && it.packet.windowID == 0 && (!player.inventory.itemStack.isEmpty || player.craftingSlots.hasAnyItem())) {
                it.cancel()
            }
        }
    }
}