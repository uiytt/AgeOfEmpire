package fr.uiytt.ageofempire;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handle the main config file
 * Currently not loading the config, need to be improved
 */
public class ConfigManager {

    //private static final Yaml configYaml = new Yaml("config.yml", "plugins" + File.separator + "AgeOfEmpire");
    private static World world;
    private static int pvpTimer = 20*60;
    private static int assaultTimer = 40*60;

    private static final Set<Material> breakableBlocks = new HashSet<>(List.of(Material.ANDESITE));
    private static final Set<Material> deletedDrops = new HashSet<>(List.of(Material.DIRT));
    private static final Set<Location> seaLanterns = new HashSet<>();

    public static void init() {
        breakableBlocks.addAll(Tag.OAK_LOGS.getValues());
        breakableBlocks.addAll(Tag.PLANKS.getValues());
        breakableBlocks.add(Material.SEA_LANTERN);
        breakableBlocks.add(Material.CAKE);
        deletedDrops.addAll(Tag.WOOL.getValues());
        deletedDrops.addAll(Tag.ITEMS_STONE_TOOL_MATERIALS.getValues());
        deletedDrops.add(Material.LEATHER_HELMET);
        deletedDrops.add(Material.LEATHER_CHESTPLATE);
        deletedDrops.add(Material.LEATHER_LEGGINGS);
        deletedDrops.add(Material.LEATHER_BOOTS);

        seaLanterns.add(new Location(getWorld(), 0, 72, 0));
    }

    public static World getWorld() {
        return world;
    }
    public static void setWorld(World world) {
        ConfigManager.world = world;
    }

    public static int getPvpTimer() {
        return pvpTimer;
    }
    public static void setPvpTimer(int pvpTimer) {
        ConfigManager.pvpTimer = pvpTimer;
    }

    public static int getAssaultTimer() {
        return assaultTimer;
    }
    public static void setAssaultTimer(int assaultTimer) {
        ConfigManager.assaultTimer = assaultTimer;
    }

    public static Set<Material> getBreakableBlocks() {
        return breakableBlocks;
    }

    public static Set<Material> getDeletedDrops() { return deletedDrops; }
    public static Set<Location> getSeaLanterns() {return seaLanterns;}
}
