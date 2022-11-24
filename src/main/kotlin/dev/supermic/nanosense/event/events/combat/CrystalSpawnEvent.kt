package dev.supermic.nanosense.event.events.combat

import dev.supermic.nanosense.event.Event
import dev.supermic.nanosense.event.EventBus
import dev.supermic.nanosense.event.EventPosting
import dev.supermic.nanosense.util.combat.CrystalDamage

class CrystalSpawnEvent(
    val entityID: Int,
    val crystalDamage: CrystalDamage
) : Event, EventPosting by Companion {
    companion object : EventBus()
}