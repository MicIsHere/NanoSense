package dev.supermic.nanosense.module.modules.chat

import dev.supermic.nanosense.mixins.core.player.MixinEntityPlayerSP
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module

/**
 * @see MixinEntityPlayerSP
 */
internal object PortalChat : Module(
    name = "PortalChat",
    category = Category.CHAT,
    description = "Allows you to open GUIs in portals",
    visible = false
)
