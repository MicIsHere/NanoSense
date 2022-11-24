package dev.supermic.nanosense.module.modules.client

import dev.supermic.nanosense.event.events.GuiEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.gui.hudgui.NanoHudGui
import dev.supermic.nanosense.module.AbstractModule
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.setting.GenericConfig
import dev.supermic.nanosense.translation.TranslationManager
import dev.supermic.nanosense.util.Wrapper
import net.minecraft.client.gui.GuiMainMenu

internal object Language : AbstractModule(
    name = "Language",
    description = "Change language",
    category = Category.CLIENT,
    alwaysEnabled = true,
    visible = false,
    config = GenericConfig
) {
    private val overrideLanguage = setting("Override Language", false)
    private val language = setting("Language", "en_us", { overrideLanguage.value })

    init {
        listener<GuiEvent.Displayed>(114514) {
            if (it.screen is GuiMainMenu || it.screen is NanoHudGui) {
                TranslationManager.reload()
            }
        }
    }

    val settingLanguage: String
        get() = if (overrideLanguage.value) {
            language.value
        } else {
            Wrapper.minecraft.gameSettings.language
        }

    init {
        overrideLanguage.listeners.add {
            TranslationManager.reload()
        }

        language.listeners.add {
            TranslationManager.reload()
        }
    }
}