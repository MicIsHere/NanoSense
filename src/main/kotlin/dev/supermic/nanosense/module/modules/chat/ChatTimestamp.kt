package dev.supermic.nanosense.module.modules.chat

import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.TimeUtils
import dev.supermic.nanosense.util.accessor.textComponent
import dev.supermic.nanosense.util.graphics.color.EnumTextColor
import dev.supermic.nanosense.util.interfaces.DisplayEnum
import dev.supermic.nanosense.util.text.format
import net.minecraft.network.play.server.SPacketChat
import net.minecraft.util.text.TextComponentString

internal object ChatTimestamp : Module(
    name = "ChatTimestamp",
    category = Category.CHAT,
    description = "Shows the time a message was sent beside the message",
    visible = false
) {
    private val color by setting("Color", EnumTextColor.GRAY)
    private val separator by setting("Separator", Separator.ARROWS)
    private val timeFormat by setting("Time Format", TimeUtils.TimeFormat.HHMM)
    private val timeUnit by setting("Time Unit", TimeUtils.TimeUnit.H12)

    init {
        listener<PacketEvent.Receive> {
            if (it.packet is SPacketChat) {
                it.packet.textComponent = TextComponentString(formattedTime).appendSibling(it.packet.textComponent)
            }
        }
    }

    val formattedTime: String
        get() = "${separator.left}${color format TimeUtils.getTime(timeFormat, timeUnit)}${separator.right} "

    val time: String
        get() = "${separator.left}${TimeUtils.getTime(timeFormat, timeUnit)}${separator.right} "

    @Suppress("unused")
    private enum class Separator(override val displayName: CharSequence, val left: String, val right: String) :
        DisplayEnum {
        ARROWS("< >", "<", ">"),
        SQUARE_BRACKETS("[ ]", "[", "]"),
        CURLY_BRACKETS("{ }", "{", "}"),
        ROUND_BRACKETS("( )", "(", ")"),
        NONE("None", "", "")
    }
}
