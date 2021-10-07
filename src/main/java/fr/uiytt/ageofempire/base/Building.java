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

    public Building(BuildingType buildingType, TeamBase teamBase) {
        this.buildingType = buildingType;
        this.teamBase = teamBase;
    }

    public boolean isAvailable() {
        return !inConstruction && !constructed;
    }


    public void summonBuildingVillager(Location villagerLocation) {
        Villager villager = (Villager) villagerLocation.getWorld().spawnEntity(villagerLocation, EntityType.VILLAGER);
        villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(buildingType.getHealth());
        villager.setHealth(buildingType.getHealth());
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

    public boolean isInConstruction() {
        return inConstruction;
    }
    public void setInConstruction(boolean inConstruction) {
        this.inConstruction = inConstruction;
    }
    public boolean isConstructed() {
        return constructed;
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
}
