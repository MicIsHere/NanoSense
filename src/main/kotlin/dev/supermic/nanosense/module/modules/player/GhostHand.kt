package dev.supermic.nanosense.module.modules.player

import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.setting.settings.impl.collection.CollectionSetting
import dev.supermic.nanosense.util.BOOLEAN_SUPPLIER_FALSE
import dev.supermic.nanosense.util.EntityUtils.eyePosition
import dev.supermic.nanosense.util.math.vector.toBlockPos
import dev.supermic.nanosense.util.world.RayTraceAction
import dev.supermic.nanosense.util.world.rayTrace
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

internal object GhostHand : Module(
    name = "GhostHand",
    description = "Ignores interaction with certain blocks",
    category = Category.PLAYER
) {
    private val defaultVisibleList = linkedSetOf("minecraft:bedrock", "minecraft:portal_frame", "minecraft:portal")

    private val ignoreListed by setting("Ignore Listed", true)
    val blockList = setting(CollectionSetting("Block List", defaultVisibleList, BOOLEAN_SUPPLIER_FALSE))

    private val function: (BlockPos, IBlockState) -> RayTraceAction = { _, blockState ->
        val block = blockState.block
        if (block == Blocks.AIR) {
            RayTraceAction.Skip
        } else {
            if (block.canCollideCheck(blockState, false) && ignoreListed != blockList.contains(block.registryName.toString())) {
                RayTraceAction.Calc
            } else {
                RayTraceAction.Skip
            }
        }
    }

    @JvmStatic
    fun handleRayTrace(blockReachDistance: Double, partialTicks: Float, cir: CallbackInfoReturnable<RayTraceResult>) {
        if (isDisabled) return
        if (mc.currentScreen == null) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) return
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) return
            if (Mouse.isButtonDown(1)) return
        }

        mc.player?.let {
            val eyePos = it.eyePosition
            val lookVec = it.getLook(partialTicks)
            val sightEnd = eyePos.add(lookVec.x * blockReachDistance, lookVec.y * blockReachDistance, lookVec.z * blockReachDistance)
            cir.returnValue = it.world.rayTrace(eyePos, sightEnd, 50, function)
                ?: RayTraceResult(RayTraceResult.Type.MISS, sightEnd, EnumFacing.UP, sightEnd.toBlockPos())
        }
    }
}