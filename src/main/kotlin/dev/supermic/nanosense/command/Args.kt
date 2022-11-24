package dev.supermic.nanosense.command

import kotlinx.coroutines.Dispatchers
import dev.supermic.nanosense.command.args.AbstractArg
import dev.supermic.nanosense.command.args.AutoComplete
import dev.supermic.nanosense.command.args.DynamicPrefixMatch
import dev.supermic.nanosense.command.args.StaticPrefixMatch
import dev.supermic.nanosense.gui.GuiManager
import dev.supermic.nanosense.gui.hudgui.AbstractHudElement
import dev.supermic.nanosense.manager.managers.UUIDManager
import dev.supermic.nanosense.module.AbstractModule
import dev.supermic.nanosense.module.ModuleManager
import dev.supermic.nanosense.util.BaritoneUtils
import dev.supermic.nanosense.util.PlayerProfile
import dev.supermic.nanosense.util.TimeUnit
import dev.supermic.nanosense.util.Wrapper
import dev.supermic.nanosense.util.delegate.AsyncCachedValue
import dev.supermic.nanosense.util.delegate.CachedValueN
import dev.supermic.nanosense.util.threads.runSafe
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import java.io.File

class ModuleArg(
    override val name: String
) : AbstractArg<AbstractModule>(), AutoComplete by DynamicPrefixMatch(::allAlias) {
    override suspend fun convertToType(string: String?): AbstractModule? {
        return ModuleManager.getModuleOrNull(string)
    }

    private companion object {
        val allAlias by CachedValueN(5000L) {
            ModuleManager.modules.asSequence()
                .flatMap {
                    sequence {
                        yield(it.name.toString())
                        it.alias.forEach {
                            yield(it)
                        }
                    }
                }
                .sorted()
                .toList()
        }
    }

}

class HudElementArg(
    override val name: String
) : AbstractArg<AbstractHudElement>(), AutoComplete by DynamicPrefixMatch(::allAlias) {
    override suspend fun convertToType(string: String?): AbstractHudElement? {
        return GuiManager.getHudElementOrNull(string)
    }

    private companion object {
        val allAlias by CachedValueN(5000L) {
            GuiManager.hudElements.asSequence()
                .flatMap {
                    sequence {
                        yield(it.name.toString())
                        it.alias.forEach {
                            yield(it.toString())
                        }
                    }
                }
                .sorted()
                .toList()
        }
    }
}

class BlockPosArg(
    override val name: String
) : AbstractArg<BlockPos>(), AutoComplete by DynamicPrefixMatch(::playerPosString) {

    override suspend fun convertToType(string: String?): BlockPos? {
        if (string == null) return null

        val splitInts = string.split(',').mapNotNull { it.toIntOrNull() }
        if (splitInts.size != 3) return null

        return BlockPos(splitInts[0], splitInts[1], splitInts[2])
    }

    private companion object {
        val playerPosString: List<String>?
            get() = Wrapper.player?.position?.let { listOf("${it.x},${it.y},${it.z}") }
    }

}

class BlockArg(
    override val name: String
) : AbstractArg<Block>(), AutoComplete by StaticPrefixMatch(allBlockNames) {

    override suspend fun convertToType(string: String?): Block? {
        if (string == null) return null
        return Block.getBlockFromName(string)
    }

    private companion object {
        val allBlockNames = ArrayList<String>().apply {
            Block.REGISTRY.keys.forEach {
                add(it.toString())
                add(it.path)
            }
            sort()
        }
    }
}

class BaritoneBlockArg(
    override val name: String
) : AbstractArg<Block>(), AutoComplete by StaticPrefixMatch(baritoneBlockNames) {

    override suspend fun convertToType(string: String?): Block? {
        if (string == null) return null
        return Block.getBlockFromName(string)
    }

    private companion object {
        val baritoneBlockNames = ArrayList<String>().apply {
            BaritoneUtils.baritoneCachedBlocks.forEach { block ->
                block.registryName?.let {
                    add(it.toString())
                    add(it.path)
                }
            }
            sort()
        }
    }
}

class SchematicArg(
    override val name: String
) : AbstractArg<File>(), AutoComplete by DynamicPrefixMatch(::schematicFiles) {

    override suspend fun convertToType(string: String?): File? {
        if (string == null) return null

        val nameWithoutExt = string.removeSuffix(".schematic")
        val file = File("schematics").listFiles()?.filter {
            it.exists() && it.isFile && it.name.equals("$nameWithoutExt.schematic", true)
        } // this stupid find and search is required because ext4 is case sensitive (Linux)

        return file?.firstOrNull()
    }

    private companion object {
        val schematicFolder = File("schematics")

        val schematicFiles by AsyncCachedValue(5L, TimeUnit.SECONDS, Dispatchers.IO) {
            schematicFolder.listFiles()?.map { it.name } ?: emptyList<String>()
        }
    }
}

class ItemArg(
    override val name: String
) : AbstractArg<Item>(), AutoComplete by StaticPrefixMatch(allItemNames) {

    override suspend fun convertToType(string: String?): Item? {
        if (string == null) return null
        return Item.getByNameOrId(string)
    }

    private companion object {
        val allItemNames = ArrayList<String>().run {
            Item.REGISTRY.keys.forEach {
                add(it.toString())
                add(it.path)
            }
            sorted()
        }
    }

}

class PlayerArg(
    override val name: String
) : AbstractArg<PlayerProfile>(), AutoComplete by DynamicPrefixMatch(::playerInfoMap) {

    override suspend fun checkType(string: String?): Boolean {
        return !string.isNullOrBlank()
    }

    override suspend fun convertToType(string: String?): PlayerProfile? {
        return UUIDManager.getByString(string)
    }

    private companion object {
        val playerInfoMap by CachedValueN(3000L) {
            runSafe {
                connection.playerInfoMap.asSequence()
                    .map { it.gameProfile.name }
                    .sorted()
                    .toList()
            }
        }
    }

}