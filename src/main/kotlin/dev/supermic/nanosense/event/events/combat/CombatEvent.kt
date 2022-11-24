package dev.supermic.nanosense.event.events.combat

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import net.minecraft.entity.EntityLivingBase

sealed class CombatEvent : Event {
    abstract val entity: EntityLivingBase?

    class UpdateTarget(val prevEntity: EntityLivingBase?, override val entity: EntityLivingBase?) : CombatEvent(), EventPosting by Companion {
        companion object : EventBus()
    }
}
