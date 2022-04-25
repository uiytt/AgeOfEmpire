package fr.uiytt.ageofempire.game

import java.util.*

class GameData {
    var isGameRunning = false
    var isPvp = false
    var isAssaults = false
    var alivePlayers: List<UUID> = ArrayList()
    val gold = HashMap<UUID, Int>()
    val teams: MutableList<GameTeam> = ArrayList()
    /**
     * @return HashMap of team of each player
     */
    val playersTeam = HashMap<UUID, GameTeam>()

    /**
     * Add a certain ammount of gold to a player safely
     * @param playerUUID the player's [UUID]
     * @param amount the ammount of gold to add.
     * @return the new gold ammount
     */
    fun addGold(playerUUID: UUID, amount: Int): Int {
        val gold = gold[playerUUID]?: 0;
        this.gold[playerUUID] = amount + gold
        return amount
    }
}

fun GameManager.isRunning(): Boolean {
    return this.gameData.isGameRunning
}