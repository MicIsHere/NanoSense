package dev.supermic.nanosense.gui.hudgui.elements.client

import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.gui.hudgui.LabelHud
import dev.supermic.nanosense.util.TickTimer
import org.lwjgl.opengl.Display
import java.lang.reflect.Field
import java.lang.reflect.Method

internal object Watermark : LabelHud(
    name = "Watermark",
    category = Category.CLIENT,
    description = "NanoSense watermark",
    enabledByDefault = true
) {
    private const val title = "${NanoSenseMod.NAME} ${NanoSenseMod.VERSION}"
    private val timer = TickTimer()
    private val titleField: Field
    private val displayImpl: Any
    private val setTitleMethod: Method

    init {
        val displayClazz = Display::class.java
        titleField = displayClazz.getDeclaredField("title")

        val displayImplField = displayClazz.getDeclaredField("display_impl")
        displayImplField.isAccessible = true
        displayImpl = displayImplField.get(null)
        displayImplField.isAccessible = false

        val displayImplClass = Class.forName("org.lwjgl.opengl.DisplayImplementation")
        setTitleMethod = displayImplClass.getDeclaredMethod("setTitle", String::class.java)

        listener<TickEvent.Pre>(true) {
            if (timer.tickAndReset(3000L)) {
                try {
                    titleField.isAccessible = true
                    titleField.set(null, title)
                    titleField.isAccessible = false

                    setTitleMethod.isAccessible = true
                    setTitleMethod.invoke(displayImpl, title)
                    setTitleMethod.isAccessible = false
                } catch (t: Throwable) {
                    //
                }
            }
        }

        settingList.forEach {
            it.listeners.add {
                timer.reset(-69420L)
            }
        }
    }

    override fun SafeClientEvent.updateText() {
        displayText.add(NanoSenseMod.NAME, secondaryColor)
        displayText.add(NanoSenseMod.VERSION, primaryColor)
    }
}