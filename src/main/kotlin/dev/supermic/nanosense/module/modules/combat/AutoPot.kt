package dev.supermic.nanosense.module.modules.combat

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap
import dev.supermic.nanosense.event.SafeClientEvent
import dev.supermic.nanosense.event.events.PacketEvent
import dev.supermic.nanosense.event.events.WorldEvent
import dev.supermic.nanosense.event.events.player.OnUpdateWalkingPlayerEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.event.safeListener
import dev.supermic.nanosense.manager.managers.EntityManager
import dev.supermic.nanosense.manager.managers.HotbarManager.spoofHotbar
import dev.supermic.nanosense.manager.managers.PlayerPacketManager
import dev.supermic.nanosense.manager.managers.PlayerPacketManager.sendPlayerPacket
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.EntityUtils.isFakeOrSelf
import dev.supermic.nanosense.util.EntityUtils.isFriend
import dev.supermic.nanosense.util.TickTimer
import dev.supermic.nanosense.util.accessor.entityID
import dev.supermic.nanosense.util.interfaces.DisplayEnum
import dev.supermic.nanosense.util.inventory.InventoryTask
import dev.supermic.nanosense.util.inventory.confirmedOrTrue
import dev.supermic.nanosense.util.inventory.inventoryTask
import dev.supermic.nanosense.util.inventory.operation.swapWith
import dev.supermic.nanosense.util.inventory.slot.*
import dev.supermic.nanosense.util.math.vector.Vec2f
import dev.supermic.nanosense.util.world.getGroundLevel
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.EntityPotion
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.inventory.Slot
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.network.play.server.SPacketDestroyEntities
import net.minecraft.network.play.server.SPacketEntityStatus
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtils
import net.minecraft.util.EnumHand
import kotlin.math.abs
import kotlin.math.sqrt

