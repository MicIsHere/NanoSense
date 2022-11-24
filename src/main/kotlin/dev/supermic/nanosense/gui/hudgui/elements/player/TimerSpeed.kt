package dev.supermic.nanosense.gui.hudgui.elements.player

import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.gui.hudgui.LabelHud
import dev.supermic.nanosense.manager.managers.TimerManager

internal object TimerSpeed : LabelHud(
    name = "TimerSpeed",
    category = Category.PLAYER,
    description = "Client side timer speed"
) {
    override fun SafeClientEvent.updateText() {
        displayText.add("%.2f".format(50.0f / TimerManager.tickLength), primaryColor)
        displayText.add("x", secondaryColor)
    }
}