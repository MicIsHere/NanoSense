package dev.supermic.nanosense.module.modules.client

import dev.supermic.nanosense.event.events.ShutdownEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.gui.hudgui.NanoHudGui
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.threads.onMainThreadSafe

internal object HudEditor : Module(
    name = "HudEditor",
    description = "Edits the Hud",
    category = Category.CLIENT,
    visible = false
) {
    init {
        onEnable {
            onMainThreadSafe {
                if (mc.currentScreen !is NanoHudGui) {
                    ClickGUI.disable()
                    mc.displayGuiScreen(NanoHudGui)
                    NanoHudGui.onDisplayed()
                }
            }
        }

        onDisable {
            onMainThreadSafe {
                if (mc.currentScreen is NanoHudGui) {
                    mc.displayGuiScreen(null)
                }
            }
        }

        listener<ShutdownEvent> {
            disable()
        }
    }
}
