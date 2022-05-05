package fr.uiytt.ageofempire.base

import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.getPlayerTeam
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import java.util.*
import java.util.function.Consumer

/**
 * A building store and manage different type of
 * information for a certain [BuildingType]
 * All [BuildingType] should have a building
 * even if not yet constructed
 * @param buildingType a type of building
 * @param teamBase the base to which the building belong
 */
class Building(val buildingType: BuildingType, private val teamBase: TeamBase) {
    var inConstruction = false
    var isConstructed = false
    var plotLocation = Location(getGameManager().world, 0.0, 0.0, 0.0)
    var health: Double = buildingType.health
    var villager: Villager? = null
    val isAvailable: Boolean
        get() = !inConstruction && !isConstructed
    private var timeOfLastWarning = System.currentTimeMillis()

    fun summonBuildingVillager(villagerLocation: Location, silent: Boolean = false) {
        villager = villagerLocation.world!!.spawnEntity(villagerLocation, EntityType.VILLAGER) as Villager
        villager!!.health = 20.0
        villager!!.customName = teamBase.gameTeam.color.chatColor.toString() + buildingType.displayName + ChatColor.GRAY + " - " + ChatColor.GREEN + health
        villager!!.setAI(false)
        villager!!.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)!!.baseValue = 100.0
        if(!silent) {
            teamBase.gameTeam.playersUUIDs.forEach(Consumer { playerUUID: UUID? ->
                val player = Bukkit.getPlayer(playerUUID!!)
                player?.sendMessage("Bâtiment " + buildingType.displayName + " construit.")
            })
        }

        if (buildingType == BuildingType.TEMPLE){
            AgeOfEmpire.gameManager.triggerTemple(teamBase.gameTeam)
        }
    }

    fun sendWarning() {
        if (System.currentTimeMillis() <= timeOfLastWarning + 10000) return
        timeOfLastWarning = System.currentTimeMillis()
        for (playerUUID in teamBase.gameTeam.playersUUIDs) {
            val player = Bukkit.getPlayer(playerUUID)
            player?.sendMessage(ChatColor.RED.toString() + "Votre " + buildingType.displayName + " est attaqué !!")
        }
    }

    fun explodeBuilding() {
        this.isConstructed = false
        val plot = teamBase.plots[plotLocation]!!
        plot.destroyed = true
        plot.constructed = false
        villager!!.location.world!!.spawnEntity(villager!!.location, EntityType.PRIMED_TNT)

        if(buildingType == BuildingType.TEMPLE){
            AgeOfEmpire.gameManager.destroyTemple()
        }
        villager = null
    }

}