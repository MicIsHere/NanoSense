package dev.supermic.nanosense

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import dev.supermic.nanosense.command.CommandManager
import dev.supermic.nanosense.gui.GuiManager
import dev.supermic.nanosense.manager.ManagerLoader
import dev.supermic.nanosense.module.ModuleManager
import dev.supermic.nanosense.util.ClassUtils
import dev.supermic.nanosense.util.threads.mainScope
import kotlin.system.measureTimeMillis

internal object LoaderWrapper {
    private val loaderList = ArrayList<AsyncLoader<*>>()

    init {
        loaderList.add(ModuleManager)
        loaderList.add(CommandManager)
        loaderList.add(ManagerLoader)
        loaderList.add(GuiManager)
    }

    @JvmStatic
    fun preLoadAll() {
        loaderList.forEach { it.preLoad() }
    }

    @JvmStatic
    fun loadAll() {
        runBlocking {
            loaderList.forEach { it.load() }
        }
    }
}

internal interface AsyncLoader<T> {
    var deferred: Deferred<T>?

    fun preLoad() {
        deferred = preLoadAsync()
    }

    private fun preLoadAsync(): Deferred<T> {
        return mainScope.async { preLoad0() }
    }

    suspend fun load() {
        load0((deferred ?: preLoadAsync()).await())
    }

    suspend fun preLoad0(): T
    suspend fun load0(input: T)

    companion object {
        val classes = mainScope.async {
            val list: List<Class<*>>
            val time = measureTimeMillis {
                list = ClassUtils.findClasses("dev.supermic.nanosense") {
                    !it.contains("mixins")
                }
            }

            NanoSenseMod.logger.info("${list.size} classes found, took ${time}ms")
            list
        }
    }
}