package dev.supermic.nanosense.event.events

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.manager.managers.WaypointManager.Waypoint

class WaypointUpdateEvent(val type: Type, val waypoint: Waypoint?) : Event, EventPosting by Companion {
    enum class Type {
        GET, ADD, REMOVE, CLEAR, RELOAD
    }

    companion object : EventBus()
}