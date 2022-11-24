package dev.supermic.nanosense.module.modules.chat

import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeParallelListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.atTrue
import dev.supermic.nanosense.util.text.MessageDetection
import dev.supermic.nanosense.util.text.MessageSendUtils
import dev.supermic.nanosense.util.text.MessageSendUtils.sendServerMessage
import net.minecraft.network.play.server.SPacketChat

internal object AutoReply : Module(
    name = "AutoReply",
    description = "Automatically reply to direct messages",
    category = Category.CHAT
) {
    private val customMessage = setting("Custom Message", false)
    private val customText = setting("Custom Text", "unchanged", customMessage.atTrue())

    private val timer = TickTimer(TimeUnit.SECONDS)

    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketChat || MessageDetection.Direct.RECEIVE detect it.packet.chatComponent.unformattedText) return@listener
            if (customMessage.value) {
                sendServerMessage("/r " + customText.value)
            } else {
                sendServerMessage("/r I just automatically replied, thanks to Troll Hack's AutoReply module!")
            }
        }

        safeParallelListener<TickEvent.Post> {
            if (timer.tickAndReset(5L) && customMessage.value && customText.value.equals("unchanged", true)) {
                MessageSendUtils.sendNoSpamWarningMessage("$chatName Warning: In order to use the custom $name, please change the CustomText setting in ClickGUI")
            }
        }
    }
}