package dev.supermic.nanosense.event.events.render

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.event.NamedProfilerEventBus

sealed class Render2DEvent : Event {
    object Mc : Render2DEvent(), EventPosting by NamedProfilerEventBus("mc")
    object Absolute : Render2DEvent(), EventPosting by NamedProfilerEventBus("absolute")
    object NanoSense : Render2DEvent(), EventPosting by NamedProfilerEventBus("troll")
}