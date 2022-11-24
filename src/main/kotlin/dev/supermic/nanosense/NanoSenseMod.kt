package dev.supermic.nanosense

import dev.supermic.nanosense.event.ForgeEventProcessor
import dev.supermic.nanosense.event.events.ShutdownEvent
import dev.supermic.nanosense.translation.TranslationManager
import dev.supermic.nanosense.util.ConfigUtils
import dev.supermic.nanosense.util.graphics.font.renderer.MainFontRenderer
import dev.supermic.nanosense.util.threads.BackgroundScope
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.opengl.Display
import java.io.File

@Mod(
    modid = NanoSenseMod.ID,
    name = NanoSenseMod.NAME,
    version = NanoSenseMod.VERSION
)
class NanoSenseMod {
    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        val directory = File("${DIRECTORY}/")
        if (!directory.exists()) directory.mkdir()

        dev.supermic.nanosense.LoaderWrapper.preLoadAll()
        TranslationManager.checkUpdate()

        Thread.currentThread().priority = Thread.MAX_PRIORITY
    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        logger.info("Initializing $NAME $VERSION")

        dev.supermic.nanosense.LoaderWrapper.loadAll()
        MinecraftForge.EVENT_BUS.register(ForgeEventProcessor)
        ConfigUtils.loadAll()
        BackgroundScope.start()
        MainFontRenderer.reloadFonts()

        logger.info("$NAME initialized!")
    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        ready = true
        Runtime.getRuntime().addShutdownHook(Thread {
            ShutdownEvent.post()
            ConfigUtils.saveAll()
        })
    }

    companion object {
        const val NAME = "NanoSense"
        const val ID = "nanosense"
        const val VERSION = "0.0.1"
        const val DIRECTORY = "nanosense"

        @JvmField
        val title: String = Display.getTitle()

        @JvmField
        val logger: Logger = LogManager.getLogger(NAME)

        @JvmStatic
        @get:JvmName("isReady")
        var ready = false; private set
    }
}