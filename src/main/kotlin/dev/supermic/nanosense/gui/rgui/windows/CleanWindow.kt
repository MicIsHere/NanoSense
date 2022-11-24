package dev.supermic.nanosense.gui.rgui.windows

import dev.supermic.nanosense.gui.rgui.WindowComponent
import dev.supermic.nanosense.setting.GuiConfig
import dev.supermic.nanosense.setting.configs.AbstractConfig
import dev.supermic.nanosense.util.interfaces.Nameable

/**
 * Window with no rendering
 */
open class CleanWindow(
    name: CharSequence,
    posX: Float,
    posY: Float,
    width: Float,
    height: Float,
    settingGroup: SettingGroup,
    config: AbstractConfig<out Nameable> = GuiConfig
) : WindowComponent(name, posX, posY, width, height, settingGroup, config)