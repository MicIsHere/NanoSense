package dev.supermic.nanosense.gui.hudgui

import dev.supermic.nanosense.gui.rgui.Component
import dev.supermic.nanosense.setting.GuiConfig
import dev.supermic.nanosense.setting.settings.SettingRegister

internal abstract class LabelHud(
    name: String,
    alias: Array<String> = emptyArray(),
    category: Category,
    description: String,
    alwaysListening: Boolean = false,
    enabledByDefault: Boolean = false
) : AbstractLabelHud(name, alias, category, description, alwaysListening, enabledByDefault, GuiConfig),
    SettingRegister<Component> by GuiConfig