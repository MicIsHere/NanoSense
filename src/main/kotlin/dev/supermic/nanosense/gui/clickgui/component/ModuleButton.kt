package dev.supermic.nanosense.gui.clickgui.component

import dev.supermic.nanosense.gui.clickgui.NanoClickGui
import dev.supermic.nanosense.gui.rgui.component.BooleanSlider
import dev.supermic.nanosense.module.AbstractModule
import dev.supermic.nanosense.util.math.vector.Vec2f

class ModuleButton(val module: AbstractModule) : BooleanSlider(module.name, module.description) {
    override val progress: Float
        get() = if (module.isEnabled) 1.0f else 0.0f

    override fun onClick(mousePos: Vec2f, buttonId: Int) {
        super.onClick(mousePos, buttonId)
        if (buttonId == 0) module.toggle()
    }

    override fun onRelease(mousePos: Vec2f, buttonId: Int) {
        super.onRelease(mousePos, buttonId)
        if (buttonId == 1) NanoClickGui.displaySettingWindow(module)
    }
}