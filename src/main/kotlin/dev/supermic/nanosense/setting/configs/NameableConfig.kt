package dev.supermic.nanosense.setting.configs

import dev.supermic.nanosense.setting.settings.AbstractSetting
import dev.supermic.nanosense.util.extension.rootName
import dev.supermic.nanosense.util.interfaces.Nameable

open class NameableConfig<T : Nameable>(
    name: String,
    filePath: String
) : AbstractConfig<T>(name, filePath) {

    override fun addSettingToConfig(owner: T, setting: AbstractSetting<*>) {
        getGroupOrPut(owner.rootName).addSetting(setting)
    }

    open fun getSettings(nameable: Nameable) = getGroup(nameable.rootName)?.getSettings() ?: emptyList()

}
