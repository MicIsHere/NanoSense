package dev.supermic.nanosense.event.events

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.event.NamedProfilerEventBus

sealed class ProcessKeyBindEvent : Event {
    object Pre : ProcessKeyBindEvent(), EventPosting by NamedProfilerEventBus("pre")
    object Post : ProcessKeyBindEvent(), EventPosting by NamedProfilerEventBus("post")
}