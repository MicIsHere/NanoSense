package dev.supermic.nanosense.module.modules.movement

import dev.supermic.nanosense.event.events.player.PlayerMoveEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.manager.managers.HoleManager
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.module.modules.combat.Burrow
import dev.supermic.nanosense.module.modules.combat.HolePathFinder
import dev.supermic.nanosense.module.modules.combat.HoleSnap
import dev.supermic.nanosense.module.modules.combat.Surround
import dev.supermic.nanosense.util.EntityUtils.betterPosition
import dev.supermic.nanosense.util.MovementUtils.isCentered
import dev.supermic.nanosense.util.atTrue
import dev.supermic.nanosense.util.math.vector.toVec3d

internal object Anchor : Module(
    name = "Anchor",
    description = "Stops your motion when you are above hole",
    category = Category.MOVEMENT
) {
    private val autoCenter by setting("Auto Center", true)
    private val stopYMotion by setting("Stop Y Motion", true)
    private val pitchTrigger0 = setting("Pitch Trigger", true)
    private val pitchTrigger by pitchTrigger0
    private val pitch by setting("Pitch", 75, 0..90, 1, pitchTrigger0.atTrue())
    private val yRange by setting("Y Range", 3, 1..5, 1)

    init {
        safeListener<PlayerMoveEvent.Pre>(-1000) { event ->
            if (Burrow.isEnabled || Surround.isEnabled || HoleSnap.isActive() || HolePathFinder.isActive()) return@safeListener

            val playerPos = player.betterPosition
            val isInHole = player.onGround && HoleManager.getHoleInfo(playerPos).isHole

            if (!pitchTrigger || player.rotationPitch > pitch) {
                // Stops XZ motion
                val hole = HoleManager.getHoleBelow(playerPos, yRange) {
                    it.canEnter(world, playerPos)
                }

                if (isInHole || hole != null) {
                    val center = hole?.center ?: playerPos.toVec3d(0.5, 0.0, 0.5)

                    if (player.isCentered(center)) {
                        if (!player.isSneaking) {
                            player.motionX = 0.0
                            player.motionZ = 0.0
                            event.x = 0.0
                            event.z = 0.0
                        }
                    } else if (autoCenter) {
                        AutoCenter.centerPlayer(center)
                    }
                }

                // Stops Y motion
                if (stopYMotion && isInHole) {
                    player.motionY = -0.08
                    event.y = -0.08 // Minecraft needs this for on ground check
                }
            }
        }
    }
}