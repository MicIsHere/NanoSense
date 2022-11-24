package dev.supermic.nanosense.command.commands

import dev.supermic.nanosense.command.ClientCommand
import dev.supermic.nanosense.module.modules.player.InventoryManager
import dev.supermic.nanosense.util.items.block
import dev.supermic.nanosense.util.items.id
import dev.supermic.nanosense.util.text.MessageSendUtils
import net.minecraft.block.BlockAir

// TODO: Remove once GUI has Block settings
object SetBuildingBlockCommand : ClientCommand(
    name = "setbuildingblock",
    description = "Set the default building block"
) {
    init {
        executeSafe {
            val heldItem = player.inventory.getCurrentItem()
            when {
                heldItem.isEmpty -> {
                    InventoryManager.buildingBlockID = 0
                    MessageSendUtils.sendNoSpamChatMessage("Building block has been reset")
                }
                heldItem.item.block !is BlockAir -> {
                    InventoryManager.buildingBlockID = heldItem.item.id
                    MessageSendUtils.sendNoSpamChatMessage("Building block has been set to ${heldItem.displayName}")
                }
                else -> {
                    MessageSendUtils.sendNoSpamChatMessage("You are not holding a valid block")
                }
            }
        }
    }
}