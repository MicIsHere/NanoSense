package dev.supermic.nanosense.event.events

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.event.NamedProfilerEventBus

sealed class TickEvent : Event {
    object Pre : TickEvent(), EventPosting by NamedProfilerEventBus("trollTickPre")
    object Post : TickEvent(), EventPosting by NamedProfilerEventBus("trollTickPost")
}