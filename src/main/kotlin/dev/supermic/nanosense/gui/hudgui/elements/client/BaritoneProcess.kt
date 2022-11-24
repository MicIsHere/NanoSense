package dev.supermic.nanosense.gui.hudgui.elements.client

import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.gui.hudgui.LabelHud
import dev.supermic.nanosense.module.modules.movement.AutoWalk
import dev.supermic.nanosense.process.PauseProcess
import dev.supermic.nanosense.util.BaritoneUtils

internal object BaritoneProcess : LabelHud(
    name = "BaritoneProcess",
    category = Category.CLIENT,
    description = "Shows what Baritone is doing"
) {

    override fun SafeClientEvent.updateText() {
        val process = BaritoneUtils.primary?.pathingControlManager?.mostRecentInControl()?.orElse(null) ?: return

        when {
            process == PauseProcess -> {
                displayText.addLine(process.displayName0())
            }
            AutoWalk.baritoneWalk -> {
                displayText.addLine("AutoWalk (${AutoWalk.direction.displayName})")
            }
            else -> {
                displayText.addLine("Process: ${process.displayName()}")
            }
        }
    }

}