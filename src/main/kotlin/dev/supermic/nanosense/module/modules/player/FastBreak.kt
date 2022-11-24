package dev.supermic.nanosense.module.modules.player

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.accessor.blockHitDelay
import dev.supermic.nanosense.util.threads.runSafe

internal object FastBreak : Module(
    name = "FastBreak",
    category = Category.PLAYER,
    description = "Breaks block faster and nullifies the break delay"
) {
    private val breakDelay by setting("Break Delay", 0, 0..5, 1)

    @JvmStatic
    fun updateBreakDelay() {
        runSafe {
            if (isEnabled) {
                playerController.blockHitDelay = breakDelay
            }
        }
    }
}