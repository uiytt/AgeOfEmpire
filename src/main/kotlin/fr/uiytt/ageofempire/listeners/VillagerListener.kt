package fr.uiytt.ageofempire.listeners

import fr.uiytt.ageofempire.AgeOfEmpire.Companion.gameManager
import fr.uiytt.ageofempire.base.Building
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.base.BuildingType.Companion.getBuildingTypeFromName
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.isRunning
import fr.uiytt.ageofempire.utils.ColorLink
import fr.uiytt.ageofempire.utils.Utils
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

@SuppressWarnings("unused")
class VillagerListener: Listener {

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if(!getGameManager().isRunning() ) return 
        if(event.entity !is Villager|| event.entity.customName == null) return 
        if(!getGameManager().gameData.isAssaults) {
            event.isCancelled = true 
            return 
        }

        //Prevent mobs from damaging the villagers
        if(event.damager !is Player) {
            event.isCancelled = true 
            return 
        }

        //Get the team of the villager from the name
        val colorLink: ColorLink = ColorLink.getColorFromString(event.entity.customName!!.substring(0,2))?: return 
        var villagerGameTeam: GameTeam? = null 
        for(iteratorTeam: GameTeam in getGameManager().gameData.teams) {
            if(iteratorTeam.color == colorLink) {
                villagerGameTeam = iteratorTeam 
                break 
            }
        }
        if(villagerGameTeam == null) return 

        //Check if player attacking is on the same team of the villager
        if(getGameManager().gameData.playersTeam[event.damager.uniqueId]!!.color != colorLink) {
            event.isCancelled = true
            return 
        }

        //Get villager's building from the name
        val type = event.entity.customName!!.split(ChatColor.GRAY.toString() + " - ")[0].substring(2) 
        val buildingType: BuildingType = BuildingType.getBuildingTypeFromName(type) ?: return

        val building: Building = villagerGameTeam.teamBase.builds[buildingType]!!
        building.health -= event.damage
        event.damage = 0.0

        if(building.health < 0.5) (event.entity as Villager).health = 0.0
        else {
            event.entity.customName = villagerGameTeam.color.chatColor.toString() + buildingType.displayName + ChatColor.GRAY + " - " + ChatColor.GREEN + Utils.roundToHalf(building.health)
            villagerGameTeam.teamBase.builds[buildingType]?.sendWarning()
        }
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (!getGameManager().isRunning()) return
        if (event.entity !is Villager) return
        if (event.entity.customName == null) return

        val colorLink = ColorLink.getColorFromString(event.entity.customName!!.substring(0, 2))
        var villagerTeam: GameTeam? = null
        for (iteratorTeam in gameManager.gameData.teams) {
            if (iteratorTeam.color == colorLink) {
                villagerTeam = iteratorTeam
                break
            }
        }
        if (villagerTeam == null) return
        val type = event.entity.customName!!.split((ChatColor.GRAY.toString() + " - ").toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].substring(2)
        val buildingType = getBuildingTypeFromName(type) ?: return

        //For the Forum Only
        if (buildingType === BuildingType.FORUM) {
            villagerTeam.teamBase.isForumAlive = false
            for (player in Bukkit.getOnlinePlayers()) {
                player.sendMessage(org.bukkit.ChatColor.GREEN.toString() + "" + org.bukkit.ChatColor.STRIKETHROUGH + "                                         ")
                player.sendMessage(org.bukkit.ChatColor.RED.toString() + "Le " + buildingType.displayName + " des " + villagerTeam.color.chatColor + villagerTeam.name + org.bukkit.ChatColor.RED + " vient d'etre détruit.")
                player.sendMessage(org.bukkit.ChatColor.WHITE.toString() + "Les joueurs de cette équipe ne peuvent plus respawn.")
                player.sendMessage(org.bukkit.ChatColor.GREEN.toString() + "" + org.bukkit.ChatColor.STRIKETHROUGH + "                                         ")
                player.playSound(player.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
            }
            return
        }

        //For the rest of the buildings
        val building = villagerTeam.teamBase.builds[buildingType]!!
        building.isConstructed = false
        building.explodeBuilding(event.entity.location)
        for (playerUUID in villagerTeam.playersUUIDs) {
            val player = Bukkit.getPlayer(playerUUID)
            if (player != null) {
                player.sendMessage(org.bukkit.ChatColor.RED.toString() + "Votre " + buildingType.displayName + " est DETRUIT !!")
                player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f)
            }
        }
    }

    @EventHandler
    fun onRightClickOnEntity(event: PlayerInteractEntityEvent) {
        if (!getGameManager().isRunning()) return
        if (event.rightClicked.type != EntityType.VILLAGER || event.rightClicked.customName == null) return
        event.isCancelled = true
        val colorLink = ColorLink.getColorFromString(event.rightClicked.customName!!.substring(0, 2))
        var villagerGameTeam: GameTeam? = null
        for (iteratorTeam in gameManager.gameData.teams) {
            if (iteratorTeam.color == colorLink) {
                villagerGameTeam = iteratorTeam
                break
            }
        }
        if (villagerGameTeam == null) return

        //Check if player rightclicking is on the team of the villager
        if (gameManager.gameData.playersTeam[event.player.uniqueId]!!.color != colorLink) return
        val type = event.rightClicked.customName!!.split((org.bukkit.ChatColor.GRAY.toString() + " - ").toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].substring(2)
        val buildingType = getBuildingTypeFromName(type) ?: return
        buildingType.openVillagerInventory(event.player)
    }
}