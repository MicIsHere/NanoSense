package dev.supermic.nanosense.event.events.player

import dev.supermic.nanosense.event.*

class PlayerTravelEvent : Event, ICancellable by Cancellable(), EventPosting by Companion {
    companion object : NamedProfilerEventBus("trollPlayerTravel")
}