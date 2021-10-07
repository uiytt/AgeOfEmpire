package fr.uiytt.ageofempire.listeners;

import fr.uiytt.ageofempire.base.Building;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameTeam;
import fr.uiytt.ageofempire.utils.ColorLink;
import fr.uiytt.ageofempire.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

@SuppressWarnings("unused")
public class VillagerListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(!GameManager.getGameInstance().getGameData().isGameRunning()) return;

        if(event.getEntity().getType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) event.getEntity();
        if (villager.getCustomName() == null) return;

        ColorLink colorLink = ColorLink.getColorFromString(villager.getCustomName().substring(0,2));
        GameTeam villagerGameTeam = null;
        for(GameTeam iteratorTeam : GameManager.getGameInstance().getGameData().getTeams()) {
            if(iteratorTeam.getColor() == colorLink) {
                villagerGameTeam = iteratorTeam;
                break;
            }
        }
        if(villagerGameTeam == null) return;

        //Check if player attacking is on the same team of the villager
        if(GameManager.getGameInstance().getGameData().getPlayersTeam().get(event.getDamager().getUniqueId()).getName().equals(villagerGameTeam.getName())) {
            return;
        }

        String type = villager.getCustomName().split(ChatColor.GRAY + " - ")[0].substring(2);
        BuildingType buildingType = BuildingType.getBuildingTypeFromName(type);
        if(buildingType == null) return;

        villager.setCustomName(villagerGameTeam.getColor().getChatColor() + buildingType.getDisplayName() + ChatColor.GRAY + " - " + ChatColor.GREEN + Utils.roundToHalf(villager.getHealth()));
        villagerGameTeam.getTeamBase().getBuilds().get(buildingType).sendWarning();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(!GameManager.getGameInstance().getGameData().isGameRunning()) return;

        if(event.getEntity().getType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) event.getEntity();
        if (villager.getCustomName() == null) return;

        ColorLink colorLink = ColorLink.getColorFromString(villager.getCustomName().substring(0,2));
        GameTeam villagerTeam = null;
        for(GameTeam iteratorTeam : GameManager.getGameInstance().getGameData().getTeams()) {
            if(iteratorTeam.getColor() == colorLink) {
                villagerTeam = iteratorTeam;
                break;
            }
        }
        if(villagerTeam == null) return;

        String type = villager.getCustomName().split(ChatColor.GRAY + " - ")[0].substring(2);
        BuildingType buildingType = BuildingType.getBuildingTypeFromName(type);
        if(buildingType == null) return;

        //For the Forum Only
        if(buildingType == BuildingType.FORUM) {
            villagerTeam.getTeamBase().setForumAlive(false);
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "                                         ");
                player.sendMessage(ChatColor.RED + "Le " + buildingType.getDisplayName() + " des " + villagerTeam.getColor().getChatColor() + villagerTeam.getName() + ChatColor.RED + " vient d'etre détruit.");
                player.sendMessage(ChatColor.WHITE + "Les joueurs de cette équipe ne peuvent plus respawn.");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "                                         ");
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
            }
            return;
        }

        //For the rest of the buildings
        Building building = villagerTeam.getTeamBase().getBuilds().get(buildingType);

        building.setConstructed(false);
        building.explodeBuilding(event.getEntity().getLocation());

        for(UUID playerUUID : villagerTeam.getPlayersUUIDs()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null) {
                player.sendMessage(ChatColor.RED + "Votre " + buildingType.getDisplayName() + " est DETRUIT !!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);
            }
        }

    }

    @EventHandler
    public void onRightClickOnEntity(PlayerInteractEntityEvent event) {
        if(!GameManager.getGameInstance().getGameData().isGameRunning()) return;
        if(event.getRightClicked().getType() != EntityType.VILLAGER || event.getRightClicked().getCustomName() == null) return;

        event.setCancelled(true);

        ColorLink colorLink = ColorLink.getColorFromString(event.getRightClicked().getCustomName().substring(0,2));
        GameTeam villagerGameTeam = null;
        for(GameTeam iteratorTeam : GameManager.getGameInstance().getGameData().getTeams()) {
            if(iteratorTeam.getColor() == colorLink) {
                villagerGameTeam = iteratorTeam;
                break;
            }
        }
        if(villagerGameTeam == null) return;

        //Check if player rightclicking is on the team of the villager
        if(!GameManager.getGameInstance().getGameData().getPlayersTeam().get(event.getPlayer().getUniqueId()).getName().equals(villagerGameTeam.getName())) {
            return;
        }

        String type = event.getRightClicked().getCustomName().split(ChatColor.GRAY + " - ")[0].substring(2);
        BuildingType buildingType = BuildingType.getBuildingTypeFromName(type);
        if(buildingType == null) return;


        buildingType.openVillagerInventory(event.getPlayer());
    }
}
