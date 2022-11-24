package dev.supermic.nanosense.module.modules.render

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.graphics.AnimationFlag
import dev.supermic.nanosense.util.graphics.Easing
import dev.supermic.nanosense.util.threads.runSafe

internal object GuiAnimation : Module(
    name = "GuiAnimation",
    category = Category.RENDER,
    description = "Animates Minecraft gui",
    visible = false,
    enabledByDefault = true
) {
    private val hotbarAnimation = AnimationFlag(Easing.OUT_CUBIC, 200.0f)

    init {
        onEnable {
            runSafe {
                val currentPos = player.inventory.currentItem * 20.0f
                hotbarAnimation.forceUpdate(currentPos, currentPos)
            }
        }
    }

    @JvmStatic
    fun updateHotbar(): Float {
        val currentPos = mc.player?.let {
            it.inventory.currentItem * 20.0f
        } ?: 0.0f

        return hotbarAnimation.getAndUpdate(currentPos)
    }
}