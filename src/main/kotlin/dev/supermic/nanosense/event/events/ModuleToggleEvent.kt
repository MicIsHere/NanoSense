package dev.supermic.nanosense.event.events

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.module.AbstractModule

class ModuleToggleEvent internal constructor(val module: AbstractModule) : Event, EventPosting by Companion {
    companion object : EventBus()
}