internal object AutoPot : Module(
    name = "AutoPot",
    description = "药水哥",
    category = Category.COMBAT,
    modulePriority = 100
) {
    private val slot by setting("Slot", 7, 1..9, 1)
    private val heal by setting("Heal", true)
    private val healHealth by setting("Heal Health", 12.0f, 0.0f..20.0f, 0.5f, ::heal)
    private val healDelay by setting("Heal Delay", 500, 0..10000, 50, ::heal)
    private val weakness by setting("Weakness", false)
    private val weaknessRange by setting("Weakness Range", 1.8f, 0.0f..4.0f, 0.1f, ::weakness)
    private val weaknessDelay by setting("Weakness Delay", 1500, 0..10000, 50, ::weakness)
    private val speed by setting("Speed", true)
    private val speedDelay by setting("Speed Delay", 5000, 0..10000, 50, ::speed)

    private val weaknessTimeMap = Int2LongOpenHashMap().apply { defaultReturnValue(0x22) }

    private var hotbarSlot: HotbarSlot? = null
    private var lastTask: InventoryTask? = null
    private var potionType = PotionType.NONE

    override fun getHudInfo(): String {
        return potionType.displayString
    }

    init {
        onDisable {
            hotbarSlot = null
            lastTask = null
            potionType = PotionType.NONE
        }

        listener<PacketEvent.PostReceive> {
            when (it.packet) {
                is SPacketDestroyEntities -> {
                    it.packet.entityIDs.forEach(weaknessTimeMap::remove)
                }
                is SPacketEntityStatus -> {
                    if (it.packet.opCode.toInt() == 35) {
                        weaknessTimeMap.remove(it.packet.entityID)
                    }
                }
            }
        }

        safeListener<WorldEvent.Entity.Remove> { event ->
            if (event.entity is EntityPotion) {
                val effect = PotionUtils.getEffectsFromStack(event.entity.potion)
                    .firstOrNull { it.potion == MobEffects.WEAKNESS } ?: return@safeListener

                val box = event.entity.entityBoundingBox.grow(4.0, 2.0, 4.0)

                EntityManager.players.asSequence()
                    .filter { it.isEntityAlive }
                    .filterNot { it.isFakeOrSelf }
                    .filterNot { it.isFriend }
                    .filter { box.intersects(it.entityBoundingBox) }
                    .forEach {
                        val distSq = event.entity.getDistanceSq(it)

                        if (distSq < 16.0) {
                            val factor = sqrt(distSq) * 0.75
                            val duration = (factor * effect.duration + 0.5)
                            weaknessTimeMap[it.entityId] = System.currentTimeMillis() + duration.toLong() * 50L
                        }
                    }
            }
        }

        safeListener<OnUpdateWalkingPlayerEvent.Pre> {
            if (!groundCheck()) {
                potionType = PotionType.NONE
                hotbarSlot = null
                return@safeListener
            }

            if (lastTask.confirmedOrTrue && potionType == PotionType.NONE) {
                potionType = PotionType.values().first {
                    it.check(this)
                }
            }

            hotbarSlot = if (potionType != PotionType.NONE) {
                getSlot(potionType)?.also {
                    sendPlayerPacket {
                        rotate(Vec2f(player.rotationYaw, 90.0f))
                    }
                }
            } else {
                null
            }
        }

        safeListener<OnUpdateWalkingPlayerEvent.Post> {
            hotbarSlot?.let {
                if (PlayerPacketManager.prevRotation.y > 85.0f && PlayerPacketManager.rotation.y > 85.0f) {
                    spoofHotbar(it) {
                        connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                        potionType.timer.reset()
                        potionType = PotionType.NONE
                    }
                }
            }
        }
    }

    private fun SafeClientEvent.groundCheck(): Boolean {
        return player.onGround
            || player.posY - world.getGroundLevel(player) < 3.0
    }

    private fun SafeClientEvent.getSlot(potionType: PotionType): HotbarSlot? {
        return player.hotbarSlots.findPotion(potionType)
            ?: run {
                player.inventorySlots.findPotion(potionType)?.let {
                    inventoryTask {
                        swapWith(it, player.hotbarSlots[slot - 1])
                        delay(0L)
                        postDelay(0L)
                    }
                }
                null
            }
    }

    private fun <T : Slot> List<T>.findPotion(potionType: PotionType): T? {
        return this.firstItem(Items.SPLASH_POTION) { itemStack ->
            PotionUtils.getEffectsFromStack(itemStack).any { it.potion == potionType.potion }
        }
    }

    private enum class PotionType(override val displayName: CharSequence, val potion: Potion) : DisplayEnum {
        INSTANT_HEALTH("Heal", MobEffects.INSTANT_HEALTH) {
            override fun check(event: SafeClientEvent): Boolean {
                return heal
                    && timer.tick(healDelay)
                    && event.player.health <= healHealth
                    && super.check(event)
            }
        },

        WEAKNESS("Weakness", MobEffects.WEAKNESS) {
            override fun check(event: SafeClientEvent): Boolean {
                return weakness
                    && timer.tick(weaknessDelay)
                    && super.check(event)
                    && EntityManager.players.asSequence()
                    .filterNot { it.isFriend }
                    .filterNot { it.isFakeOrSelf }
                    .filter { event.isInRange(it) }
                    .any { !weaknessTimeMap.containsKey(it.entityId) }
            }

            private fun SafeClientEvent.isInRange(entity: Entity): Boolean {
                return abs(player.posX - entity.posX) <= 4.125
                    && abs(player.posY - entity.posY) <= 2.125
                    && abs(player.posZ - entity.posZ) <= 4.125
                    && player.getDistanceSq(entity) <= weaknessRange * weaknessRange
            }
        },

        SPEED("Speed", MobEffects.SPEED) {
            override fun check(event: SafeClientEvent): Boolean {
                return speed
                    && timer.tick(speedDelay)
                    && !event.player.isPotionActive(MobEffects.SPEED)
                    && super.check(event)
            }
        },

        NONE("", MobEffects.LUCK) {
            override fun check(event: SafeClientEvent): Boolean {
                return true
            }
        };

        val timer = TickTimer()

        open fun check(event: SafeClientEvent): Boolean {
            return event.player.inventorySlots.hasItem(Items.SPLASH_POTION) { itemStack ->
                PotionUtils.getEffectsFromStack(itemStack).any { it.potion == potion }
            }
        }
    }
}