package dev.supermic.nanosense.event.events.render

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting

class ResolutionUpdateEvent(val width: Int, val height: Int) : Event, EventPosting by Companion {
    companion object : EventBus()
}