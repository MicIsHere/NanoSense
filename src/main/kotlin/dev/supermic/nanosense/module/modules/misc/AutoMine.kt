package dev.supermic.nanosense.module.modules.misc

import dev.supermic.nanosense.command.CommandManager
import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.event.events.ConnectionEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.events.baritone.BaritoneCommandEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.BaritoneUtils
import dev.supermic.nanosense.util.accessor.sendClickBlockToController
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.text.formatValue
import dev.supermic.nanosense.util.threads.runSafe

internal object AutoMine : Module(
    name = "AutoMine",
    description = "Automatically mines chosen ores",
    category = Category.MISC
) {

    private val manual by setting("Manual", false)
    private val iron = setting("Iron", false)
    private val diamond = setting("Diamond", true)
    private val gold = setting("Gold", false)
    private val coal = setting("Coal", false)
    private val log = setting("Logs", false)

    init {
        onEnable {
            runSafe {
                run()
            } ?: disable()
        }

        onDisable {
            BaritoneUtils.cancelEverything()
        }
    }

    private fun SafeClientEvent.run() {
        if (isDisabled || manual) return

        val blocks = ArrayList<String>()

        if (iron.value) blocks.add("iron_ore")
        if (diamond.value) blocks.add("diamond_ore")
        if (gold.value) blocks.add("gold_ore")
        if (coal.value) blocks.add("coal_ore")
        if (log.value) {
            blocks.add("log")
            blocks.add("log2")
        }

        if (blocks.isEmpty()) {
            MessageSendUtils.sendBaritoneMessage("Error: you have to choose at least one thing to mine. " +
                "To mine custom blocks run the ${formatValue("${CommandManager.prefix}b mine block")} command")
            BaritoneUtils.cancelEverything()
            return
        }

        MessageSendUtils.sendBaritoneCommand("mine", *blocks.toTypedArray())
    }

    init {
        safeListener<TickEvent.Pre> {
            if (manual) {
                mc.sendClickBlockToController(true)
            }
        }

        listener<ConnectionEvent.Disconnect> {
            disable()
        }

        listener<BaritoneCommandEvent> {
            if (it.command.contains("cancel")) {
                disable()
            }
        }

        iron.listeners.add { runSafe { run() } }
        diamond.listeners.add { runSafe { run() } }
        gold.listeners.add { runSafe { run() } }
        coal.listeners.add { runSafe { run() } }
        log.listeners.add { runSafe { run() } }
    }
}
