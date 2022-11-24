package dev.supermic.nanosense.event.events.baritone

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting

class BaritoneCommandEvent(val command: String) : Event, EventPosting by Companion {
    companion object : EventBus()
}