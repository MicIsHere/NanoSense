package dev.supermic.nanosense.module.modules.combat

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.text.MessageSendUtils.sendServerMessage
import dev.supermic.nanosense.util.threads.runSafe

internal object AutoKys : Module(
    name = "AutoKys",
    description = "Do /kill",
    category = Category.COMBAT
) {
    init {
        onEnable {
            runSafe {
                MessageSendUtils.sendServerMessage("/kill")
            }
            disable()
        }
    }
}
