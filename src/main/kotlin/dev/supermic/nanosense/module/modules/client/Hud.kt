package dev.supermic.nanosense.module.modules.client

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.graphics.color.ColorRGB

internal object Hud : Module(
    name = "Hud",
    description = "Toggles Hud displaying and settings",
    category = Category.CLIENT,
    visible = false,
    enabledByDefault = true
) {
    val primaryColor by setting("Primary Color", ColorRGB(255, 250, 253), false)
    val secondaryColor by setting("Secondary Color", ColorRGB(255, 135, 230), false)
}