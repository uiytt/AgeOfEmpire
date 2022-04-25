package fr.uiytt.ageofempire.listeners

import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType.Companion.getBuildingTypeFromName
import fr.uiytt.ageofempire.base.Plot.Companion.checkForPlot
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.game.GameTeam.Companion.reorganizeTeam
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.isRunning
import fr.uiytt.ageofempire.getConfigManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class GameListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val team: GameTeam? = getGameManager().gameData.playersTeam[event.player.uniqueId]
        if (getGameManager().isRunning() && team != null) {
            event.player.setPlayerListName(team.color.tabColor.toString() + event.player.displayName)
            return
        } else if (!getGameManager().isRunning()) reorganizeTeam()
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        if (getGameManager().isRunning()) return
        getGameManager().gameData.playersTeam[event.player.uniqueId]?.removePlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!getGameManager().isRunning()) return
        val itemMeta = event.itemInHand.itemMeta ?: return
        val lore = itemMeta.lore
        if (lore == null || !lore[1].contains("AOE")) return
        event.isCancelled = true
        val playerTeam: GameTeam = getGameManager().gameData.playersTeam[event.player.uniqueId]!!
        val plot = checkForPlot(playerTeam.teamBase, event.block.location.subtract(0.0, 1.0, 0.0))

        //Check that plot can be built upon
        if (plot == null || !plot.isPlotAvailable) {
            event.player.sendMessage("Vous ne pouvez pas construire ici")
            return
        }

        //Extract building type from lore of the item
        val buildingName = lore[0].substring(2) //Remove color at the begining
        val buildingType = getBuildingTypeFromName(buildingName)
        if (buildingType == null) {
            event.player.sendMessage("Erreur : mauvais item de construction, contactez un admin")
            return
        }

        //Check that the building is not already constructed
        val building = playerTeam.teamBase.builds[buildingType]
        if (!building!!.isAvailable) {
            event.player.sendMessage("Vous ne pouvez pas construir ça, ce bâtiment existe déjà")
            return
        }
        //Check plot size
        if (buildingType.size != plot.size) {
            event.player.sendMessage("Ce bâtiment doit être construit dans un plot de taille " + buildingType.size)
            return
        }

        event.isCancelled = false
        event.block.type = Material.AIR
        plot.build(playerTeam, buildingType, building)
    }

    @EventHandler
    fun onExplosion(event: EntityExplodeEvent) {
        if (!getGameManager().isRunning()) return
        event.yield = 0f
        event.blockList().removeIf { block: Block -> Tag.WOOL.isTagged(block.type) }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!getGameManager().isRunning()) return
        val gameTeam: GameTeam = getGameManager().gameData.playersTeam[event.player.uniqueId]!!
        if (!getConfigManager().getSetOfBreakableBlocks().contains(event.block.type)) {
            event.isCancelled = true
            return
        }

        event.isDropItems = false
        if (event.block.type == Material.ANDESITE) {
            gameTeam.teamBase.stone += 8
            getGameManager().gameData.addGold(event.player.uniqueId, 10)
            event.player.giveExp(1)
            event.player.sendMessage("+ 8 Stone, +10 Gold")
            gameTeam.teamBase.updateTeamScoreboard()
        } else if (Tag.LOGS.isTagged(event.block.type)) {
            gameTeam.teamBase.wood += 10
            getGameManager().gameData.addGold(event.player.uniqueId, 10)
            event.player.giveExp(1)
            event.player.sendMessage("+ 10 Bois, +10 Gold")
            gameTeam.teamBase.updateTeamScoreboard()
        } else if (event.block.type == Material.SEA_LANTERN) {
            gameTeam.teamBase.wood += 10
            gameTeam.teamBase.stone += 8
            getGameManager().gameData.addGold(event.player.uniqueId, 10)
            event.player.giveExp(1)
            event.player.sendMessage("+8 Stone + 10 Bois, +10 Gold")
            gameTeam.teamBase.updateTeamScoreboard()
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (!getGameManager().isRunning()) return
        val deadPlayer = event.entity
        val gameTeam: GameTeam = getGameManager().gameData.playersTeam[deadPlayer.uniqueId]!!
        event.drops.removeIf { itemStack: ItemStack -> getConfigManager().getSetOfDeletedDrops().contains(itemStack.type) }
        deadPlayer.gameMode = GameMode.SPECTATOR

        if (gameTeam.teamBase.isForumAlive) {
            object : BukkitRunnable() {
                override fun run() {
                    deadPlayer.gameMode = GameMode.SURVIVAL
                    deadPlayer.teleport(gameTeam.teamBase.spawnTeam)
                }
            }.runTaskLater(AgeOfEmpire.instance, 7*20L)
            return
        }
        Bukkit.broadcastMessage("&f&lLe joueur " + gameTeam.color.chatColor + deadPlayer.displayName + " &f&l est mort définitivement")
        val lastTeam: GameTeam = getGameManager().isGameEnd ?: return
        Bukkit.getOnlinePlayers().forEach { player: Player -> player.gameMode = GameMode.SPECTATOR }
        Bukkit.broadcastMessage(org.bukkit.ChatColor.GREEN.toString() + "" + org.bukkit.ChatColor.STRIKETHROUGH + "                                         ")
        Bukkit.broadcastMessage(org.bukkit.ChatColor.WHITE.toString() + "Victoire de l'equipe " + lastTeam.color.chatColor + lastTeam.name + org.bukkit.ChatColor.WHITE + " !!")
        Bukkit.broadcastMessage(org.bukkit.ChatColor.GREEN.toString() + "" + org.bukkit.ChatColor.STRIKETHROUGH + "                                         ")
        getGameManager().stopGame()
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        if (!getGameManager().isRunning()) return
        val gameTeam: GameTeam = getGameManager().gameData.playersTeam[event.player.uniqueId]!!
        event.isCancelled = true
        if (event.player.gameMode != GameMode.SPECTATOR || gameTeam.teamBase.isForumAlive) {
            if (event.message[0] == '*') {
                Bukkit.broadcastMessage(
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        "&8&l[" + gameTeam.color.chatColor + gameTeam.name + "&8&l] &e" + event.player.displayName + "&7 : " + event.message.substring(1)
                    )
                )
            } else {
                gameTeam.playersUUIDs.forEach {
                    Bukkit.getPlayer(it)?.sendMessage(
                        ChatColor.translateAlternateColorCodes(
                            '&',
                            "&8&l[" + gameTeam.color.chatColor + "EQUIPE&8&l] &e" + event.player.displayName + "&7: " + event.message
                        )
                    )
                }
            }
        } else {
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.gameMode == GameMode.SPECTATOR) {
                    onlinePlayer.sendMessage(ChatColor.GRAY.toString() + "[SPEC] " + event.player.displayName + ": " + event.message)
                }
            }
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!getGameManager().isRunning() || getGameManager().gameData.isPvp) return
        if (event.entity !is Player) return
        if (event.damager.type == EntityType.ARROW) {
            val arrow = event.damager as Arrow
            if (arrow.shooter !is Player) return
        } else if (event.damager !is Player) return
        event.isCancelled = true
    }
}