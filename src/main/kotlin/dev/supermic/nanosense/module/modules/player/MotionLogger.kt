package dev.supermic.nanosense.module.modules.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.event.events.ConnectionEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.event.safeParallelListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.MovementUtils.realSpeed
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.threads.defaultScope
import java.io.File
import java.io.FileWriter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal object MotionLogger : Module(
    name = "MotionLogger",
    description = "Logs player motion to a file",
    category = Category.PLAYER
) {
    private val fileTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss_SSS")
    private val timer = TickTimer(TimeUnit.SECONDS)

    private const val directory = "${NanoSenseMod.DIRECTORY}/motionLogs"

    private var tick = 0
    private var filename = ""
    private var lines = ArrayList<String>()

    init {
        onEnable {
            filename = "${fileTimeFormatter.format(LocalTime.now())}.csv"

            synchronized(this) {
                lines.add("Tick,Pos X,Pos Y,Pos Z,Motion X,Motion Y,Motion Z,Speed\n")
                tick = 0
            }
        }

        onDisable {
            write()
        }

        safeParallelListener<TickEvent.Post> {
            val motionX = player.posX - player.lastTickPosX
            val motionY = player.posY - player.lastTickPosY
            val motionZ = player.posZ - player.lastTickPosZ
            val speed = player.realSpeed

            synchronized(MotionLogger) {
                lines.add("${tick++},${player.posX},${player.posY},${player.posZ},$motionX,$motionY,$motionZ,$speed\n")
            }

            if (lines.size >= 500 || timer.tickAndReset(15L)) {
                write()
            }
        }

        safeListener<ConnectionEvent.Disconnect> {
            disable()
        }
    }

    private fun write() {
        val lines = synchronized(this) {
            val cache = lines
            lines = ArrayList()
            cache
        }

        defaultScope.launch(Dispatchers.IO) {
            try {
                with(File(directory)) {
                    if (!exists()) mkdir()
                }

                FileWriter("$directory/${filename}", true).buffered().use {
                    for (line in lines) it.write(line)
                }
            } catch (e: Exception) {
                NanoSenseMod.logger.warn("$chatName Failed saving motion log!", e)
            }
        }
    }
}
