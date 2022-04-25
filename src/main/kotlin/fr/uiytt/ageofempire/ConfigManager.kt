package fr.uiytt.ageofempire

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.World

/**
 * Handle the main config file
 * Currently not loading the config, need to be improved
 */
class ConfigManager {
    //private static final Yaml configYaml = new Yaml("config.yml", "plugins" + File.separator + "AgeOfEmpire") 
    var world: World? = null
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
