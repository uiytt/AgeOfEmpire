package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import fr.uiytt.ageofempire.utils.Utils;
import net.md_5.bungee.api.ChatColor;
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
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));

        contents.set(1,1, ClickableItem.of(Utils.newItemStack(Material.LEATHER_HELMET, null, List.of("&e20 OR")), event -> buy(player, new ItemStack(Material.LEATHER_HELMET), 20)));
        contents.set(2,1, ClickableItem.of(Utils.newItemStack(Material.LEATHER_CHESTPLATE, null, List.of("&e15 OR")), event -> buy(player, new ItemStack(Material.LEATHER_CHESTPLATE), 15)));
        contents.set(3,1, ClickableItem.of(Utils.newItemStack(Material.LEATHER_LEGGINGS, null, List.of("&e15 OR")), event -> buy(player, new ItemStack(Material.LEATHER_LEGGINGS), 15)));
        contents.set(4,1, ClickableItem.of(Utils.newItemStack(Material.LEATHER_BOOTS, null, List.of("&e15 OR")), event -> buy(player, new ItemStack(Material.LEATHER_BOOTS), 15)));


        if(teamBase.getAge() >= 2) {
            contents.set(1,2, ClickableItem.of(Utils.newItemStack(Material.IRON_HELMET, null, List.of("&e45 OR")), event -> buy(player, new ItemStack(Material.IRON_HELMET), 45)));
            contents.set(2,2, ClickableItem.of(Utils.newItemStack(Material.IRON_CHESTPLATE, null, List.of("&e60 OR")), event -> buy(player, new ItemStack(Material.IRON_CHESTPLATE), 60)));
            contents.set(3,2, ClickableItem.of(Utils.newItemStack(Material.IRON_LEGGINGS, null, List.of("&e50 OR")), event -> buy(player, new ItemStack(Material.IRON_LEGGINGS), 50)));
            contents.set(4,2, ClickableItem.of(Utils.newItemStack(Material.IRON_BOOTS, null, List.of("&e45 OR")), event -> buy(player, new ItemStack(Material.IRON_BOOTS), 45)));
        }

        if(teamBase.getAge() >= 3) {
            contents.set(1,3, ClickableItem.of(Utils.newItemStack(Material.DIAMOND_HELMET, null, List.of("&e90 OR")), event -> buy(player, new ItemStack(Material.DIAMOND_HELMET), 90)));
            contents.set(2,3, ClickableItem.of(Utils.newItemStack(Material.DIAMOND_CHESTPLATE, null, List.of("&e110 OR")), event -> buy(player, new ItemStack(Material.DIAMOND_CHESTPLATE), 110)));
            contents.set(3,3, ClickableItem.of(Utils.newItemStack(Material.DIAMOND_LEGGINGS, null, List.of("&e105 OR")), event -> buy(player, new ItemStack(Material.DIAMOND_LEGGINGS), 105)));
            contents.set(4,3, ClickableItem.of(Utils.newItemStack(Material.DIAMOND_BOOTS, null, List.of("&e90 OR")), event -> buy(player, new ItemStack(Material.DIAMOND_BOOTS), 90)));

        }

        if(teamBase.getAge() == 4) {
            contents.set(2,4, ClickableItem.of(Utils.newItemStack(Material.NETHERITE_CHESTPLATE, null, List.of("&e250 OR")), event -> buy(player, new ItemStack(Material.NETHERITE_CHESTPLATE), 250)));
            contents.set(3,4, ClickableItem.of(Utils.newItemStack(Material.NETHERITE_BOOTS, null, List.of("&e200 OR")), event -> buy(player, new ItemStack(Material.NETHERITE_BOOTS), 200)));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
