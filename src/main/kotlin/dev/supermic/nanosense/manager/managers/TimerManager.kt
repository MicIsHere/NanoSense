package dev.supermic.nanosense.manager.managers

import dev.supermic.nanosense.event.events.RunGameLoopEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.manager.Manager
import dev.supermic.nanosense.module.AbstractModule
import dev.supermic.nanosense.util.accessor.tickLength
import dev.supermic.nanosense.util.accessor.timer
import dev.supermic.nanosense.util.extension.lastValueOrNull
import dev.supermic.nanosense.util.extension.synchronized
import dev.supermic.nanosense.util.graphics.RenderUtils3D
import dev.supermic.nanosense.util.threads.runSafe
import java.util.*
import kotlin.math.roundToInt

object TimerManager : Manager() {
    private val modifiers = TreeMap<AbstractModule, Modifier>().synchronized()
    private var modified = false

    var totalTicks = Int.MIN_VALUE
    var tickLength = 50.0f; private set

    init {
        listener<RunGameLoopEvent.Start>(Int.MAX_VALUE, true) {
            runSafe {
                synchronized(modifiers) {
                    modifiers.values.removeIf { it.endTick < totalTicks }
                    modifiers.lastValueOrNull()?.let {
                        mc.timer.tickLength = it.tickLength
                    } ?: return@runSafe null
                }

                modified = true
            } ?: run {
                modifiers.clear()
                if (modified) {
                    mc.timer.tickLength = 50.0f
                    modified = false
                }
            }

            tickLength = mc.timer.tickLength
        }

        listener<TickEvent.Pre>(Int.MAX_VALUE, true) {
            totalTicks++
        }
    }

    fun AbstractModule.resetTimer() {
        modifiers.remove(this)
    }

    fun AbstractModule.modifyTimer(tickLength: Float, timeoutTicks: Int = 1) {
        runSafe {
            modifiers[this@modifyTimer] = Modifier(tickLength, totalTicks + RenderUtils3D.partialTicks.roundToInt() + timeoutTicks)
        }
    }

    private class Modifier(
        val tickLength: Float,
        val endTick: Int
    )
}