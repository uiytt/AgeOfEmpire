package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryProvider;
import fr.uiytt.ageofempire.game.GameData;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class VillagerGUI implements InventoryProvider {

    protected SmartInventory inventory;
    protected final GameData gameData = GameManager.getGameInstance().getGameData();

    public void open(Player player) {
        inventory.open(player);
    }

    /**
     * Buy item for gold
     * @param player Player buying the item
     * @param itemStack Item to buy
     * @param goldPrice Ammount of gold
     */
    protected void buy(Player player, ItemStack itemStack, int goldPrice) {
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
