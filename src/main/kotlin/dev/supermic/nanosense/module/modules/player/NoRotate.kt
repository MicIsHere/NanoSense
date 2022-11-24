package dev.supermic.nanosense.module.modules.player

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module

internal object NoRotate : Module(
    name = "NoRotate",
    alias = arrayOf("AntiForceLook"),
    category = Category.PLAYER,
    description = "Stops server packets from turning your head"
)