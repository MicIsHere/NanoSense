package dev.supermic.nanosense.event.events.player

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.event.WrappedForgeEvent
import net.minecraftforge.client.event.InputUpdateEvent

class InputUpdateEvent(override val event: InputUpdateEvent) : Event, WrappedForgeEvent, EventPosting by Companion {
    val movementInput
        get() = event.movementInput

    companion object : EventBus()
}