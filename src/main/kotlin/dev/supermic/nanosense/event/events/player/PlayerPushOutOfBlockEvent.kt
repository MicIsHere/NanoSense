package dev.supermic.nanosense.event.events.player

import dev.supermic.nanosense.event.*
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent

class PlayerPushOutOfBlockEvent(override val event: PlayerSPPushOutOfBlocksEvent) : Event, ICancellable, WrappedForgeEvent, EventPosting by Companion {
    companion object : EventBus()
}