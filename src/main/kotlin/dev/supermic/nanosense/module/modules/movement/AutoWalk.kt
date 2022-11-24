package dev.supermic.nanosense.module.modules.movement

import baritone.api.pathing.goals.GoalXZ
import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.event.events.ConnectionEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.events.baritone.BaritoneCommandEvent
import dev.supermic.nanosense.event.events.player.InputUpdateEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.module.modules.player.LagNotifier
import dev.supermic.nanosense.util.BaritoneUtils
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.extension.fastFloor
import dev.supermic.nanosense.util.interfaces.DisplayEnum
import dev.supermic.nanosense.util.math.Direction
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.threads.runSafe
import net.minecraft.util.MovementInputFromOptions

internal object AutoWalk : Module(
    name = "AutoWalk",
    category = Category.MOVEMENT,
    description = "Automatically walks somewhere"
) {
    private val mode = setting("Direction", AutoWalkMode.BARITONE)
    private val disableOnDisconnect by setting("Disable On Disconnect", true)

    private enum class AutoWalkMode(override val displayName: CharSequence) : DisplayEnum {
        FORWARD("Forward"),
        BACKWARD("Backward"),
        BARITONE("Baritone")
    }

    val baritoneWalk get() = isEnabled && mode.value == AutoWalkMode.BARITONE

    private const val border = 30000000
    private val messageTimer = TickTimer(TimeUnit.SECONDS)
    var direction = Direction.NORTH; private set

    override fun isActive(): Boolean {
        return isEnabled && (mode.value != AutoWalkMode.BARITONE || BaritoneUtils.isActive || BaritoneUtils.isPathing)
    }

    override fun getHudInfo(): String {
        return if (mode.value == AutoWalkMode.BARITONE && (BaritoneUtils.isActive || BaritoneUtils.isPathing)) {
            direction.displayString
        } else {
            mode.value.displayString
        }
    }

    init {
        onDisable {
            if (mode.value == AutoWalkMode.BARITONE) BaritoneUtils.cancelEverything()
        }

        listener<BaritoneCommandEvent> {
            if (it.command.contains("cancel")) {
                disable()
            }
        }

        listener<ConnectionEvent.Disconnect> {
            if (disableOnDisconnect) disable()
        }

        listener<InputUpdateEvent>(6969) {
            if (LagNotifier.paused && LagNotifier.pauseAutoWalk) return@listener

            if (it.movementInput !is MovementInputFromOptions) return@listener

            when (mode.value) {
                AutoWalkMode.FORWARD -> {
                    it.movementInput.moveForward = 1.0f
                }
                AutoWalkMode.BACKWARD -> {
                    it.movementInput.moveForward = -1.0f
                }
                else -> {
                    // Baritone mode
                }
            }
        }

        safeListener<TickEvent.Pre> {
            if (mode.value == AutoWalkMode.BARITONE && !checkBaritoneElytra() && !isActive()) {
                startPathing()
            }
        }
    }

    private fun SafeClientEvent.startPathing() {
        if (!world.isChunkGeneratedAt(player.chunkCoordX, player.chunkCoordZ)) return

        direction = Direction.fromEntity(player)
        val x = player.posX.fastFloor() + direction.directionVec.x * border
        val z = player.posZ.fastFloor() + direction.directionVec.z * border

        BaritoneUtils.cancelEverything()
        BaritoneUtils.primary?.customGoalProcess?.setGoalAndPath(GoalXZ(x, z))
    }

    private fun checkBaritoneElytra() = mc.player?.let {
        if (it.isElytraFlying && messageTimer.tickAndReset(10L)) {
            MessageSendUtils.sendNoSpamErrorMessage("$chatName Baritone mode isn't currently compatible with Elytra flying!" +
                " Choose a different mode if you want to use AutoWalk while Elytra flying")
        }
        it.isElytraFlying
    } ?: true

    init {
        mode.listeners.add {
            if (isDisabled || mc.player == null) return@add
            if (mode.value == AutoWalkMode.BARITONE) {
                if (!checkBaritoneElytra()) {
                    runSafe { startPathing() }
                }
            } else {
                BaritoneUtils.cancelEverything()
            }
        }
    }
}