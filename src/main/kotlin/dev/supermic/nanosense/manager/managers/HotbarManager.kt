package dev.supermic.nanosense.manager.managers

import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.manager.Manager
import dev.supermic.nanosense.util.accessor.currentPlayerItem
import dev.supermic.nanosense.util.inventory.slot.HotbarSlot
import dev.supermic.nanosense.util.text.MessageSendUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.CPacketHeldItemChange

@Suppress("NOTHING_TO_INLINE")
object HotbarManager : Manager() {
    var serverSideHotbar = 0; private set
    var swapTime = 0L; private set

    val EntityPlayerSP.serverSideItem: ItemStack
        get() = inventory.mainInventory[serverSideHotbar]

    init {
        safeListener<PacketEvent.Send>(Int.MIN_VALUE) {
            if (it.cancelled || it.packet !is CPacketHeldItemChange) return@safeListener

            synchronized(playerController) {
                serverSideHotbar = it.packet.slotId
                swapTime = System.currentTimeMillis()
            }
        }
    }

    inline fun SafeClientEvent.spoofHotbar(slot: HotbarSlot, crossinline block: () -> Unit) {
        synchronized(playerController) {
            spoofHotbar(slot)
            block.invoke()
            resetHotbar()
        }
    }

    inline fun SafeClientEvent.spoofHotbarPlus(slot: HotbarSlot, crossinline block: () -> Unit) {
        spoofHotbarPlus(slot)
    }

    inline fun SafeClientEvent.spoofHotbar(slot: Int, crossinline block: () -> Unit) {
        synchronized(playerController) {
            spoofHotbar(slot)
            block.invoke()
            resetHotbar()
        }
    }

    inline fun SafeClientEvent.spoofHotbarBypass(slot: HotbarSlot, crossinline block: () -> Unit) {
        synchronized(playerController) {
            val swap = slot.hotbarSlot != serverSideHotbar
            if (swap) playerController.pickItem(slot.hotbarSlot)
            block.invoke()
            if (swap) playerController.pickItem(slot.hotbarSlot)
        }
    }

    inline fun SafeClientEvent.spoofHotbarBypass(slot: Int, crossinline block: () -> Unit) {
        synchronized(playerController) {
            val swap = slot != serverSideHotbar
            if (swap) playerController.pickItem(slot)
            block.invoke()
            if (swap) playerController.pickItem(slot)
        }
    }

    inline fun SafeClientEvent.spoofHotbar(slot: HotbarSlot) {
        return spoofHotbar(slot.hotbarSlot)
    }

    inline fun SafeClientEvent.spoofHotbar(slot: Int) {
        if (serverSideHotbar != slot) {
            connection.sendPacket(CPacketHeldItemChange(slot))
        }
    }

    inline fun SafeClientEvent.spoofHotbarPlus(slot: HotbarSlot) {
        return spoofHotbarPlus(slot.hotbarSlot)
    }

    inline fun SafeClientEvent.spoofHotbarPlus(slot: Int) {
        if (serverSideHotbar != slot) {
            try{
                if(mc.isCallingFromMinecraftThread){
                    synchronized(mc.playerController){
                        Minecraft.getMinecraft().player.inventory.currentItem = slot
                        Minecraft.getMinecraft().playerController.updateController()
                    }
                }
            }catch(e: Exception){
                MessageSendUtils.sendNoSpamChatMessage("HotbarManager.SafeClientEvent.spoofHotbarPlus.try.isCallingFromMinecraftThread.Exception: $e")
            }
        }
    }

    inline fun SafeClientEvent.resetHotbar() {
        val slot = playerController.currentPlayerItem
        if (serverSideHotbar != slot) {
            spoofHotbar(slot)
        }
    }
}