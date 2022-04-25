package fr.uiytt.ageofempire

import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.game.isRunning
import fr.uiytt.ageofempire.gui.TeamGui
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class Command: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "start" -> {
                if (getGameManager().isRunning()) sender.sendMessage("déjà commencé")
                else {
                    getGameManager().init(Bukkit.getServer().onlinePlayers.toList())
                    Bukkit.broadcastMessage("ça commence")
                }
            }
            "stop" -> {
                if (!getGameManager().isRunning()) sender.sendMessage("pas commencé")
                else {
                    getGameManager().stopGame()
                    Bukkit.broadcastMessage("ça stop")
                }
            }
            "team" -> {
                if (sender !is Player || getGameManager().isRunning()) return false
                TeamGui().openGUI(sender)
            }
            "test" -> {
                if (sender !is Player || getGameManager().isRunning()) return false
                val playerTeam: GameTeam = sender.uniqueId.getPlayerTeam()!!
                playerTeam.teamBase.age += 1
            }
            "force" -> {
                if (!getGameManager().isRunning()) return false
                if(args[1].equals("pvp", ignoreCase = true)) {
                    getGameManager().enablePVP()
                } else if(args[1].equals("assaults", ignoreCase = true)) {
                    getGameManager().enableAssaults()
                }
            }
        }
        sendHelp(sender)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        TODO("Not yet implemented")
    }

    private fun sendHelp(player: CommandSender) {
        player.sendMessage("HELP")
    }
}