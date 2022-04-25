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

public class ForgeVillagerGui extends VillagerGUI {

    public ForgeVillagerGui() {
        super.inventory = SmartInventory.builder()
                .id("AOE_Forge")
                .size(5, 9)
                .title(BuildingType.FORGE.getDisplayName())
                .provider(this)
                .manager(AgeOfEmpire.getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        GUIUtils.addBuyableItem(contents, player, 1, 1, Material.STONE_SWORD, 20);
        GUIUtils.addBuyableItem(contents, player, 2, 1, Material.STONE_PICKAXE, 15);
        GUIUtils.addBuyableItem(contents, player, 3, 1, Material.STONE_AXE, 15);

        if (teamBase.getAge() >= 2) {
            GUIUtils.addBuyableItem(contents, player, 1, 2, Material.IRON_SWORD, 60);
            GUIUtils.addBuyableItem(contents, player, 2, 2, Material.IRON_PICKAXE, 40);
            GUIUtils.addBuyableItem(contents, player, 3, 2, Material.IRON_AXE, 40);
        }

        if (teamBase.getAge() >= 3) {
            GUIUtils.addBuyableItem(contents, player, 1, 3, Material.DIAMOND_SWORD, 180);
            GUIUtils.addBuyableItem(contents, player, 2, 3, Material.DIAMOND_PICKAXE, 100);
            GUIUtils.addBuyableItem(contents, player, 3, 3, Material.DIAMOND_AXE, 100);
        }

        if (teamBase.getAge() >= 4) {
            GUIUtils.addBuyableItem(contents, player, 1, 4, Material.NETHERITE_SWORD, 300);
            GUIUtils.addBuyableItem(contents, player, 2, 4, Material.NETHERITE_PICKAXE, 200);
            GUIUtils.addBuyableItem(contents, player, 3, 4, Material.NETHERITE_AXE, 200);
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }


}
