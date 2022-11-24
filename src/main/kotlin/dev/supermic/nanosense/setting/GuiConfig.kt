package dev.supermic.nanosense.setting

import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.gui.rgui.Component
import dev.supermic.nanosense.module.modules.client.Configurations
import dev.supermic.nanosense.setting.configs.AbstractConfig
import dev.supermic.nanosense.setting.settings.AbstractSetting
import dev.supermic.nanosense.util.extension.rootName
import java.io.File

internal object GuiConfig : AbstractConfig<Component>(
    "gui",
    "${NanoSenseMod.DIRECTORY}/config/gui"
) {
    override val file get() = File("$filePath/${Configurations.guiPreset}.json")
    override val backup get() = File("$filePath/${Configurations.guiPreset}.bak")

    override fun addSettingToConfig(owner: Component, setting: AbstractSetting<*>) {
        val groupName = owner.settingGroup.groupName
        if (groupName.isNotEmpty()) {
            getGroupOrPut(groupName).getGroupOrPut(owner.rootName).addSetting(setting)
        }
    }
}