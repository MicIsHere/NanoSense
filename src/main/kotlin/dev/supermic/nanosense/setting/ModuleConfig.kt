package dev.supermic.nanosense.setting

import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.module.AbstractModule
import dev.supermic.nanosense.module.modules.client.Configurations
import dev.supermic.nanosense.setting.configs.NameableConfig
import java.io.File

internal object ModuleConfig : NameableConfig<AbstractModule>(
    "modules",
    "${NanoSenseMod.DIRECTORY}/config/modules",
) {
    override val file: File get() = File("$filePath/${Configurations.modulePreset}.json")
    override val backup get() = File("$filePath/${Configurations.modulePreset}.bak")
}