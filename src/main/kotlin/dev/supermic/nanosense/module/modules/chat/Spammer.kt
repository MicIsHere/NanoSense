package dev.supermic.nanosense.module.modules.chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.atTrue
import dev.supermic.nanosense.util.extension.synchronized
import dev.supermic.nanosense.util.text.MessageDetection
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.text.MessageSendUtils.sendServerMessage
import dev.supermic.nanosense.util.threads.defaultScope
import java.io.File
import java.net.URL
import kotlin.random.Random

internal object Spammer : Module(
    name = "Spammer",
    description = "Spams text from a file on a set delay into the chat",
    category = Category.CHAT,
    modulePriority = 100
) {
    private val modeSetting = setting("Order", Mode.RANDOM_ORDER)
    private val delay = setting("Delay", 10, 1..180, 1, description = "Delay between messages, in seconds")
    private val loadRemote = setting("Load From URL", false)
    private val remoteURL = setting("Remote URL", "Unchanged", loadRemote.atTrue())

    private val file = File("${NanoSenseMod.DIRECTORY}/spammer.txt")
    private val spammer = ArrayList<String>().synchronized()
    private val timer = TickTimer(TimeUnit.SECONDS)
    private var currentLine = 0

    private enum class Mode {
        IN_ORDER, RANDOM_ORDER
    }

    private val urlValue
        get() = if (remoteURL.value != "Unchanged") {
            remoteURL.value
        } else {
            MessageSendUtils.sendNoSpamErrorMessage("Change the RemoteURL setting in the ClickGUI!")
            disable()
            null
        }

    init {
        onEnable {
            spammer.clear()

            if (loadRemote.value) {
                val url = urlValue ?: return@onEnable

                defaultScope.launch(Dispatchers.IO) {
                    try {
                        val text = URL(url).readText()
                        spammer.addAll(text.split("\n"))

                        MessageSendUtils.sendNoSpamChatMessage("$chatName Loaded remote spammer messages!")
                    } catch (e: Exception) {
                        MessageSendUtils.sendNoSpamErrorMessage("$chatName Failed loading remote spammer, $e")
                        disable()
                    }
                }

            } else {
                defaultScope.launch(Dispatchers.IO) {
                    if (file.exists()) {
                        try {
                            file.forEachLine { if (it.isNotBlank()) spammer.add(it.trim()) }
                            MessageSendUtils.sendNoSpamChatMessage("$chatName Loaded spammer messages!")
                        } catch (e: Exception) {
                            MessageSendUtils.sendNoSpamErrorMessage("$chatName Failed loading spammer, $e")
                            disable()
                        }
                    } else {
                        file.createNewFile()
                        MessageSendUtils.sendNoSpamErrorMessage("$chatName Spammer file is empty!" +
                            ", please add them in the §7spammer.txt§f under the §7.minecraft/trollhack§f directory.")
                        disable()
                    }
                }
            }
        }

        safeListener<TickEvent.Post> {
            if (spammer.isEmpty() || !timer.tickAndReset(delay.value)) return@safeListener

            val message = if (modeSetting.value == Mode.IN_ORDER) getOrdered() else getRandom()
            if (MessageDetection.Command.NANOSENSE detect message) {
                MessageSendUtils.sendTrollCommand(message)
            } else {
                Spammer.sendServerMessage(message)
            }
        }
    }

    private fun getOrdered(): String {
        currentLine %= spammer.size
        return spammer[currentLine++]
    }

    private fun getRandom(): String {
        val prevLine = currentLine
        // Avoids sending the same message
        while (spammer.size != 1 && currentLine == prevLine) {
            currentLine = Random.nextInt(spammer.size)
        }
        return spammer[currentLine]
    }
}
