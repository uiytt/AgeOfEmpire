package fr.uiytt.ageofempire.base;


import de.leonhard.storage.Yaml;
import fr.uiytt.ageofempire.ConfigManager;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameScoreboard;
import fr.uiytt.ageofempire.game.GameTeam;
import fr.uiytt.ageofempire.utils.ConfigParser;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamBase {

    private final Yaml yamlBase;
    private final GameTeam gameTeam;

    private final Location spawnTeam;
    private final HashMap<BuildingType, Building> builds = new HashMap<>();
    private final HashMap<Location, Plot> plots = new HashMap<>();

    private int stone = 0;
    private int wood = 0;
    private int age = 1;
    private boolean forumAlive = true;

    public TeamBase(GameTeam team) {
        this.gameTeam = team;
        this.yamlBase = new Yaml("config.yml", "plugins" + File.separator + "AgeOfEmpire" + File.separator + team.getColor().name());
        for(BuildingType buildingType : BuildingType.values()) {
            builds.put(buildingType, new Building(buildingType, this));
        }
        spawnTeam = ConfigParser.stringToLocation(yamlBase.getOrDefault("spawn","20 80 20"));

        List<?> plotsList = yamlBase.getList("plots");
        for (Object plot : plotsList) {
            new Plot(this, plot);
        }

        Villager villager = (Villager) ConfigManager.getWorld().spawnEntity(ConfigParser.stringToLocation(yamlBase.getOrDefault("villager","13 70 0")), EntityType.VILLAGER);
        villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(BuildingType.FORUM.getHealth());
        villager.setHealth(BuildingType.FORUM.getHealth());
        villager.setCustomName(getGameTeam().getColor().getChatColor() + BuildingType.FORUM.getDisplayName() + ChatColor.GRAY + " - " + ChatColor.GREEN + villager.getHealth());
        villager.setAI(false);
        villager.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100);
    }

    public void updateTeamScoreboard() {
        HashMap<UUID, Integer> gold = GameManager.getGameInstance().getGameData().getGold();
        for(UUID playerUUID : gameTeam.getPlayersUUIDs()) {
            GameScoreboard gameScoreboard = GameScoreboard.getPlayersScoreboard().get(playerUUID);
            gameScoreboard.updateGoldAmmount(gold.get(playerUUID));
            gameScoreboard.updateWoodAmmount(wood);
            gameScoreboard.updateStoneAmmount(stone);
        }
    }



    public Yaml getYamlBase() { return yamlBase;}
    public HashMap<Location,Plot> getPlots() { return plots;}
    public HashMap<BuildingType, Building> getBuilds() { return builds;}
    public Location getSpawnTeam() { return spawnTeam;}
    public GameTeam getGameTeam() { return gameTeam;}

    public int getStone() {
        return stone;
    }

    public void addStone(int stone) {
        this.stone += stone;
    }

    public int getWood() {
        return wood;
    }

    public void addWood(int wood) {
        this.wood += wood;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isForumAlive() {
        return forumAlive;
    }

    public void setForumAlive(boolean forumAlive) {
        this.forumAlive = forumAlive;
    }
}
