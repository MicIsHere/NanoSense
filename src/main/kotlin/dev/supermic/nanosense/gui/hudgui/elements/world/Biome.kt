package dev.supermic.nanosense.gui.hudgui.elements.world

import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.gui.hudgui.LabelHud

internal object Biome : LabelHud(
    name = "Biome",
    category = Category.WORLD,
    description = "Display the current biome you are in"
) {

    override fun SafeClientEvent.updateText() {
        val biome = world.getBiome(player.position).biomeName ?: "Unknown"

        displayText.add(biome, primaryColor)
        displayText.add("Biome", secondaryColor)
    }

}