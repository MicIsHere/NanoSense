package dev.supermic.nanosense.command.commands

import dev.supermic.nanosense.command.ClientCommand

object FixSoundCommand : ClientCommand(
    name = "fixsound",
    description = "Fix sound device switching"
) {
    init {
        executeSafe {
            mc.soundHandler.onResourceManagerReload(mc.resourceManager)
        }
    }
}