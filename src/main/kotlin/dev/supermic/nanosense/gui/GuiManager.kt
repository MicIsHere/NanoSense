package dev.supermic.nanosense.gui

import kotlinx.coroutines.Deferred
import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.gui.clickgui.NanoClickGui
import dev.supermic.nanosense.gui.hudgui.AbstractHudElement
import dev.supermic.nanosense.gui.hudgui.NanoHudGui
import dev.supermic.nanosense.util.ClassUtils.instance
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.collections.AliasSet
import dev.supermic.nanosense.util.delegate.AsyncCachedValue
import java.lang.reflect.Modifier
import kotlin.system.measureTimeMillis

internal object GuiManager : dev.supermic.nanosense.AsyncLoader<List<Class<out AbstractHudElement>>> {
    override var deferred: Deferred<List<Class<out AbstractHudElement>>>? = null
    private val hudElementSet = AliasSet<AbstractHudElement>()

    val hudElements by AsyncCachedValue(5L, TimeUnit.SECONDS) {
        hudElementSet.distinct().sortedBy { it.nameAsString }
    }

    override suspend fun preLoad0(): List<Class<out AbstractHudElement>> {
        val classes = dev.supermic.nanosense.AsyncLoader.classes.await()
        val list: List<Class<*>>

        val time = measureTimeMillis {
            val clazz = AbstractHudElement::class.java

            list = classes.asSequence()
                .filter { Modifier.isFinal(it.modifiers) }
                .filter { it.name.startsWith("dev.supermic.nanosense.gui.hudgui.elements") }
                .filter { clazz.isAssignableFrom(it) }
                .sortedBy { it.simpleName }
                .toList()
        }

        NanoSenseMod.logger.info("${list.size} hud elements found, took ${time}ms")

        @Suppress("UNCHECKED_CAST")
        return list as List<Class<out AbstractHudElement>>
    }

    override suspend fun load0(input: List<Class<out AbstractHudElement>>) {
        val time = measureTimeMillis {
            for (clazz in input) {
                register(clazz.instance)
            }
        }

        NanoSenseMod.logger.info("${input.size} hud elements loaded, took ${time}ms")

        NanoClickGui.onGuiClosed()
        NanoHudGui.onGuiClosed()

        NanoClickGui.subscribe()
        NanoHudGui.subscribe()
    }

    internal fun register(hudElement: AbstractHudElement) {
        hudElementSet.add(hudElement)
        NanoHudGui.register(hudElement)
    }

    internal fun unregister(hudElement: AbstractHudElement) {
        hudElementSet.remove(hudElement)
        NanoHudGui.unregister(hudElement)
    }

    fun getHudElementOrNull(name: String?) = name?.let { hudElementSet[it] }
}