package dev.supermic.nanosense.command.commands

import dev.supermic.nanosense.command.ClientCommand
import dev.supermic.nanosense.util.text.MessageSendUtils.sendNoSpamChatMessage
import net.minecraft.util.text.TextFormatting

object ToggleCommand : ClientCommand(
    name = "toggle",
    alias = arrayOf("switch", "t"),
    description = "Toggle a module on and off!"
) {
    init {
        module("module") { moduleArg ->
            execute {
                val module = moduleArg.value
                module.toggle()
                sendNoSpamChatMessage(module.nameAsString +
                    if (module.isEnabled) " ${TextFormatting.GREEN}enabled"
                    else " ${TextFormatting.RED}disabled"
                )
            }
        }
    }
}