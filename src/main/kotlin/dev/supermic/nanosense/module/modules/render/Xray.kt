package dev.supermic.nanosense.module.modules.render

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.setting.settings.impl.collection.CollectionSetting
import dev.supermic.nanosense.util.BOOLEAN_SUPPLIER_FALSE
import dev.supermic.nanosense.util.threads.onMainThread
import net.minecraft.block.state.IBlockState

internal object Xray : Module(
    name = "Xray",
    description = "Lets you see through blocks",
    category = Category.RENDER
) {
    private val defaultVisibleList = linkedSetOf("minecraft:diamond_ore", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:portal", "minecraft:cobblestone")

    val blockList = setting(CollectionSetting("Visible List", defaultVisibleList, BOOLEAN_SUPPLIER_FALSE))

    @JvmStatic
    fun shouldReplace(state: IBlockState): Boolean {
        return isEnabled && !blockList.contains(state.block.registryName.toString())
    }

    init {
        onToggle {
            onMainThread {
                mc.renderGlobal?.loadRenderers()
            }
        }

        blockList.editListeners.add {
            onMainThread {
                mc.renderGlobal?.loadRenderers()
            }
        }
    }
}
