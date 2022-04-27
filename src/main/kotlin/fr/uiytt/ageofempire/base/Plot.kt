package fr.uiytt.ageofempire.base

import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.stringToLocation
import fr.uiytt.ageofempire.structures.Structure
import fr.uiytt.ageofempire.structures.StructureNotLoadedException
import org.bukkit.Location
import org.bukkit.Tag
import java.io.File

/**
 * Create a new usable plot for building from yaml values and automatically register it
 * @param teamBase the TeamBase instance of the team
 * @param values A LinkedHashMap<String, <String,String> , should contain a "size" and a "location"
 * Size should be a string of an int between 1 and 3
 * Location should be a string with the 3 lowest coordinates of the plot ex : "12 69 -15"
 */
class Plot(teamBase: TeamBase, values: Any) {
    var size = 0
    private lateinit var location: Location
    var constructed = false
    var destroyed = false
    private var side: String = "left"
    val isPlotAvailable: Boolean
        get() = !constructed && !destroyed


    init {
        try {
            val data = values as LinkedHashMap<String, String>
            size = data["size"]!!.toInt()
            location = stringToLocation(data["location"]!!)
            side = data["side"]?: side
            if (size > 3 || size < 1 || (location.x == 0.0 && location.y == 80.0 && location.z == 0.0))
                throw Exception("Size or location load error in plot")
            teamBase.plots[location] = this
        } catch (exception: Exception) {
            AgeOfEmpire.log.warning("Wrong value in plots of " + teamBase.yamlBase.filePath)
            exception.printStackTrace()
        }
    }

    /**
     * Start the construction of the corresponding building in this plot
     * @param playerTeam The team constructing the plot
     * @param buildingType The type of building constructed
     * @param building An instance of a building to store information
     */
    fun build(playerTeam: GameTeam, buildingType: BuildingType, building: Building) {
        building.inConstruction = true
        building.plotLocation = location
        constructed = true
        val structureFile = File("plugins" + File.separator + "AgeOfEmpire" + File.separator + playerTeam.color.name + File.separator + buildingType.name + ".yml")
        val structure = Structure(AgeOfEmpire.instance, structureFile, building, side)
        try {
            structure.loadStructure().pastStructure(location, buildingType.time)
        } catch (e: StructureNotLoadedException) {
            e.printStackTrace()
            building.inConstruction = false
            constructed = false
        }
    }

    companion object {
        private const val MAX_PLOT_SIZE = 30

        /**
         * From a location, search around to locate if there is a plot of this team existing
         * @param teamBase the team of the plot it's looking for
         * @param location the location where it must look around
         * @return A plot instance if a plot is found, or null if no correct plot is found
         */
        @JvmStatic
        fun checkForPlot(teamBase: TeamBase, location: Location): Plot? {
            //Substract 1 from x until a border is met or return null
            for (x in 0..MAX_PLOT_SIZE) {
                if (Tag.WOOL.isTagged(location.subtract(1.0, 0.0, 0.0).block.type)) {
                    location.add(
                        1.0,
                        0.0,
                        0.0
                    ) //Add 1 because the registered location for the plot is not on the border
                    break
                }
                if (x == MAX_PLOT_SIZE) return null
            }
            //Substract 1 from y until a border is met or return null
            for (z in 0..MAX_PLOT_SIZE) {
                if (Tag.WOOL.isTagged(location.subtract(0.0, 0.0, 1.0).block.type)) {
                    location.add(
                        0.0,
                        0.0,
                        1.0
                    ) //Add 1 because the registered location for the plot is not on the border
                    break
                }
                if (z == MAX_PLOT_SIZE) return null
            }
            return teamBase.plots[location]
        }
    }
}