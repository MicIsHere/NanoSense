package dev.supermic.nanosense.module.modules.client

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module

internal object Tooltips : Module(
    name = "Tooltips",
    description = "Displays handy module descriptions in the GUI",
    category = Category.CLIENT,
    visible = false,
    enabledByDefault = true
)
