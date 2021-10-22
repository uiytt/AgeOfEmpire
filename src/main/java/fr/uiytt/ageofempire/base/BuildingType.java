package fr.uiytt.ageofempire.base;

import fr.uiytt.ageofempire.gui.villagergui.ArcheryVillagerGUI;
import fr.uiytt.ageofempire.gui.villagergui.ArmoryVillagerGUI;
import fr.uiytt.ageofempire.gui.villagergui.ForgeVillagerGui;
import fr.uiytt.ageofempire.gui.villagergui.ForumVillagerGUI;
import fr.uiytt.ageofempire.gui.villagergui.LibraryVillagerGUI;
import fr.uiytt.ageofempire.gui.villagergui.MillVillagerGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public enum BuildingType {
    FORUM("Forum", 0, 0, 0, 1, 3000d,0, player -> new ForumVillagerGUI().open(player)),
    FORGE("Forge", 1, 250,200,1, 1500d, 120, player -> new ForgeVillagerGui().open(player)),
    MILL("Moulin", 1, 110, 150, 1, 1300d, 90, player -> new MillVillagerGui().open(player)),
    ARMORY("Armurie", 1, 200, 175, 1, 1000d, 120, player -> new ArmoryVillagerGUI().open(player)),
    MINE("Mine", 2, 300, 120, 1, 800d, 200, player -> {}),
    SAWMILL("Scierie", 2, 100, 250, 1, 800d, 200, player -> {}),
    LIBRARY("Bibliothèque", 2, 320, 250, 2, 1100d, 180, player -> new LibraryVillagerGUI().open(player)),
    ARCHERY("Archerie", 2, 200, 300, 2, 700d, 150, player -> new ArcheryVillagerGUI().open(player));

    private final String displayName;
    private final int age;
    private final int woodCost;
    private final int stoneCost;
    private final int size;
    private final double health;
    private final int time;
    private final Consumer<Player> openVillagerInventory;

    /**
     * All building with their fix data no matter the time of the game nor the team
     * @param displayName Name of the build, can be anything, do not use this to find the schem file name, use .name() instead
     * @param woodCost int
     * @param stoneCost int
     * @param size size should be either 1, 2 or 3 and indicate which plot should be used to build this building
     */
    BuildingType(String displayName, int age, int stoneCost, int woodCost, int size, double health, int time, Consumer<Player> openVillagerInventory) {
        this.displayName = displayName;
        this.age =  age;
        this.woodCost = woodCost;
        this.stoneCost = stoneCost;
        if(size > 3 || size < 1) size = 1;
        this.size = size;
        this.health = health;
        this.time = time;
        this.openVillagerInventory = openVillagerInventory;
    }

    public void openVillagerInventory(Player player) {
        openVillagerInventory.accept(player);
    }

    public String getDisplayName() {return displayName;}
    public int getAge() {return age;}
    public int getSize() {return size;}
    public double getHealth() {return  health;}
    public int getWoodCost() { return woodCost;}
    public int getStoneCost() { return stoneCost;}
    public int getTime() {return time;}

    @Nullable
    public static BuildingType getBuildingTypeFromName(String name) {
        for(BuildingType buildingType : BuildingType.values()) {
            if(buildingType.displayName.equalsIgnoreCase(name)) return buildingType;
        }
        return null;
    }

}
