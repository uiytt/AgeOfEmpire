package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import fr.uiytt.ageofempire.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MillVillagerGui extends VillagerGUI {

    public MillVillagerGui() {
        super.inventory = SmartInventory.builder()
                .id("AOE_Mill")
                .size(5, 9)
                .title(BuildingType.MILL.getDisplayName())
                .provider(this)
                .manager(AgeOfEmpire.getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        GUIUtils.addBuyableItem(contents, player, 1, 1, Material.COOKED_SALMON, 40, 16);
        GUIUtils.addBuyableItem(contents, player, 2, 1, Material.APPLE, 30, 16);
        GUIUtils.addBuyableItem(contents, player, 3, 1, Material.CARROT, 20, 16);

        if (teamBase.getAge() >= 2) {
            GUIUtils.addBuyableItem(contents, player, 1, 2, Material.BREAD, 60, 16);
            GUIUtils.addBuyableItem(contents, player, 2, 2, Material.RABBIT_STEW, 10, 16);
            GUIUtils.addBuyableItem(contents, player, 3, 2, Material.BAKED_POTATO, 50, 16);
        }

        if (teamBase.getAge() >= 3) {
            GUIUtils.addBuyableItem(contents, player, 1, 3, Material.COOKED_BEEF, 100, 16);
            GUIUtils.addBuyableItem(contents, player, 2, 3, Material.COOKED_MUTTON, 90, 16);
            GUIUtils.addBuyableItem(contents, player, 3, 3, Material.COOKED_CHICKEN, 80, 16);
        }

        if (teamBase.getAge() >= 4) {
            GUIUtils.addBuyableItem(contents, player, 1, 4, Material.GOLDEN_CARROT, 200, 16);
            GUIUtils.addBuyableItem(contents, player, 2, 4, Material.CAKE, 200, 16);
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
