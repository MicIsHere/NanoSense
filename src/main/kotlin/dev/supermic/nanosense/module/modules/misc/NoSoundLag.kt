package dev.supermic.nanosense.module.modules.misc

import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import net.minecraft.init.SoundEvents
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.SoundCategory

internal object NoSoundLag : Module(
    name = "NoSoundLag",
    category = Category.MISC,
    description = "Prevents lag caused by sound machines"
) {
    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketSoundEffect) return@listener
            if (it.packet.category == SoundCategory.PLAYERS
                && (it.packet.sound === SoundEvents.ITEM_ARMOR_EQUIP_GENERIC
                    || it.packet.sound === SoundEvents.ITEM_SHIELD_BLOCK)) {
                it.cancel()
            }
        }
    }
}