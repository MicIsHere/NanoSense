package dev.supermic.nanosense.module.modules.combat

import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.safeConcurrentListener
import dev.supermic.nanosense.manager.managers.CombatManager
import dev.supermic.nanosense.manager.managers.HoleManager
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.module.modules.player.PacketMine
import dev.supermic.nanosense.util.EntityUtils.betterPosition
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.interfaces.DisplayEnum
import dev.supermic.nanosense.util.world.getBlock
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

internal object FootMiner : Module(
    name = "FootMiner",
    category = Category.COMBAT,
    description = "Trolling",
) {
    private val mode by setting("Mode",mineMode.ALL )
    private val toggle by setting("Auto Disable", true)
    private val timeout by setting("Timeout", 5000, 0..10000, 100)
    private val timeoutTimer = TickTimer()
    private var lastPos: BlockPos? = null

    private enum class mineMode(override val displayName: String) : DisplayEnum {
        ALL("All"),
        LITE("Lite")
    }

    init {
        onEnable {
            enable()
        }

        onDisable {
            FootMiner.reset()
        }

        safeConcurrentListener<TickEvent.Post> {
            CombatManager.target?.let { target ->
                val pos = target.betterPosition
                if (pos == player.betterPosition) return@safeConcurrentListener
                val burrow = Burrow.isBurrowed(target)
                val isHole = HoleManager.getHoleInfo(pos).isHole
                val targetFeetPos = BlockPos(target.posX, target.posY, target.posZ)

                if (isHole) {
                    val priority = if (burrow || pos == FootMiner.lastPos) 80 else -100
                    if(world.getBlock(targetFeetPos.add(0,1,2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.BEDROCK){
                        PacketMine.mineBlock(FootMiner,targetFeetPos.add(0,0,1),priority)

                    }else if(world.getBlock(targetFeetPos.add(0,1,-2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) != Blocks.BEDROCK){
                        PacketMine.mineBlock(FootMiner,targetFeetPos.add(0,0,-1),priority)

                    }else if(world.getBlock(targetFeetPos.add(2,1,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(1,0,0)) != Blocks.AIR && world.getBlock(targetFeetPos.add(2,0,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(-1,0,0)) != Blocks.BEDROCK){
                        PacketMine.mineBlock(FootMiner,targetFeetPos.add(1,0,0),priority)

                    }else if(world.getBlock(targetFeetPos.add(-2,1,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(-1,0,0)) != Blocks.AIR && world.getBlock(targetFeetPos.add(-2,0,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(-1,0,0)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(-1, 0, -1), priority)

                    }else if(world.getBlock(targetFeetPos.add(2,1,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(2,0,0)) != Blocks.AIR && world.getBlock(targetFeetPos.add(1,0,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(2,0,0)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(-1, 0, -1), priority)

                    }else if(world.getBlock(targetFeetPos.add(-2,1,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(-2,0,0)) != Blocks.AIR && world.getBlock(targetFeetPos.add(-1,0,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(-2,0,0)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(-2, 0, -1), priority)

                    }else if(world.getBlock(targetFeetPos.add(1,-2,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-2)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(0,0,-2)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(-2, 0, -1), priority)

                    }else if(world.getBlock(targetFeetPos.add(0,1,2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,2)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(0,0,2)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(-2, 0, -1), priority)

                    }else if(world.getBlock(targetFeetPos.add(2,1,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(1,0,0)) != Blocks.AIR && world.getBlock(targetFeetPos.add(2,0,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(1,0,0)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(2,0,0)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(2, 0, 0), priority)

                    }else if(world.getBlock(targetFeetPos.add(-2,1,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(-1,0,0)) != Blocks.AIR && world.getBlock(targetFeetPos.add(-2,0,0)) == Blocks.AIR && world.getBlock(targetFeetPos.add(-1,0,0)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(-2,0,0)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(-2, 0, 0), priority)

                    }else if(world.getBlock(targetFeetPos.add(0,1,-2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(0,0,-2)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(0, 0, -2), priority)

                    }else if(world.getBlock(targetFeetPos.add(0,1,-2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,-1)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(0,0,-2)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(0, 0, -1), priority)

                    }else if(world.getBlock(targetFeetPos.add(0,1,2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(0,0,2)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(0, 0, 2), priority)

                    }else if(world.getBlock(targetFeetPos.add(0,1,2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,2)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.BEDROCK && world.getBlock(targetFeetPos.add(0,0,2)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(0, 0, 1), priority)

                    }else if(world.getBlock(targetFeetPos.add(0,2,1)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,1,1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,1,1)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(0, 1, 1), priority)

                    }else if(world.getBlock(targetFeetPos.add(0,2,1)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.AIR && world.getBlock(targetFeetPos.add(0,1,1)) == Blocks.AIR && world.getBlock(targetFeetPos.add(0,0,1)) != Blocks.BEDROCK) {
                        PacketMine.mineBlock(FootMiner, targetFeetPos.add(0, 0, 1), priority)
                    }
                        FootMiner.timeoutTimer.reset()
                    FootMiner.lastPos = pos
                } else if (pos != FootMiner.lastPos || FootMiner.timeoutTimer.tick(FootMiner.timeout)) {
                    FootMiner.reset()
                }
            } ?: FootMiner.reset()
        }
    }

    private fun reset() {
        PacketMine.reset(this)
    }

}