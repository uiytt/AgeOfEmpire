package fr.uiytt.ageofempire.game

import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.getConfigManager
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * Thread run every second that time the game and handle things like pvp timer and other.
 * The thread automatically stop itself when the [GameManager] instance is no longer active.
 */
class SecondRunnable: BukkitRunnable() {
    private val pvpTimer: Int = getConfigManager().pvpTimer
    private val assaultTimer: Int = getConfigManager().assaultTimer
    private val game: GameManager = getGameManager()
    private var secondFromStart = 0
    private var timeMinutes = 0

    override fun run() {
        //Get gamedata and stop game if the game is not running
        val gamedata = game.gameData
        if (!gamedata.isGameRunning) {
            cancel()
            return
        }

        //Increment timer
        secondFromStart += 1
        GameScoreboard.updateGlobalTimer(secondFromStart)

        //Update pvp timer
        if (secondFromStart <= pvpTimer) {
            GameScoreboard.updatePvpTimer(pvpTimer - secondFromStart)
            if (secondFromStart == pvpTimer - 5) {
                game.enablePVP()
            }
        }

        //Update assault timer and events
        if (secondFromStart <= assaultTimer) {
            GameScoreboard.updateAssaultTimer(assaultTimer - secondFromStart)
            if (secondFromStart == assaultTimer - 5) {
                game.enableAssaults()
            }
        }
        timeMinutes += 1
        if (timeMinutes != 60) return
        timeMinutes = 0

        for (team in gamedata.teams) {
            val buildings = team.teamBase.builds
            if (buildings[BuildingType.MINE]!!.isConstructed) {
                team.teamBase.stone += 20
                team.teamBase.updateTeamScoreboard()
            }
            if (buildings[BuildingType.SAWMILL]!!.isConstructed) {
                team.teamBase.wood += 20
                team.teamBase.updateTeamScoreboard()
            }
            if (buildings[BuildingType.BANK]!!.isConstructed) {
                team.playersUUIDs.forEach {
                    gamedata.addGold(it, 20)
                }
                team.teamBase.updateTeamScoreboard()
            }
            if (buildings[BuildingType.TRAINING_CAMP]!!.isConstructed) {
                team.playersUUIDs.forEach {
                    val player = Bukkit.getPlayer(it)
                    player?.exp = (player?.exp ?: 0f) + 1
                }
            }
        }
    }
}