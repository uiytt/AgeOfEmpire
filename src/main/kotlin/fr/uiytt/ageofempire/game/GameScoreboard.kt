package fr.uiytt.ageofempire.game

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.Scoreboard
import java.util.*
import kotlin.math.floor

class GameScoreboard(playerUUID: UUID) {
    private val scoreboard: Scoreboard
    private val scorelist: MutableList<Score> = ArrayList()

    /**
     * A scoreboard for the game for each player
     */
    init {
        val scoreboardManager = Bukkit.getScoreboardManager()
        scoreboard = scoreboardManager!!.newScoreboard
        playersScoreboard[playerUUID] = this //Store the scoreboard instance
    }

    //Based on https://www.spigotmc.org/wiki/making-scoreboard-with-teams-no-flicker/ 
    fun createScoreboard(player: Player) {
        //Title
        val obj = scoreboard.registerNewObjective(
            "title",
            "dummy",
            ChatColor.DARK_GRAY.toString() + "»" + ChatColor.YELLOW + "" + ChatColor.BOLD + "AgeOfEmpire" + ChatColor.DARK_GRAY + "«"
        )
        obj.displaySlot = DisplaySlot.SIDEBAR

        //Timer
        scorelist.add(obj.getScore(ChatColor.MAGIC.toString() + "" + ChatColor.GRAY))
        scorelist.add(obj.getScore(ChatColor.RED.toString() + "" + ChatColor.BOLD + "      Timer"))
        val timeCounter = scoreboard.registerNewTeam("AOE_Timer")
        timeCounter.addEntry(ChatColor.AQUA.toString() + "")
        timeCounter.prefix = ChatColor.GRAY.toString() + "Timer: " + ChatColor.RED + "00:00"
        scorelist.add(obj.getScore(ChatColor.AQUA.toString() + ""))
        val pvpCounter = scoreboard.registerNewTeam("AOE_pvp")
        pvpCounter.addEntry(ChatColor.BLACK.toString() + "")
        pvpCounter.prefix = ChatColor.GRAY.toString() + "Pvp: " + ChatColor.RED + "00:00"
        scorelist.add(obj.getScore(ChatColor.BLACK.toString() + ""))
        val assaultCounter = scoreboard.registerNewTeam("AOE_assault")
        assaultCounter.addEntry(ChatColor.BOLD.toString() + "")
        assaultCounter.prefix = ChatColor.GRAY.toString() + "Assaut: " + ChatColor.RED + "00:00"
        scorelist.add(obj.getScore(ChatColor.BOLD.toString() + ""))

        //Ressources
        scorelist.add(obj.getScore(ChatColor.MAGIC.toString() + "" + ChatColor.BOLD))
        scorelist.add(obj.getScore(ChatColor.GREEN.toString() + "" + ChatColor.BOLD + "  Ressources"))
        val stoneAmmount = scoreboard.registerNewTeam("AOE_stone")
        stoneAmmount.addEntry(ChatColor.BLUE.toString() + "")
        stoneAmmount.prefix = ChatColor.GRAY.toString() + "Pierre: " + ChatColor.RESET + "" + ChatColor.YELLOW + "0"
        scorelist.add(obj.getScore(ChatColor.BLUE.toString() + ""))
        val woodAmmount = scoreboard.registerNewTeam("AOE_wood")
        woodAmmount.addEntry(ChatColor.DARK_BLUE.toString() + "")
        woodAmmount.prefix = ChatColor.GRAY.toString() + "Bois: " + ChatColor.RESET + "" + ChatColor.YELLOW + "0"
        scorelist.add(obj.getScore(ChatColor.DARK_BLUE.toString() + ""))
        val goldAmmount = scoreboard.registerNewTeam("AOE_gold")
        goldAmmount.addEntry(ChatColor.DARK_GRAY.toString() + "")
        goldAmmount.prefix =
            ChatColor.GRAY.toString() + "" + ChatColor.BOLD + "Or: " + ChatColor.RESET + "" + ChatColor.YELLOW + "0"
        scorelist.add(obj.getScore(ChatColor.DARK_GRAY.toString() + ""))
        var n = scorelist.size //Set the number of each line of the scoreboard
        for (score in scorelist) {
            score.score = n
            n--
        }
        player.scoreboard = scoreboard
    }

