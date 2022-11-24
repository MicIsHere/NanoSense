package dev.supermic.nanosense.module

import dev.supermic.nanosense.setting.ModuleConfig

internal abstract class Module(
    name: String,
    alias: Array<String> = emptyArray(),
    category: Category,
    description: String,
    modulePriority: Int = -1,
    alwaysListening: Boolean = false,
    visible: Boolean = true,
    alwaysEnabled: Boolean = false,
    enabledByDefault: Boolean = false
) : AbstractModule(
    name,
    alias,
    category,
    description,
    modulePriority,
    alwaysListening,
    visible,
    alwaysEnabled,
    enabledByDefault,
    ModuleConfig
)