package fr.uiytt.ageofempire;

import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameTeam;
import fr.uiytt.ageofempire.gui.TeamGui;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1) {
            sendHelp(sender);
            return true;
        }
        switch (args[0]) {
            case "start" -> {
                if (GameManager.getGameInstance().getGameData().isGameRunning()) {
                    sender.sendMessage("déjà commencé");
                    break;
                }
                GameManager.getGameInstance().init(new ArrayList<>(Bukkit.getServer().getOnlinePlayers()));
                Bukkit.broadcastMessage("ça commence");
            }
            case "stop" -> {
                if (!GameManager.getGameInstance().getGameData().isGameRunning()) {
                    sender.sendMessage("pas commencé");
                    break;
                }
                GameManager.getGameInstance().stopGame();
                Bukkit.broadcastMessage("ça stop");
            }
            case "team" -> {
                if (!(sender instanceof Player player) || GameManager.getGameInstance().getGameData().isGameRunning()) {
                    return false;
                }
                new TeamGui().openGUI(player);
            }
            case "test" -> {
                if (!(sender instanceof Player player)) {
                    return false;
                }
                if (!GameManager.getGameInstance().getGameData().isGameRunning()) return false;
                GameTeam playerTeam = GameManager.getGameInstance().getGameData().getPlayersTeam().get(player.getUniqueId());
                playerTeam.getTeamBase().setAge(playerTeam.getTeamBase().getAge() + 1);
                //player.getInventory().addItem(Utils.newItemStack(playerTeam.getColor().getWool(), BuildingType.FORGE.getDisplayName(), List.of("&8" + BuildingType.FORGE.getDisplayName(), "&8AOE")));
            }
        }
        return true;
    }

    private void sendHelp(CommandSender player) {
        player.sendMessage("HELP");
    }
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
