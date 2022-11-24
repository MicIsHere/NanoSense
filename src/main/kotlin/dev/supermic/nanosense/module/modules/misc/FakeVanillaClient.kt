package dev.supermic.nanosense.module.modules.misc

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module

internal object FakeVanillaClient : Module(
    name = "FakeVanillaClient",
    description = "Fakes a modless client when connecting",
    category = Category.MISC
)
