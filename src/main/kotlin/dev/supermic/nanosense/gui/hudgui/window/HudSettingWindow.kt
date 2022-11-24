package dev.supermic.nanosense.gui.hudgui.window

import dev.supermic.nanosense.gui.hudgui.AbstractHudElement
import dev.supermic.nanosense.gui.rgui.windows.SettingWindow
import dev.supermic.nanosense.setting.settings.AbstractSetting

class HudSettingWindow(
    hudElement: AbstractHudElement,
    posX: Float,
    posY: Float
) : SettingWindow<AbstractHudElement>(hudElement.name, hudElement, posX, posY, SettingGroup.NONE) {

    override fun getSettingList(): List<AbstractSetting<*>> {
        return element.settingList
    }

}