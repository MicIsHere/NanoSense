package dev.supermic.nanosense.event.events

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting

sealed class ConnectionEvent : Event {
    object Connect : ConnectionEvent(), EventPosting by EventBus()
    object Disconnect : ConnectionEvent(), EventPosting by EventBus()
}