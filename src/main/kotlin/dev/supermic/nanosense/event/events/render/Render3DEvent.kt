package dev.supermic.nanosense.event.events.render

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.event.NamedProfilerEventBus

object Render3DEvent : Event, EventPosting by NamedProfilerEventBus("trollRender3D")