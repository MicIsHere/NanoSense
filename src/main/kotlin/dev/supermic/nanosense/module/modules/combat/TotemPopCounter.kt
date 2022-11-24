package dev.supermic.nanosense.module.modules.combat

import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.event.events.EntityEvent
import dev.supermic.nanosense.event.events.combat.TotemPopEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.gui.hudgui.elements.client.Notification
import dev.supermic.nanosense.manager.managers.FriendManager
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.graphics.color.EnumTextColor
import dev.supermic.nanosense.util.text.MessageSendUtils.sendServerMessage
import dev.supermic.nanosense.util.text.NoSpamMessage
import dev.supermic.nanosense.util.text.format
import net.minecraft.util.text.TextFormatting

internal object TotemPopCounter : Module(
    name = "TotemPopCounter",
    description = "Counts how many times players pop",
    category = Category.COMBAT
) {
    private val countFriends by setting("Count Friends", true)
    private val countSelf by setting("Count Self", true)
    private val thanksTo by setting("Thanks To", false)
    private val colorName by setting("Color Name", EnumTextColor.BLUE)
    private val colorNumber by setting("Color Number", EnumTextColor.GREEN)
    private val chat by setting("Chat", true)
    private val announce by setting("Announce", Announce.CLIENT, { chat })
    private val notification by setting("Notification", true)

    private enum class Announce {
        CLIENT, SERVER
    }

    init {
        safeListener<TotemPopEvent.Pop> {
            if (friendCheck(it.name) && selfCheck(it.name)) {
                val isSelf = it.name == player.name
                val message = "${formatName(it.name)} popped ${formatNumber(it.count)} ${plural(it.count)}${ending(isSelf)}"
                sendMessage(it.name, message, !isSelf && isPublic)
            }
        }

        safeListener<TotemPopEvent.Death> {
            if (friendCheck(it.name) && selfCheck(it.name)) {
                val message = "${formatName(it.name)} died after popping ${formatNumber(it.count)} ${plural(it.count)}${ending(false)}"
                sendMessage(it.name, message, isPublic)
            }
        }

        safeListener<EntityEvent.Death>(-1000) {
            if (it.entity == player) {
                Notification.send(TotemPopCounter, "$chatName Cleared totem pops count on death")
            }
        }
    }

    private fun friendCheck(name: String): Boolean {
        return countFriends || !FriendManager.isFriend(name)
    }

    private fun selfCheck(name: String): Boolean {
        return countSelf || name != mc.player?.name
    }

    private fun formatName(name: String): String {
        return colorName.textFormatting format when {
            name == mc.player?.name -> "I"
            FriendManager.isFriend(name) -> if (isPublic) "My friend ${name}, " else "Your friend ${name}, "
            else -> name
        }
    }

    private val isPublic: Boolean
        get() = chat && announce == Announce.SERVER

    private fun formatNumber(message: Int): String {
        return colorNumber.textFormatting format message
    }

    private fun plural(count: Int): String {
        return if (count == 1) "totem" else "totems"
    }

    private fun ending(self: Boolean): String {
        return if (!self && thanksTo) " thanks to ${NanoSenseMod.NAME} !" else "!"
    }

    private fun sendMessage(name: String, message: String, public: Boolean) {
        TextFormatting.getTextWithoutFormattingCodes(message)?.let {
            if (public) sendServerMessage(it)
            else if (chat) NoSpamMessage.sendMessage(name.hashCode(), "$chatName $message")
            if (notification) Notification.send(this.hashCode() * 31 + name.hashCode(), message)
        }
    }
}