package dev.supermic.nanosense.module.modules.client

import dev.supermic.nanosense.event.events.ShutdownEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.gui.clickgui.NanoClickGui
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.threads.onMainThreadSafe
import org.lwjgl.input.Keyboard

internal object ClickGUI : Module(
    name = "ClickGUI",
    description = "Opens the Click GUI",
    category = Category.CLIENT,
    visible = false,
    alwaysListening = true
) {
    init {
        listener<ShutdownEvent> {
            disable()
        }

        onEnable {
            onMainThreadSafe {
                if (mc.currentScreen !is NanoClickGui) {
                    HudEditor.disable()
                    mc.displayGuiScreen(NanoClickGui)
                    NanoClickGui.onDisplayed()
                }
            }
        }

        onDisable {
            onMainThreadSafe {
                if (mc.currentScreen is NanoClickGui) {
                    mc.displayGuiScreen(null)
                }
            }
        }

        bind.value.setBind(Keyboard.KEY_Y)
    }
}
