package dev.supermic.nanosense.module.modules.combat

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.events.player.OnUpdateWalkingPlayerEvent
import dev.supermic.nanosense.event.events.render.Render3DEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.event.safeParallelListener
import dev.supermic.nanosense.gui.hudgui.elements.client.Notification
import dev.supermic.nanosense.manager.managers.CombatManager
import dev.supermic.nanosense.manager.managers.PlayerPacketManager.sendPlayerPacket
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.Bind
import dev.supermic.nanosense.util.EntityUtils.eyePosition
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.combat.CalcContext
import dev.supermic.nanosense.util.combat.CrystalDamage
import dev.supermic.nanosense.util.combat.CrystalUtils
import dev.supermic.nanosense.util.combat.CrystalUtils.hasValidSpaceForCrystal
import dev.supermic.nanosense.util.graphics.ESPRenderer
import dev.supermic.nanosense.util.graphics.color.ColorRGB
import dev.supermic.nanosense.util.inventory.slot.allSlots
import dev.supermic.nanosense.util.inventory.slot.firstBlock
import dev.supermic.nanosense.util.inventory.slot.hasItem
import dev.supermic.nanosense.util.inventory.slot.hotbarSlots
import dev.supermic.nanosense.util.math.RotationUtils.getRotationTo
import dev.supermic.nanosense.util.math.VectorUtils
import dev.supermic.nanosense.util.math.vector.distanceTo
import dev.supermic.nanosense.util.math.vector.toVec3d
import dev.supermic.nanosense.util.threads.defaultScope
import dev.supermic.nanosense.util.threads.onMainThreadSafeSuspend
import dev.supermic.nanosense.util.threads.runSafe
import dev.supermic.nanosense.util.world.*
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.max

