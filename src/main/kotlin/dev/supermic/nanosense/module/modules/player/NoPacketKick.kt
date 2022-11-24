package dev.supermic.nanosense.module.modules.player

import dev.supermic.nanosense.mixins.core.network.MixinNetworkManager
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.text.MessageSendUtils.sendNoSpamWarningMessage

/**
 * @see MixinNetworkManager
 */
internal object NoPacketKick : Module(
    name = "NoPacketKick",
    category = Category.PLAYER,
    description = "Suppress network exceptions and prevent getting kicked",
    visible = false
) {
    @JvmStatic
    fun sendWarning(throwable: Throwable) {
        sendNoSpamWarningMessage("$chatName Caught exception - \"$throwable\" check log for more info.")
        throwable.printStackTrace()
    }
}
