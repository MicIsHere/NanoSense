package dev.supermic.nanosense.gui.hudgui.elements.player

import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.gui.hudgui.LabelHud
import dev.supermic.nanosense.util.math.MathUtils
import dev.supermic.nanosense.util.math.RotationUtils

internal object Rotation : LabelHud(
    name = "Rotation",
    category = Category.PLAYER,
    description = "Player rotation"
) {

    override fun SafeClientEvent.updateText() {
        val yaw = MathUtils.round(RotationUtils.normalizeAngle(mc.player?.rotationYaw ?: 0.0f), 1)
        val pitch = MathUtils.round(mc.player?.rotationPitch ?: 0.0f, 1)

        displayText.add("Yaw", secondaryColor)
        displayText.add(yaw.toString(), primaryColor)
        displayText.add("Pitch", secondaryColor)
        displayText.add(pitch.toString(), primaryColor)
    }

}