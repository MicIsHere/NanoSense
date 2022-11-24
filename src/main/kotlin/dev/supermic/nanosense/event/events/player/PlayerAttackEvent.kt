package dev.supermic.nanosense.event.events.player

import dev.supermic.nanosense.event.Cancellable
import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import net.minecraft.entity.Entity

class PlayerAttackEvent(val entity: Entity) : Event, Cancellable(), EventPosting by Companion {
    companion object : EventBus()
}