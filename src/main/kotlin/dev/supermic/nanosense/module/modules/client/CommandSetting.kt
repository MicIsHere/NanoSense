package dev.supermic.nanosense.module.modules.client

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module

internal object CommandSetting : Module(
    name = "CommandSetting",
    category = Category.CLIENT,
    description = "Settings for commands",
    visible = false,
    alwaysEnabled = true
) {
    var prefix by setting("Prefix", ";")
}