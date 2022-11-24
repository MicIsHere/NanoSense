package dev.supermic.nanosense.event.events

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.event.NamedProfilerEventBus

sealed class RunGameLoopEvent : Event {
    object Start : RunGameLoopEvent(), EventPosting by NamedProfilerEventBus("start")
    object Tick : RunGameLoopEvent(), EventPosting by NamedProfilerEventBus("tick")
    object Render : RunGameLoopEvent(), EventPosting by NamedProfilerEventBus("render")
    object End : RunGameLoopEvent(), EventPosting by NamedProfilerEventBus("end")
}