package dev.supermic.nanosense.module

import kotlinx.coroutines.Deferred
import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.util.ClassUtils.instance
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.collections.AliasSet
import dev.supermic.nanosense.util.delegate.AsyncCachedValue
import dev.supermic.nanosense.util.extension.rootName
import dev.supermic.nanosense.util.interfaces.Helper
import java.lang.reflect.Modifier
import kotlin.system.measureTimeMillis

object ModuleManager : dev.supermic.nanosense.AsyncLoader<List<Class<out AbstractModule>>>, Helper {
    override var deferred: Deferred<List<Class<out AbstractModule>>>? = null

    private val moduleSet = AliasSet<AbstractModule>()
    private val modulesDelegate = AsyncCachedValue(5L, TimeUnit.SECONDS) {
        moduleSet.distinct().sortedBy { it.rootName }
    }
    val modules by modulesDelegate

    override suspend fun preLoad0(): List<Class<out AbstractModule>> {
        val classes = dev.supermic.nanosense.AsyncLoader.classes.await()
        val list: List<Class<*>>

        val time = measureTimeMillis {
            val clazz = AbstractModule::class.java

            list = classes.asSequence()
                .filter { Modifier.isFinal(it.modifiers) }
                .filter { it.name.startsWith("dev.supermic.nanosense.module.modules") }
                .filter { clazz.isAssignableFrom(it) }
                .sortedBy { it.simpleName }
                .toList()
        }

        NanoSenseMod.logger.info("${list.size} modules found, took ${time}ms")

        @Suppress("UNCHECKED_CAST")
        return list as List<Class<out AbstractModule>>
    }

    override suspend fun load0(input: List<Class<out AbstractModule>>) {
        val time = measureTimeMillis {
            for (clazz in input) {
                register(clazz.instance)
            }
        }

        NanoSenseMod.logger.info("${input.size} modules loaded, took ${time}ms")
    }

    internal fun register(module: AbstractModule) {
        moduleSet.add(module)
        if (module.enabledByDefault || module.alwaysEnabled) module.enable()
        if (module.alwaysListening) module.subscribe()

        modulesDelegate.update()
    }

    internal fun unregister(module: AbstractModule) {
        moduleSet.remove(module)
        module.unsubscribe()

        modulesDelegate.update()
    }

    fun getModuleOrNull(moduleName: String?) = moduleName?.let { moduleSet[it] }
}