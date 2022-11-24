package dev.supermic.nanosense.gui.hudgui.elements.misc

import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.gui.hudgui.LabelHud
import dev.supermic.nanosense.util.TpsCalculator
import dev.supermic.nanosense.util.collections.CircularArray
import dev.supermic.nanosense.util.collections.CircularArray.Companion.average

internal object TPS : LabelHud(
    name = "TPS",
    category = Category.MISC,
    description = "Server TPS"
) {

    // Buffered TPS readings to add some fluidity to the TPS HUD element
    private val tpsBuffer = CircularArray(120, 20.0f)

    override fun SafeClientEvent.updateText() {
        tpsBuffer.add(TpsCalculator.tickRate)

        displayText.add("%.2f".format(tpsBuffer.average()), primaryColor)
        displayText.add("tps", secondaryColor)
    }

}