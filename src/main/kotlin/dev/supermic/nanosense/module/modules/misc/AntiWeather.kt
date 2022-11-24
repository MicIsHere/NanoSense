package dev.supermic.nanosense.module.modules.misc

import dev.supermic.nanosense.mixins.core.world.MixinWorld
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module

/**
 * @see MixinWorld.getThunderStrengthHead
 * @see MixinWorld.getRainStrengthHead
 */
internal object AntiWeather : Module(
    name = "AntiWeather",
    description = "Removes rain and thunder from your world",
    category = Category.MISC
)
