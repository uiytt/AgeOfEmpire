package fr.uiytt.ageofempire

import org.bukkit.*

/**
 * Handle the main config file
 * Currently not loading the config, need to be improved
 */
class ConfigManager {
    //private static final Yaml configYaml = new Yaml("config.yml", "plugins" + File.separator + "AgeOfEmpire") 
    var world: World? = Bukkit.getWorld("world")
    var pvpTimer = 20 * 60
    var assaultTimer = 40 * 60

    private val breakableBlocks: MutableSet<Material> = HashSet(listOf(Material.ANDESITE))
    private val deletedDrops: MutableSet<Material> = HashSet(listOf(Material.DIRT))
    private val seaLanterns: MutableSet<Location> = HashSet() //Sea lanterns are infinite ressources

    fun init() {
        breakableBlocks.addAll(Tag.OAK_LOGS.values)
        breakableBlocks.addAll(Tag.PLANKS.values)
        deletedDrops.addAll(Tag.WOOL.values)
        deletedDrops.addAll(Tag.ITEMS_STONE_TOOL_MATERIALS.values)
        breakableBlocks.add(Material.SEA_LANTERN)
        breakableBlocks.add(Material.CAKE)
        deletedDrops.add(Material.LEATHER_HELMET)
        deletedDrops.add(Material.LEATHER_CHESTPLATE)
        deletedDrops.add(Material.LEATHER_LEGGINGS)
        deletedDrops.add(Material.LEATHER_BOOTS)
        seaLanterns.add(Location(world, 0.0, 72.0, 0.0))
    }

    fun getSetOfBreakableBlocks(): Set<Material> = breakableBlocks.toSet() 
    fun getSetOfDeletedDrops(): Set<Material> = deletedDrops.toSet() 
    fun getSetOfSeaLanterns(): Set<Location> = seaLanterns.toSet() 

}
fun getConfigManager(): ConfigManager = AgeOfEmpire.configManager

/**
 * For a given string of coordinates, return a location in the Config World
 * @param locString a string either "x y z" or "x y z yaw pitch"
 * @return a location in [ConfigManager.world]
 */
fun stringToLocation(locString: String): Location {
    val string = locString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    return try {
        val loc = Location(getConfigManager().world, string[0].toDouble(), string[1].toDouble(), string[2].toDouble())
        if (string.size > 3) {
            loc.yaw = string[3].toFloat()
            loc.pitch = string[4].toFloat()
        }
        loc
    } catch (exception: NumberFormatException) {
        exception.printStackTrace()
        Location(getConfigManager().world, 0.0, 80.0, 0.0)
    }
}
