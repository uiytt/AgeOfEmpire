package fr.uiytt.ageofempire.gui

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.utils.PlayerFromUUIDNotFoundException
import fr.uiytt.ageofempire.utils.Utils.newItemStack
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

class TeamGui : InventoryProvider {
    private val inventory: SmartInventory = SmartInventory.builder()
        .id("AOE_Team")
        .size(3, 9)
        .title("Team")
        .provider(this)
        .manager(AgeOfEmpire.invManager)
        .build()

    override fun init(player: Player, contents: InventoryContents) {
        contents.fillBorders(
            ClickableItem.empty(
                newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY.toString() + "", listOf("")))
        )
    }

    override fun update(player: Player, contents: InventoryContents) {
        val slots = intArrayOf(3, 5, 1, 7)
        for ((i, team) in getGameManager().gameData.teams.withIndex()) {
            contents[1, slots[i]] = ClickableItem.of(
                newItemStack(team.color.banner, team.color.chatColor.toString() + team.name, loreBuilder(team), 1)
            ) { if (addPlayer(team, player)) { player.sendMessage("vous avez été ajouté à la team") } }
        }
    }

    /**
     * For a given team, return a list of player and available slots in the team to be shown in the lore
     *
     * @param team A team which may contains player
     * @return a list of players and slots for this team as Lore {@see List<String>}
     */
    private fun loreBuilder(team: GameTeam): List<String?> {
        val lore: MutableList<String?> = ArrayList()
        for (i in 0..7) {
            if (team.playersUUIDs.size - 1 >= i) {
                val playerUUID = team.playersUUIDs[i]
                val player = Bukkit.getPlayer(playerUUID)
                if (player == null) {
                    team.removePlayer(playerUUID)
                    return loreBuilder(team)
                }
                lore.add(team.color.chatColor.toString() + "- " + player.name)
            } else {
                lore.add(team.color.chatColor.toString() + "- _______")
            }
        }
        return lore
    }

    /**
     * Try to add a player to a team
     *
     * @param team   the team to join
     * @param player the player that want to join
     * @return true if the player was added
     */
    private fun addPlayer(team: GameTeam, player: Player): Boolean {
        if (team.playersUUIDs.size >= 8) {
            return false
        }
        val previousTeam: GameTeam? = player.uniqueId.getPlayerTeam()
        try {
            previousTeam?.removePlayer(player.uniqueId)
            team.addPlayer(player.uniqueId)
        } catch (e: PlayerFromUUIDNotFoundException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun openGUI(player: Player?) {
        inventory.open(player)
    }
}