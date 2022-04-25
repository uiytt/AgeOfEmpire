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

public class ArmoryVillagerGUI extends VillagerGUI {

    public ArmoryVillagerGUI() {
        super.inventory = SmartInventory.builder()
                .id("AOE_Armory")
                .size(6, 9)
                .title(BuildingType.ARMORY.getDisplayName())
                .provider(this)
                .manager(AgeOfEmpire.getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();


        GUIUtils.addBuyableItem(contents, player, 1, 1, Material.LEATHER_HELMET, 20);
        GUIUtils.addBuyableItem(contents, player, 2, 1, Material.LEATHER_CHESTPLATE, 15);
        GUIUtils.addBuyableItem(contents, player, 3, 1, Material.LEATHER_LEGGINGS, 15);
        GUIUtils.addBuyableItem(contents, player, 4, 1, Material.LEATHER_BOOTS, 15);

        if (teamBase.getAge() >= 2) {
            GUIUtils.addBuyableItem(contents, player, 1, 2, Material.IRON_HELMET, 45);
            GUIUtils.addBuyableItem(contents, player, 2, 2, Material.IRON_CHESTPLATE, 60);
            GUIUtils.addBuyableItem(contents, player, 3, 2, Material.IRON_LEGGINGS, 50);
            GUIUtils.addBuyableItem(contents, player, 4, 2, Material.IRON_BOOTS, 45);
        }

        if (teamBase.getAge() >= 3) {
            GUIUtils.addBuyableItem(contents, player, 1, 3, Material.DIAMOND_HELMET, 90);
            GUIUtils.addBuyableItem(contents, player, 2, 3, Material.DIAMOND_CHESTPLATE, 110);
            GUIUtils.addBuyableItem(contents, player, 3, 3, Material.DIAMOND_LEGGINGS, 105);
            GUIUtils.addBuyableItem(contents, player, 4, 3, Material.DIAMOND_BOOTS, 90);

        }

        if (teamBase.getAge() >= 4) {
            GUIUtils.addBuyableItem(contents, player, 2, 4, Material.NETHERITE_CHESTPLATE, 250);
            GUIUtils.addBuyableItem(contents, player, 2, 4, Material.NETHERITE_BOOTS, 200);
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
