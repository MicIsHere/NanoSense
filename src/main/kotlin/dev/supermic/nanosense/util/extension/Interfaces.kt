package dev.supermic.nanosense.util.extension

import dev.supermic.nanosense.translation.TranslationKey
import dev.supermic.nanosense.util.interfaces.DisplayEnum
import dev.supermic.nanosense.util.interfaces.Nameable

val DisplayEnum.rootName: String
    get() = (displayName as? TranslationKey)?.rootString ?: displayName.toString()

val Nameable.rootName: String
    get() = (name as? TranslationKey)?.rootString ?: name.toString()