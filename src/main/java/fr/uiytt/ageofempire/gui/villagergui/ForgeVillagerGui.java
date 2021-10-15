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
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));

        contents.set(1,1, ClickableItem.of(Utils.newItemStack(Material.STONE_SWORD, null, List.of("&e20 OR")), event -> buy(player, new ItemStack(Material.STONE_SWORD), 20)));
        contents.set(2,1, ClickableItem.of(Utils.newItemStack(Material.STONE_PICKAXE, null, List.of("&e15 OR")), event -> buy(player, new ItemStack(Material.STONE_PICKAXE), 15)));
        contents.set(3,1, ClickableItem.of(Utils.newItemStack(Material.STONE_AXE, null, List.of("&e15 OR")), event -> buy(player, new ItemStack(Material.STONE_AXE), 15)));

        if(teamBase.getAge() >= 2) {
            contents.set(1,2, ClickableItem.of(Utils.newItemStack(Material.IRON_SWORD, null, List.of("&e60 OR")), event -> buy(player, new ItemStack(Material.IRON_SWORD), 60)));
            contents.set(2,2, ClickableItem.of(Utils.newItemStack(Material.IRON_PICKAXE, null, List.of("&e40 OR")), event -> buy(player, new ItemStack(Material.IRON_PICKAXE), 40)));
            contents.set(3,2, ClickableItem.of(Utils.newItemStack(Material.IRON_AXE, null, List.of("&e40 OR")), event -> buy(player, new ItemStack(Material.IRON_AXE), 40)));
        }

        if(teamBase.getAge() >= 3) {
            contents.set(1,3, ClickableItem.of(Utils.newItemStack(Material.DIAMOND_SWORD, null, List.of("&e180 OR")), event -> buy(player, new ItemStack(Material.DIAMOND_SWORD), 180)));
            contents.set(2,3, ClickableItem.of(Utils.newItemStack(Material.DIAMOND_PICKAXE, null, List.of("&e100 OR")), event -> buy(player, new ItemStack(Material.DIAMOND_PICKAXE), 100)));
            contents.set(3,3, ClickableItem.of(Utils.newItemStack(Material.DIAMOND_AXE, null, List.of("&e100 OR")), event -> buy(player, new ItemStack(Material.DIAMOND_AXE), 100)));
        }

        if(teamBase.getAge() == 4) {
            contents.set(1,4, ClickableItem.of(Utils.newItemStack(Material.NETHERITE_SWORD, null, List.of("&e300 OR")), event -> buy(player, new ItemStack(Material.NETHERITE_SWORD), 300)));
            contents.set(2,4, ClickableItem.of(Utils.newItemStack(Material.NETHERITE_PICKAXE, null, List.of("&e200 OR")), event -> buy(player, new ItemStack(Material.NETHERITE_PICKAXE), 200)));
            contents.set(3,4, ClickableItem.of(Utils.newItemStack(Material.NETHERITE_AXE, null, List.of("&e200 OR")), event -> buy(player, new ItemStack(Material.NETHERITE_AXE), 200)));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }


}
