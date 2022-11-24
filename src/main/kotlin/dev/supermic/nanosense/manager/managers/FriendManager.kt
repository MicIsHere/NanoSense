package dev.supermic.nanosense.manager.managers

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.manager.Manager
import dev.supermic.nanosense.util.ConfigUtils
import dev.supermic.nanosense.util.PlayerProfile
import dev.supermic.nanosense.util.extension.synchronized
import java.io.File
import java.io.FileWriter

object FriendManager : Manager() {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File("${NanoSenseMod.DIRECTORY}/friends.json")

    private var friendFile = FriendFile()
    val friends = HashMap<String, PlayerProfile>().synchronized()

    val empty get() = friends.isEmpty()
    var enabled = friendFile.enabled
        set(value) {
            field = value
            friendFile.enabled = value
        }

    fun isFriend(name: String) = friendFile.enabled && friends.contains(name.lowercase())

    fun addFriend(name: String) = UUIDManager.getByName(name)?.let {
        friendFile.friends.add(it)
        friends[it.name.lowercase()] = it
        true
    } ?: false

    fun removeFriend(name: String) = friendFile.friends.remove(friends.remove(name.lowercase()))

    fun clearFriend() {
        friends.clear()
        friendFile.friends.clear()
    }

    fun loadFriends(): Boolean {
        ConfigUtils.fixEmptyJson(file)

        return try {
            friendFile = gson.fromJson(file.readText(), object : TypeToken<FriendFile>() {}.type)
            friends.clear()
            friends.putAll(friendFile.friends.associateBy { it.name.lowercase() })
            NanoSenseMod.logger.info("Friend loaded")
            true
        } catch (e: Exception) {
            NanoSenseMod.logger.warn("Failed loading friends", e)
            false
        }
    }

    fun saveFriends(): Boolean {
        return try {
            FileWriter(file, false).buffered().use {
                gson.toJson(friendFile, it)
            }
            NanoSenseMod.logger.info("Friends saved")
            true
        } catch (e: Exception) {
            NanoSenseMod.logger.warn("Failed saving friends", e)
            false
        }
    }

    data class FriendFile(
        var enabled: Boolean = true,
        val friends: MutableSet<PlayerProfile> = LinkedHashSet<PlayerProfile>().synchronized()
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FriendFile) return false

            if (enabled != other.enabled) return false
            if (friends != other.friends) return false

            return true
        }

        override fun hashCode(): Int {
            var result = enabled.hashCode()
            result = 31 * result + friends.hashCode()
            return result
        }
    }
}