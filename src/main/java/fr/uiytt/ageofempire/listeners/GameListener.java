package fr.uiytt.ageofempire.listeners;

import fr.uiytt.ageofempire.ConfigManager;
import fr.uiytt.ageofempire.base.Building;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.Plot;
import fr.uiytt.ageofempire.game.GameData;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class GameListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        GameTeam team = GameManager.getGameInstance().getGameData().getPlayersTeam().get(event.getPlayer().getUniqueId());
        if(GameManager.getGameInstance().getGameData().isGameRunning()) {
            if(team != null) {
                event.getPlayer().setPlayerListName(team.getColor().getTabColor() + event.getPlayer().getDisplayName());
            } else {
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            return;
        }
        GameTeam.reorganizeTeam();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(GameManager.getGameInstance().getGameData().isGameRunning()) {
            return;
        }
        GameTeam team = GameManager.getGameInstance().getGameData().getPlayersTeam().get(event.getPlayer().getUniqueId());
        if(team != null) {
            team.removePlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!GameManager.getGameInstance().getGameData().isGameRunning()) return;

        ItemMeta itemMeta = event.getItemInHand().getItemMeta();
        if(itemMeta == null) return;

        List<String> lore = itemMeta.getLore();
        if(lore == null || !lore.get(1).contains("AOE")) return;

        Player player = event.getPlayer();

        event.setCancelled(true);

        GameTeam playerTeam = GameManager.getGameInstance().getGameData().getPlayersTeam().get(event.getPlayer().getUniqueId());
        Plot plot = Plot.checkForPlot(playerTeam.getTeamBase(),event.getBlock().getLocation().subtract(0,1,0));
        if(plot == null || !plot.isPlotAvailable()) {
            player.sendMessage("Vous ne pouvez pas construire ici");
            return;
        }

        String buildingName = itemMeta.getLore().get(0).substring(2);
        BuildingType buildingType = BuildingType.getBuildingTypeFromName(buildingName);
        if(buildingType == null) {
            player.sendMessage("Erreur : mauvais item de construction, contactez un admin");
            return;
        }

        Building building = playerTeam.getTeamBase().getBuilds().get(buildingType);
        if(!building.isAvailable()) {
            player.sendMessage("Vous ne pouvez pas construir ça, ce bâtiment existe déjà");
            return;
        }
        if(buildingType.getSize() != plot.getSize()) {
            player.sendMessage("Ce bâtiment doit être construit dans un plot de taille " + buildingType.getSize());
            return;
        }
        event.setCancelled(false);

        event.getBlock().setType(Material.AIR);

        plot.build(playerTeam, buildingType, building);

    }



    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if(!GameManager.getGameInstance().getGameData().isGameRunning()) return;
        event.setYield(0f);
        event.blockList().removeIf(block -> Tag.WOOL.isTagged(block.getType()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!GameManager.getGameInstance().getGameData().isGameRunning()) return;
        Player player = event.getPlayer();
        GameData gameData = GameManager.getGameInstance().getGameData();
        GameTeam gameTeam = gameData.getPlayersTeam().get(player.getUniqueId());

        if(!ConfigManager.getBreakableBlocks().contains(event.getBlock().getType())) {
            event.setCancelled(true);
            return;
        }

        event.setDropItems(false);
        if(event.getBlock().getType() == Material.ANDESITE) {
            gameTeam.getTeamBase().addStone(8);
            gameData.getGold().put(player.getUniqueId(), gameData.getGold().get(player.getUniqueId()) + 10);
            player.sendMessage("+ 8 Stone, +10 Gold");
            player.giveExp(1);
            gameTeam.getTeamBase().updateTeamScoreboard();
        } else if(Tag.LOGS.isTagged(event.getBlock().getType())) {
            gameTeam.getTeamBase().addWood(10);
            gameData.getGold().put(player.getUniqueId(), gameData.getGold().get(player.getUniqueId()) + 10);
            player.sendMessage("+ 10 Bois, +10 Gold");
            player.giveExp(1);
            gameTeam.getTeamBase().updateTeamScoreboard();
        } else if(event.getBlock().getType() == Material.SEA_LANTERN) {
            gameTeam.getTeamBase().addWood(10);
            gameTeam.getTeamBase().addStone(8);
            gameData.getGold().put(player.getUniqueId(), gameData.getGold().get(player.getUniqueId()) + 10);
            player.sendMessage("+8 Stone + 10 Bois, +10 Gold");
            player.giveExp(1);
            gameTeam.getTeamBase().updateTeamScoreboard();
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        GameData gamedata = GameManager.getGameInstance().getGameData();
        if(!gamedata.isGameRunning()) return;

        Player deadPlayer = event.getEntity();
        GameTeam playerTeam = GameManager.getGameInstance().getGameData().getPlayersTeam().get(deadPlayer.getUniqueId());

        if(!playerTeam.getTeamBase().isForumAlive()) {
            deadPlayer.setGameMode(GameMode.SPECTATOR);
            return;
        }

        event.getDrops().removeIf(itemStack -> ConfigManager.getDeletedDrops().contains(itemStack.getType()));

        deadPlayer.setGameMode(GameMode.SPECTATOR);

        GameTeam lastTeam = GameManager.getGameInstance().isGameEnd();
        if(lastTeam == null) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "                                         ");
        Bukkit.broadcastMessage(ChatColor.WHITE + "Victoire de l'equipe " + lastTeam.getColor().getChatColor() + lastTeam.getName() + ChatColor.WHITE + " !!");
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "                                         ");

        GameManager.getGameInstance().stopGame();

    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        GameData gameData = GameManager.getGameInstance().getGameData();
        if(!gameData.isGameRunning()) return;
        GameTeam playerTeam = gameData.getPlayersTeam().get(event.getPlayer().getUniqueId());
        event.setCancelled(true);
        Player player = event.getPlayer();
        if(event.getPlayer().getGameMode() != GameMode.SPECTATOR || playerTeam.getTeamBase().isForumAlive()) {
             if(event.getMessage().charAt(0) == '*') {
                 Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8&l[" + playerTeam.getColor().getChatColor() + playerTeam.getName() + "&8&l] &e" + player.getDisplayName() + "&7 : " + event.getMessage().substring(1)));
             } else {
                 for(UUID mateUUID : playerTeam.getPlayersUUIDs()) {
                     Player matePlayer = Bukkit.getPlayer(mateUUID);
                     if(matePlayer != null) matePlayer.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',"&8&l[" + playerTeam.getColor().getChatColor() + "EQUIPE&8&l] &e" + player.getDisplayName() + "&7: " + event.getMessage()));
                 }
             }
        } else {
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.getGameMode() == GameMode.SPECTATOR) {
                    onlinePlayer.sendMessage(net.md_5.bungee.api.ChatColor.GRAY + "[SPEC] " + player.getDisplayName() + ": " +  event.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(GameManager.getGameInstance().getGameData().isGameRunning()) || GameManager.getGameInstance().getGameData().isPvp()) {
            return;
        }
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        if(event.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) event.getDamager();
            ProjectileSource damager = arrow.getShooter();
            if(!(damager instanceof Player)) return;
        } else if(!(event.getDamager() instanceof Player)) return;

        event.setCancelled(true);
    }
}
