package dev.supermic.nanosense.module.modules.chat

import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.TimeUtils
import dev.supermic.nanosense.util.text.MessageSendUtils.sendServerMessage
import dev.supermic.nanosense.util.text.NoSpamMessage
import dev.supermic.nanosense.util.text.format
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.EnumDifficulty

internal object AutoQMain : Module(
    name = "AutoQMain",
    description = "Automatically does '/queue main'",
    category = Category.CHAT,
    visible = false
) {
    private val delay by setting("Delay", 5000, 0..15000, 100)
    private val twoBeeCheck by setting("2B Check", true)
    private val command by setting("Command", "/queue main")

    private val timer = TickTimer()

    init {
        @Suppress("UNNECESSARY_SAFE_CALL")
        safeListener<TickEvent.Pre> {
            if (world.difficulty == EnumDifficulty.PEACEFUL
                && player.dimension == 1
                && (!twoBeeCheck || player.serverBrand?.contains("2b2t") == true)
                && timer.tickAndReset(delay)) {
                sendQueueMain()
            }
        }
    }

    private fun sendQueueMain() {
        NoSpamMessage.sendMessage(this, "$chatName Run ${TextFormatting.GRAY format command} at ${TextFormatting.GRAY format TimeUtils.getTime(TimeUtils.TimeFormat.HHMMSS, TimeUtils.TimeUnit.H24)}")
        sendServerMessage(command)
    }
}
