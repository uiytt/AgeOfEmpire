package fr.uiytt.ageofempire.game

import fr.uiytt.ageofempire.base.TeamBase
import fr.uiytt.ageofempire.utils.ColorLink
import fr.uiytt.ageofempire.utils.PlayerFromUUIDNotFoundException
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * A team of players, and their base
 * Teams already exist before the game start, to allow players to join.
 * @param color [ColorLink] of the team
 * @param name Name of the team
 *
 * @see GameData for all the instances of the teams
 */
class GameTeam(val color: ColorLink, val name: String) {
    val playersUUIDs: MutableList<UUID> = ArrayList()
    lateinit var teamBase: TeamBase
        private set

    /**
     * At the start of game, assign a new [TeamBase]
     */
    fun registerTeamBase() {
        teamBase = TeamBase(this)
    }

    /**
     * This return only a COPY of the list of players, you cannot modify the players here,
     * @see .addPlayer
     * @see .removePlayer
     */
    fun getImmuablePlayersUUIDs(): List<UUID> {
        return playersUUIDs.toList()
    }

    /**
     * Add a player to the team
     * @param playerUUID if of the player, produce an error if the player is not online
     */
    @Throws(PlayerFromUUIDNotFoundException::class)
    fun addPlayer(playerUUID: UUID) {
        val player = Bukkit.getPlayer(playerUUID) ?: throw PlayerFromUUIDNotFoundException(playerUUID)
        player.setPlayerListName(color.tabColor.toString() + player.displayName)
        playersUUIDs.add(playerUUID)
        getGameManager().gameData.playersTeam[playerUUID] = this
    }

    fun removePlayer(playerUUID: UUID) {
        val player = Bukkit.getPlayer(playerUUID)
        player?.setPlayerListName(player.displayName)
        playersUUIDs.remove(playerUUID)
        getGameManager().gameData.playersTeam.remove(playerUUID)
    }

    /**
     * Remove all players from this team.
     */
    fun removeAllPlayers() {
        playersUUIDs.forEach { playersUUIDs.remove(it) }
        playersUUIDs.clear()
    }

    companion object {
        @JvmStatic
        fun removePlayerFromAllTeams(playerUUID: UUID) {
            playerUUID.getPlayerTeam()?.removePlayer(playerUUID)
        }

        /**
         * This register new teams depending on the number of player, the size of the teams etc...
         */
        @JvmStatic
        fun reorganizeTeam() {
            val numberTeam = if (Bukkit.getOnlinePlayers().size >= 16) 4 else 2
            if (numberTeam == getGameManager().gameData.teams.size) return
            getGameManager().gameData.teams.forEach {it.removeAllPlayers()}
            getGameManager().gameData.teams.clear()
            val colors: Array<ColorLink> = ColorLink.values()
            for (i in 0 until numberTeam) {
                val color: ColorLink = colors[i]
                getGameManager().gameData.teams.add(GameTeam(color, color.name))
            }
        }
    }
}

fun UUID.getPlayerTeam(): GameTeam? {
    return getGameManager().gameData.playersTeam[this]
}

fun Player.getPlayerTeam(): GameTeam? {
    return getGameManager().gameData.playersTeam[this.uniqueId]
}