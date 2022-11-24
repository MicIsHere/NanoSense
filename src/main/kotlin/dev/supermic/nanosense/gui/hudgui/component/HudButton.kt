package dev.supermic.nanosense.gui.hudgui.component

import dev.supermic.nanosense.gui.hudgui.AbstractHudElement
import dev.supermic.nanosense.gui.hudgui.NanoHudGui
import dev.supermic.nanosense.gui.rgui.component.BooleanSlider
import dev.supermic.nanosense.util.math.vector.Vec2f

class HudButton(val hudElement: AbstractHudElement) : BooleanSlider(hudElement.name, hudElement.description) {
    override val progress: Float
        get() = if (hudElement.visible) 1.0f else 0.0f

    override fun onClick(mousePos: Vec2f, buttonId: Int) {
        super.onClick(mousePos, buttonId)
        if (buttonId == 0) hudElement.visible = !hudElement.visible
    }

    override fun onRelease(mousePos: Vec2f, buttonId: Int) {
        super.onRelease(mousePos, buttonId)
        if (buttonId == 1) NanoHudGui.displaySettingWindow(hudElement)
    }
}