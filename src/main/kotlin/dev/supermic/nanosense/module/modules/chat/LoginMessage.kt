package dev.supermic.nanosense.module.modules.chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.event.events.ConnectionEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.MovementUtils.isMoving
import dev.supermic.nanosense.util.text.MessageDetection
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.text.MessageSendUtils.sendServerMessage
import dev.supermic.nanosense.util.threads.defaultScope
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

internal object LoginMessage : Module(
    name = "LoginMessage",
    description = "Sends a given message(s) to public chat on login.",
    category = Category.CHAT,
    visible = false,
    modulePriority = 150
) {
    private val sendAfterMoving by setting("Send After Moving", false, description = "Wait until you have moved after logging in")

    private val file = File("${NanoSenseMod.DIRECTORY}/loginmsg.txt")
    private val loginMessages = CopyOnWriteArrayList<String>()
    private var sent = false
    private var moved = false

    init {
        onEnable {
            if (file.exists()) {
                defaultScope.launch(Dispatchers.IO) {
                    try {
                        file.forEachLine {
                            if (it.isNotBlank()) loginMessages.add(it.trim())
                        }
                        MessageSendUtils.sendNoSpamChatMessage("$chatName Loaded ${loginMessages.size} login messages!")
                    } catch (e: Exception) {
                        MessageSendUtils.sendNoSpamErrorMessage("$chatName Failed loading login messages, $e")
                        disable()
                    }
                }
            } else {
                file.createNewFile()
                MessageSendUtils.sendNoSpamErrorMessage("$chatName Login Messages file not found!" +
                    ", please add them in the §7loginmsg.txt§f under the §7.minecraft/trollhack§f directory.")
                disable()
            }
        }

        onDisable {
            loginMessages.clear()
        }

        listener<ConnectionEvent.Disconnect> {
            sent = false
            moved = false
        }

        safeListener<TickEvent.Post> {
            if (!sent && (!sendAfterMoving || moved)) {
                for (message in loginMessages) {
                    if (MessageDetection.Command.NANOSENSE detect message) {
                        MessageSendUtils.sendTrollCommand(message)
                    } else {
                        LoginMessage.sendServerMessage(message)
                    }
                }

                sent = true
            }

            if (!moved) moved = player.isMoving
        }
    }
}
