package dev.supermic.nanosense.module.modules.combat

import dev.supermic.nanosense.event.events.InputEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.manager.managers.HotbarManager.spoofHotbar
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.inventory.slot.firstItem
import dev.supermic.nanosense.util.inventory.slot.hotbarSlots
import dev.supermic.nanosense.util.text.MessageSendUtils.sendNoSpamChatMessage
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand
import net.minecraft.util.math.RayTraceResult.Type

internal object MidClickPearl : Module(
    name = "MidClickPearl",
    category = Category.COMBAT,
    description = "Throws a pearl automatically when you middle click in air"
) {
    init {
        safeListener<InputEvent.Mouse> {
            if (it.state || it.button != 2) return@safeListener

            val objectMouseOver = mc.objectMouseOver
            if (objectMouseOver == null || objectMouseOver.typeOfHit != Type.BLOCK) {
                val pearlSlot = player.hotbarSlots.firstItem(Items.ENDER_PEARL)

                if (pearlSlot != null) {
                    spoofHotbar(pearlSlot) {
                        connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                    }
                } else {
                    sendNoSpamChatMessage("No Ender Pearl was found in hotbar!")
                }
            }
        }
    }
}