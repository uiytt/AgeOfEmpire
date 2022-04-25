package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.uiytt.ageofempire.game.GameData;
import fr.uiytt.ageofempire.game.GameManager;
import org.bukkit.entity.Player;

public abstract class VillagerGUI implements InventoryProvider {

    protected final GameData gameData;
    protected SmartInventory inventory;

    protected VillagerGUI() {
        this.gameData = GameManager.getGameInstance().getGameData();
    }

    public void open(Player player) {
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        GUIUtils.initGui(contents);
    }


}
