package fr.uiytt.ageofempire.game

import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.game.GameScoreboard.Companion.updateAssaultTimer
import fr.uiytt.ageofempire.game.GameScoreboard.Companion.updateGlobalTimer
import fr.uiytt.ageofempire.game.GameScoreboard.Companion.updatePvpTimer
import fr.uiytt.ageofempire.getConfigManager
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class ThreadEverySecond {
    /**
     * Thread run every second that time the game and handle things like pvp timer and other.
     * The thread automatically stop itself when the GameManager instance is no longer active.
     * @param plugin Instance of the plugin
     * @param game Current instance of [GameManager]
     */
    fun init(plugin: Plugin?, game: GameManager) {
        SecondRunnable(game).runTaskTimer(plugin!!, 1, 20)
    }

    private class SecondRunnable(_game: GameManager) : BukkitRunnable() {
        private val pvpTimer: Int = getConfigManager().pvpTimer
        private val assaultTimer: Int = getConfigManager().assaultTimer
        private val game: GameManager = _game
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
            updateGlobalTimer(secondFromStart)

            //Update pvp timer
            if (secondFromStart <= pvpTimer) {
                updatePvpTimer(pvpTimer - secondFromStart)
                if (secondFromStart == pvpTimer - 5) {
                    game.enablePVP()
                }
            }

            //Update assault timer and events
            if (secondFromStart <= assaultTimer) {
                updateAssaultTimer(assaultTimer - secondFromStart)
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
            }
        }
    }
}