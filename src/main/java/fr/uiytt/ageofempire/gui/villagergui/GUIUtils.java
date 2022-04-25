package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameScoreboard;
import fr.uiytt.ageofempire.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class GUIUtils {
    public static void initGui(InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of(""))));
    }

    public static void addBuyableItem(InventoryContents contents, Player player, int row, int col, Material material, int goldPrice, int amount) {
        ItemStack item = new ItemStack(material, amount);
        addGoldToLore(item, goldPrice);
        contents.set(row, col, ClickableItem.of(item,
                event -> buy(player, new ItemStack(material, amount), goldPrice)));
    }

    public static void addBuyableItem(InventoryContents contents, Player player, int row, int col, Material material, int goldPrice) {
        addBuyableItem(contents, player, row, col, material, goldPrice, 1);
    }

    private static void addGoldToLore(ItemStack item, int goldPrice){
        String priceInLoreStr = ChatColor.COLOR_CHAR + "e" + goldPrice + " OR";
        if(item.getItemMeta() != null){
            ItemMeta meta = item.getItemMeta();
            if(meta.hasLore()) {
                List<String> lore = item.getItemMeta().getLore();
                lore.add(0, priceInLoreStr);
            }else{
                meta.setLore(List.of(priceInLoreStr));
            }
            item.setItemMeta(meta);
        }
    }

    /**
     * Adds a buyable item. By default it will strip the item sold of its lore but the lore will still be shown on the GUI.
     * @param contents
     * @param player
     * @param row
     * @param col
     * @param item
     * @param goldPrice
     */
    public static void addBuyableItem(InventoryContents contents, Player player, int row, int col, ItemStack item, int goldPrice) {
        addGoldToLore(item, goldPrice);

        ItemStack noLoreItem = item.clone();
        if (noLoreItem.getItemMeta() != null)
            noLoreItem.getItemMeta().setLore(List.of(""));

        contents.set(row, col, ClickableItem.of(item, event -> buy(player, noLoreItem, goldPrice)));
    }


    /**
     * Adds a buyable item. By default it will strip the item sold of its lore but the lore will still be shown on the GUI.
     * @param contents
     * @param player
     * @param row
     * @param col
     * @param item
     * @param goldPrice
     */
    public static void addBuyableItem(InventoryContents contents, Player player, int row, int col, ItemStack item, int goldPrice, List<String> itemLore) {
        addGoldToLore(item, goldPrice);

        ItemStack noLoreItem;
        if (item.getItemMeta() != null)
            noLoreItem = Utils.newItemStack(item.getType(), item.getItemMeta().getDisplayName(), itemLore, item.getAmount());
        else
            noLoreItem = Utils.newItemStack(item.getType(), null, itemLore, item.getAmount());

        contents.set(row, col, ClickableItem.of(item, event -> buy(player, noLoreItem, goldPrice)));
    }

    /**
     * Buy item for gold
     *
     * @param player    Player buying the item
     * @param itemStack Item to buy
     * @param goldPrice Ammount of gold
     */
    public static void buy(Player player, ItemStack itemStack, int goldPrice) {
        var gameData = GameManager.getGameInstance().getGameData();
        UUID playerUUID = player.getUniqueId();
        int gold = gameData.getGold().get(playerUUID);

        if (goldPrice > gold) {
            player.sendMessage("Vous n'avez pas assez d'or.");
            return;
        }

        player.getInventory().addItem(itemStack);
        gameData.getGold().put(playerUUID, gold - goldPrice);
        GameScoreboard.getPlayersScoreboard().get(playerUUID).updateGoldAmmount(gold - goldPrice);

    }


}
