package dev.supermic.nanosense.event.events

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import net.minecraft.entity.EntityLivingBase

sealed class EntityEvent(val entity: EntityLivingBase) : Event {
    class UpdateHealth(entity: EntityLivingBase, val prevHealth: Float, val health: Float) : EntityEvent(entity), EventPosting by Companion {
        companion object : EventBus()
    }

    class Death(entity: EntityLivingBase) : EntityEvent(entity), EventPosting by Companion {
        companion object : EventBus()
    }
}