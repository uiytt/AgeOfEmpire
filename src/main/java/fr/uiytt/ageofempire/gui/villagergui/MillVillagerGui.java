package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import fr.uiytt.ageofempire.game.GameData;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameScoreboard;
import fr.uiytt.ageofempire.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class MillVillagerGui implements InventoryProvider {

    public final SmartInventory inventory = SmartInventory.builder()
            .id("AOE_Mill")
            .size(5, 9)
            .title(BuildingType.MILL.getDisplayName())
            .provider(this)
            .manager(AgeOfEmpire.getInvManager())
            .build();

    private GameData gameData;

    @Override
    public void init(Player player, InventoryContents contents) {

        gameData = GameManager.getGameInstance().getGameData();
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));

        contents.set(1,1, ClickableItem.of(Utils.newItemStack(Material.COOKED_SALMON, null, List.of("&e40 OR"), 32), event -> buy(player, new ItemStack(Material.COOKED_SALMON, 32), 40)));
        contents.set(2,1, ClickableItem.of(Utils.newItemStack(Material.APPLE, null, List.of("&e30 OR"), 32), event -> buy(player, new ItemStack(Material.APPLE, 32), 30)));
        contents.set(3,1, ClickableItem.of(Utils.newItemStack(Material.CARROT, null, List.of("&e20 OR"), 32), event -> buy(player, new ItemStack(Material.CARROT, 32), 20)));

        if(teamBase.getAge() >= 2) {
            contents.set(1,2, ClickableItem.of(Utils.newItemStack(Material.BREAD, null, List.of("&e60 OR"), 32), event -> buy(player, new ItemStack(Material.BREAD, 32), 60)));
            contents.set(2,2, ClickableItem.of(Utils.newItemStack(Material.RABBIT_STEW, null, List.of("&e10 OR")), event -> buy(player, new ItemStack(Material.RABBIT_STEW, 1), 10)));
            contents.set(3,2, ClickableItem.of(Utils.newItemStack(Material.BAKED_POTATO, null, List.of("&e50 OR"), 32), event -> buy(player, new ItemStack(Material.BAKED_POTATO, 32), 50)));
        }

        if(teamBase.getAge() >= 3) {
            contents.set(1,3, ClickableItem.of(Utils.newItemStack(Material.COOKED_BEEF, null, List.of("&e100 OR"), 32), event -> buy(player, new ItemStack(Material.COOKED_BEEF, 32), 100)));
            contents.set(2,3, ClickableItem.of(Utils.newItemStack(Material.COOKED_MUTTON, null, List.of("&e90 OR"), 32), event -> buy(player, new ItemStack(Material.COOKED_MUTTON, 32), 90)));
            contents.set(3,3, ClickableItem.of(Utils.newItemStack(Material.COOKED_CHICKEN, null, List.of("&e80 OR"), 32), event -> buy(player, new ItemStack(Material.COOKED_CHICKEN, 32), 80)));
        }

        if(teamBase.getAge() == 4) {
            contents.set(1,4, ClickableItem.of(Utils.newItemStack(Material.GOLDEN_CARROT, null, List.of("&e200 OR"), 32), event -> buy(player, new ItemStack(Material.GOLDEN_CARROT, 32), 200)));
            contents.set(2,4, ClickableItem.of(Utils.newItemStack(Material.CAKE, null, List.of("&e200 OR"), 5), event -> buy(player, new ItemStack(Material.CAKE), 200)));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    private void buy(Player player, ItemStack itemStack, int goldPrice) {
        UUID playerUUID = player.getUniqueId();
        int gold = gameData.getGold().get(playerUUID);

        if(goldPrice > gold) {
            player.sendMessage("Vous n'avez pas assez d'or.");
            return;
        }

        player.getInventory().addItem(itemStack);
        gameData.getGold().put(playerUUID, gold - goldPrice);
        GameScoreboard.getPlayersScoreboard().get(playerUUID).updateGoldAmmount(gold - goldPrice);

    }
}
