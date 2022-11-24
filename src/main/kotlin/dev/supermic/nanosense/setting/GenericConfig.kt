package dev.supermic.nanosense.setting

import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.setting.configs.NameableConfig

internal object GenericConfig : NameableConfig<GenericConfigClass>(
    "generic",
    "${NanoSenseMod.DIRECTORY}/config/"
)