    fun updateStoneAmmount(stoneAmmount: Int) {
        val stoneCounter = scoreboard.getTeam("AOE_stone") ?: return
        stoneCounter.prefix =
            ChatColor.GREEN.toString() + "" + ChatColor.BOLD + "Pierre: " + ChatColor.RESET + "" + ChatColor.GRAY + stoneAmmount
    }

    fun updateWoodAmmount(woodAmmount: Int) {
        val woodCounter = scoreboard.getTeam("AOE_wood") ?: return
        woodCounter.prefix =
            ChatColor.GREEN.toString() + "" + ChatColor.BOLD + "Bois: " + ChatColor.RESET + "" + ChatColor.GRAY + woodAmmount
    }

    fun updateGoldAmmount(goldAmmount: Int) {
        val borderCounter = scoreboard.getTeam("AOE_gold") ?: return
        borderCounter.prefix =
            ChatColor.YELLOW.toString() + "" + ChatColor.BOLD + "Or: " + ChatColor.RESET + "" + ChatColor.GRAY + goldAmmount
    }

    private fun updateScores() {
        var n = scorelist.size
        for (score in scorelist) {
            val obj = scoreboard.getObjective(DisplaySlot.SIDEBAR)!!
            obj.getScore(score.entry).score = n
            n--
        }
    }

    companion object {
        @JvmStatic
        val playersScoreboard = HashMap<UUID, GameScoreboard>()
        @JvmStatic
        fun updateGlobalTimer(time: Int) {
            playersScoreboard.forEach { (_: UUID?, gameScoreboard: GameScoreboard) ->
                val timeCounter = gameScoreboard.scoreboard.getTeam("AOE_Timer") ?: return@forEach
                timeCounter.prefix = ChatColor.GRAY.toString() + "Timer: " + ChatColor.RED + intToTime(time)
            }
        }

        @JvmStatic
        fun updatePvpTimer(time: Int) {
            playersScoreboard.forEach { (_: UUID?, gameScoreboard: GameScoreboard) ->
                val pvpCounter = gameScoreboard.scoreboard.getTeam("AOE_pvp") ?: return@forEach
                if (time != -1) {
                    pvpCounter.prefix = ChatColor.GRAY.toString() + "Pvp: " + ChatColor.RED + intToTime(time)
                    return@forEach
                }
                gameScoreboard.scorelist.removeIf { l: Score -> l.entry == ChatColor.BLACK.toString() + "" }
                gameScoreboard.scoreboard.resetScores(ChatColor.BLACK.toString() + "")
                gameScoreboard.updateScores()
            }
        }

        @JvmStatic
		fun updateAssaultTimer(time: Int) {
            playersScoreboard.forEach { (_: UUID?, gameScoreboard: GameScoreboard) ->
                val assaultTimer = gameScoreboard.scoreboard.getTeam("AOE_assault") ?: return@forEach
                if (time != -1) {
                    assaultTimer.prefix = ChatColor.GRAY.toString() + "Assaut: " + ChatColor.RED + intToTime(time)
                    return@forEach
                }
                gameScoreboard.scorelist.removeIf { l: Score -> l.entry == ChatColor.BOLD.toString() + "" }
                gameScoreboard.scoreboard.resetScores(ChatColor.BOLD.toString() + "")
                gameScoreboard.updateScores()
            }
        }

        @JvmStatic
        fun removePlayerScoreboard(player: Player) {
            val scoreboardManager = Bukkit.getScoreboardManager()
            player.scoreboard = scoreboardManager!!.newScoreboard
        }
    }
}

private fun intToTime(_time: Int): String {
    var time = _time
    var hour = ""
    var min = "00"
    var sec = ""
    if (time > 59) {
        var temp: Int
        if (time > 3599) {
            temp = floor((time / 3600f).toDouble()).toInt()
            hour = temp.toString()
            hour += ":"
            time -= temp * 3600
        }
        temp = floor((time / 60f).toDouble()).toInt()
        min = temp.toString()
        time -= temp * 60
    }
    if (time < 10) sec = "0"
    sec += time.toString()
    return "$hour$min:$sec"
}