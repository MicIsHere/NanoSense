package dev.supermic.nanosense.module

import dev.supermic.nanosense.translation.TranslateType
import dev.supermic.nanosense.util.interfaces.DisplayEnum

enum class Category(override val displayName: CharSequence) : DisplayEnum {
    CHAT(TranslateType.COMMON commonKey "Chat"),
    CLIENT(TranslateType.COMMON commonKey "Client"),
    COMBAT(TranslateType.COMMON commonKey "Combat"),
    MISC(TranslateType.COMMON commonKey "Misc"),
    MOVEMENT(TranslateType.COMMON commonKey "Movement"),
    PLAYER(TranslateType.COMMON commonKey "Player"),
    RENDER(TranslateType.COMMON commonKey "Render");

    override fun toString() = displayString
}