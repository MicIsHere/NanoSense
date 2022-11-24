package dev.supermic.nanosense.module.modules.movement

import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import net.minecraft.init.MobEffects

internal object AntiLevitation : Module(
    name = "AntiLevitation",
    description = "Removes levitation potion effect",
    category = Category.MOVEMENT
) {
    init {
        safeListener<TickEvent.Pre> {
            player.removeActivePotionEffect(MobEffects.LEVITATION)
        }
    }
}