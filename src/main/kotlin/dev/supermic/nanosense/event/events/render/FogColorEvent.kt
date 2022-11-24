package dev.supermic.nanosense.event.events.render

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.event.WrappedForgeEvent
import net.minecraftforge.client.event.EntityViewRenderEvent

class FogColorEvent(override val event: EntityViewRenderEvent.FogColors) : Event, WrappedForgeEvent, EventPosting by Companion {
    var red by event::red
    var green by event::green
    var blue by event::blue

    companion object : EventBus()
}