package dev.supermic.nanosense.event.events.combat

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import net.minecraft.entity.player.EntityPlayer

sealed class TotemPopEvent(val name: String, val count: Int) : Event {
    class Pop(val entity: EntityPlayer, count: Int) : TotemPopEvent(entity.name, count), EventPosting by Companion {
        companion object : EventBus()
    }

    class Death(val entity: EntityPlayer, count: Int) : TotemPopEvent(entity.name, count), EventPosting by Companion {
        companion object : EventBus()
    }

    class Clear(name: String, count: Int) : TotemPopEvent(name, count), EventPosting by Companion {
        companion object : EventBus()
    }
}
