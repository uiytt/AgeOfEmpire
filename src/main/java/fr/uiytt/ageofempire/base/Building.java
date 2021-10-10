package fr.uiytt.ageofempire.base;

import fr.uiytt.ageofempire.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.UUID;

public class Building {

    private final BuildingType buildingType;
    private final TeamBase teamBase;

    private boolean inConstruction = false;
    private boolean constructed = false;
    private long lastWarning = System.currentTimeMillis();
    private Location plotLocation = new Location(ConfigManager.getWorld(), 0, 0, 0);
    private double health;

    /**
     * An object storing all the information related to a building constructed in game.
     * @param buildingType The type of building
     * @param teamBase The base in which the building is constructed
     */
    public Building(BuildingType buildingType, TeamBase teamBase) {
        this.buildingType = buildingType;
        this.teamBase = teamBase;
        this.health = buildingType.getHealth();
    }

    public boolean isAvailable() {
        return !inConstruction && !constructed;
    }

    public void summonBuildingVillager(Location villagerLocation) {
        Villager villager = (Villager) villagerLocation.getWorld().spawnEntity(villagerLocation, EntityType.VILLAGER);
        villager.setHealth(20);
        villager.setCustomName(teamBase.getGameTeam().getColor().getChatColor() + buildingType.getDisplayName() + ChatColor.GRAY + " - " + ChatColor.GREEN + villager.getHealth());
        villager.setAI(false);
        villager.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100);

        teamBase.getGameTeam().getPlayersUUIDs().forEach(playerUUID -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null) {
                player.sendMessage("Bâtiment " + buildingType.getDisplayName() + " construit.");
            }
        });
    }

    public void sendWarning() {
        if(System.currentTimeMillis() <= lastWarning + 10000) return;
        lastWarning = System.currentTimeMillis();
        for(UUID playerUUID : teamBase.getGameTeam().getPlayersUUIDs()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null) player.sendMessage(ChatColor.RED + "Votre " + buildingType.getDisplayName() + " est attaqué !!");
        }
    }

    public void explodeBuilding(Location villagerLoc) {
        Plot plot = teamBase.getPlots().get(plotLocation);
        plot.setDestroyed(true);
        plot.setConstructed(false);

        villagerLoc.getWorld().spawnEntity(villagerLoc, EntityType.PRIMED_TNT);
    }
    
    public void setInConstruction(boolean inConstruction) {
        this.inConstruction = inConstruction;
    }
    public void setConstructed(boolean constructed) {
        this.constructed = constructed;
    }
    public Location getPlotLocation() {
        return plotLocation;
    }
    public void setPlotLocation(Location plotLocation) {
        this.plotLocation = plotLocation;
    }
    public double getHealth() {return health;}
    public void setHealth(double health) {this.health = health;}
}