@CombatManager.CombatModule
internal object CrystalBasePlace : Module(
    name = "CrystalBasePlace",
    description = "Places obby for placing crystal on",
    category = Category.COMBAT,
    modulePriority = 90
) {
    private val manualPlaceBind by setting("Bind Manual Place", Bind(), {
        runSafe {
            if (isEnabled && CombatManager.isOnTopPriority(CrystalBasePlace) && !CombatSetting.pause) {
                prePlace(minDamageIncManual)
            }
        }
    })
    private val minDamageIncManual by setting("Min Damage Inc Inactive", 2.0f, 0.0f..20.0f, 0.25f)
    private val minDamageIncInactive by setting("Min Damage Inc Inactive", 4.0f, 0.0f..20.0f, 0.25f)
    private val minDamageIncActive by setting("Min Damage Inc Active", 8.0f, 0.0f..20.0f, 0.25f)
    private val range by setting("Range", 4.0f, 0.0f..8.0f, 0.5f)
    private val delay by setting("Delay", 20, 0..50, 5)

    private val timer = TickTimer(TimeUnit.TICKS)
    private val renderer = ESPRenderer().apply { aFilled = 33; aOutline = 233 }
    private var inactiveTicks = 0
    private var rotationTo: Vec3d? = null

    override fun isActive(): Boolean {
        return isEnabled && inactiveTicks < 4
    }

    override fun getHudInfo(): String {
        return if (inactiveTicks <= 10) {
            "Active"
        } else {
            ""
        }
    }

    init {
        onDisable {
            inactiveTicks = 0
        }

        listener<Render3DEvent> {
            val clear = inactiveTicks >= 30
            renderer.render(clear)
        }

        safeListener<OnUpdateWalkingPlayerEvent.Pre> {
            if (isActive()) {
                rotationTo?.let { hitVec ->
                    sendPlayerPacket {
                        rotate(getRotationTo(hitVec))
                    }
                }
            } else {
                rotationTo = null
            }
        }

        safeParallelListener<TickEvent.Post> {
            inactiveTicks++

            if (CombatManager.isOnTopPriority(CrystalBasePlace)
                && !CombatSetting.pause
                && TrollAura.isEnabled
                && player.allSlots.hasItem(Items.END_CRYSTAL)) {
                prePlace(if (TrollAura.inactiveTicks > 10) minDamageIncInactive else minDamageIncActive)
            }
        }
    }

    private fun SafeClientEvent.prePlace(minDamageInc: Float) {
        if (rotationTo != null || !timer.tick(delay)) return

        val slot = player.hotbarSlots.firstBlock(Blocks.OBSIDIAN) ?: return
        val eyePos = player.eyePosition
        val posList = VectorUtils.getBlockPosInSphere(eyePos, range)
            .filter { hasValidSpaceForCrystal(it) }
            .filter { world.isPlaceable(it) }
            .toList()

        defaultScope.launch {
            val placeInfo = getPlaceInfo(eyePos, posList, minDamageInc)

            if (placeInfo != null) {
                rotationTo = placeInfo.hitVec
                renderer.replaceAll(mutableListOf(ESPRenderer.Info(AxisAlignedBB(placeInfo.placedPos), ColorRGB(255, 255, 255))))
                inactiveTicks = 0
                timer.reset()

                delay(50)
                onMainThreadSafeSuspend {
                    placeBlock(placeInfo, slot)
                    Notification.send(CrystalBasePlace, "$chatName Obsidian placed")
                }
            } else {
                timer.reset(-max(delay - 1, 0) * 50L)
            }
        }
    }

    private fun SafeClientEvent.getPlaceInfo(eyePos: Vec3d, posList: List<BlockPos>, minDamageInc: Float): PlaceInfo? {
        val contextSelf = CombatManager.contextSelf ?: return null
        val contextTarget = CombatManager.contextTarget ?: return null

        val mutableBlockPos = BlockPos.MutableBlockPos()
        val cacheList = ArrayList<CrystalDamage>()
        val maxCurrentDamage = CombatManager.placeMap.entries
            .filter { eyePos.distanceTo(it.key) < range }
            .maxOfOrNull { it.value.targetDamage } ?: 0.0f

        for (pos in posList) {
            // Neighbor blocks check
            if (!hasNeighbor(pos)) continue

            // Collide check
            val crystalPos = pos.toVec3d(0.5, 1.0, 0.5)
            if (!contextSelf.checkColliding(crystalPos)) continue
            if (!contextTarget.checkColliding(crystalPos)) continue

            // Damage check
            val crystalDamage = calculateDamage(contextSelf, contextTarget, eyePos, crystalPos, pos, mutableBlockPos)
            if (!checkDamage(crystalDamage, maxCurrentDamage, minDamageInc)) continue

            cacheList.add(crystalDamage)
        }

        cacheList.sortByDescending { it.targetDamage }

        for (crystalDamage in cacheList) {
            return getNeighbor(crystalDamage.blockPos, 1) ?: continue
        }

        return null
    }

    private fun calculateDamage(
        contextSelf: CalcContext,
        contextTarget: CalcContext?,
        eyePos: Vec3d,
        crystalPos: Vec3d,
        pos: BlockPos,
        mutableBlockPos: BlockPos.MutableBlockPos
    ): CrystalDamage {
        val function: World.(BlockPos, IBlockState) -> FastRayTraceAction = { rayTracePos, blockState ->
            when {
                rayTracePos == pos -> {
                    FastRayTraceAction.HIT
                }
                blockState.block != Blocks.AIR && CrystalUtils.isResistant(blockState) -> {
                    FastRayTraceAction.CALC
                }
                else -> {
                    FastRayTraceAction.SKIP
                }
            }
        }

        val selfDamage = max(contextSelf.calcDamage(crystalPos, true, mutableBlockPos, function), contextSelf.calcDamage(crystalPos, false, mutableBlockPos, function))
        val targetDamage = contextTarget?.calcDamage(crystalPos, true, mutableBlockPos, function) ?: 0.0f

        return CrystalDamage(crystalPos, pos, selfDamage, targetDamage, eyePos.distanceTo(crystalPos), contextSelf.currentPos.distanceTo(crystalPos))
    }

    private fun checkDamage(crystalDamage: CrystalDamage, maxCurrentDamage: Float, minDamageInc: Float) =
        crystalDamage.selfDamage <= TrollAura.maxSelfDamage
            && (crystalDamage.targetDamage >= TrollAura.minDamage
            && (crystalDamage.targetDamage - maxCurrentDamage >= minDamageInc))
}