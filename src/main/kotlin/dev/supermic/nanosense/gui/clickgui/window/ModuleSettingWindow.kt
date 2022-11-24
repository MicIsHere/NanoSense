package dev.supermic.nanosense.gui.clickgui.window

import dev.supermic.nanosense.gui.rgui.windows.SettingWindow
import dev.supermic.nanosense.module.AbstractModule
import dev.supermic.nanosense.setting.settings.AbstractSetting

class ModuleSettingWindow(
    module: AbstractModule,
    posX: Float,
    posY: Float
) : SettingWindow<AbstractModule>(module.name, module, posX, posY, SettingGroup.NONE) {

    override fun getSettingList(): List<AbstractSetting<*>> {
        return element.fullSettingList.filter { it.name != "Enabled" && it.name != "Clicks" }
    }

}