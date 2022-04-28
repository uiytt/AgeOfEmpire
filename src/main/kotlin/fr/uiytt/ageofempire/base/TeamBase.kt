package fr.uiytt.ageofempire.base

import de.leonhard.storage.Yaml
import fr.uiytt.ageofempire.game.GameScoreboard.Companion.playersScoreboard
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.stringToLocation
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import java.io.File

/**
 * All data linked to the base of team
 * Information such as ressources, Age and plots are stored here.
 *
 * @param gameTeam to which team the base belong
 */
class TeamBase(val gameTeam: GameTeam) {
    val yamlBase: Yaml = Yaml("config.yml", "plugins" + File.separator + "AgeOfEmpire" + File.separator + gameTeam.color.name)
    val spawnTeam: Location = stringToLocation(yamlBase.getOrDefault("spawn", "20 80 20"))
    val builds = HashMap<BuildingType, Building>()
    val plots = HashMap<Location, Plot>()
    var age = 1
    var stone = 0
    var wood = 0
    var isForumAlive = true

    init {
        for (buildingType in BuildingType.values()) {
            builds[buildingType] = Building(buildingType, this)
        }
        yamlBase.getList("plots").forEach {Plot(this, it!!)}
        val building = builds[BuildingType.FORUM]!!
        val villager = getGameManager().world.spawnEntity(
            stringToLocation(yamlBase.getOrDefault("villager", "13 70 0")),
            EntityType.VILLAGER
        ) as Villager
        villager.health = 20.0
        villager.customName = gameTeam.color.chatColor.toString() + BuildingType.FORUM.displayName + ChatColor.GRAY + " - " + ChatColor.GREEN + building.health
        villager.setAI(false)
        villager.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)!!.baseValue = 100.0
    }

    /**
     * Update for all players of the team, the ressources in the scoreboard
     */
    fun updateTeamScoreboard() {
        gameTeam.playersUUIDs.forEach {
            val gameScoreboard = playersScoreboard[it]!!
            gameScoreboard.updateGoldAmmount(getGameManager().gameData.gold[it]?: 0)
            gameScoreboard.updateWoodAmmount(wood)
            gameScoreboard.updateStoneAmmount(stone)
        }
    }

}