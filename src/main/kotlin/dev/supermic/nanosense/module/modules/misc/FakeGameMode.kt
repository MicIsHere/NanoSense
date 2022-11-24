package dev.supermic.nanosense.module.modules.misc

import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.safeParallelListener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.interfaces.DisplayEnum
import dev.supermic.nanosense.util.threads.runSafe
import net.minecraft.world.GameType

internal object FakeGameMode : Module(
    name = "FakeGameMode",
    description = "Fakes your current gamemode client side",
    category = Category.MISC
) {
    private val gamemode by setting("Mode", GameMode.CREATIVE)

    @Suppress("UNUSED")
    private enum class GameMode(override val displayName: CharSequence, val gameType: GameType) : DisplayEnum {
        SURVIVAL("Survival", GameType.SURVIVAL),
        CREATIVE("Creative", GameType.CREATIVE),
        ADVENTURE("Adventure", GameType.ADVENTURE),
        SPECTATOR("Spectator", GameType.SPECTATOR)
    }

    override fun getHudInfo(): String {
        return gamemode.displayString
    }

    private var prevGameMode: GameType? = null

    init {
        safeParallelListener<TickEvent.Pre> {
            playerController.setGameType(gamemode.gameType)
        }

        onEnable {
            runSafe {
                prevGameMode = playerController.currentGameType
            } ?: disable()
        }

        onDisable {
            runSafe {
                prevGameMode?.let { playerController.setGameType(it) }
            }
        }
    }
}