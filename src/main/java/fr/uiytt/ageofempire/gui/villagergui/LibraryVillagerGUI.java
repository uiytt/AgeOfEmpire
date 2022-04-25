package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LibraryVillagerGUI extends VillagerGUI {

    public LibraryVillagerGUI() {
        super.inventory = SmartInventory.builder()
                .id("AOE_Library")
                .size(6, 9)
                .title(BuildingType.LIBRARY.getDisplayName())
                .provider(this)
                .manager(AgeOfEmpire.getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        GUIUtils.addBuyableItem(contents, player, 1, 1, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 1, null), 60);
        GUIUtils.addBuyableItem(contents, player, 2, 1, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 1, null), 50);
        GUIUtils.addBuyableItem(contents, player, 3, 1, enchantingBookItemBuilding(Enchantment.DURABILITY, 1, null), 20);
        GUIUtils.addBuyableItem(contents, player, 4, 1, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 1, null), 30);

        if (teamBase.getAge() >= 3) {
            GUIUtils.addBuyableItem(contents, player, 1, 2, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 2, null), 110);
            GUIUtils.addBuyableItem(contents, player, 2, 2, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 2, null), 100);
            GUIUtils.addBuyableItem(contents, player, 3, 2, enchantingBookItemBuilding(Enchantment.DURABILITY, 2, null), 40);
            GUIUtils.addBuyableItem(contents, player, 4, 2, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 2, null), 60);

            GUIUtils.addBuyableItem(contents, player, 1, 7, enchantingBookItemBuilding(Enchantment.ARROW_DAMAGE, 2, null), 70);
        }

        if (teamBase.getAge() >= 4) {
            GUIUtils.addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 3, null), 110);
            GUIUtils.addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 3, null), 150);
            GUIUtils.addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.DURABILITY, 3, null), 65);
            GUIUtils.addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 3, null), 90);

            GUIUtils.addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.FROST_WALKER, 3, null), 50);
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    @NotNull
    private ItemStack enchantingBookItemBuilding(@NotNull Enchantment enchantment, @NotNull int level, @Nullable List<String> lore) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        if (lore != null) {
            ItemMeta meta = book.getItemMeta();
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
            book.setItemMeta(meta);
        }
        EnchantmentStorageMeta enchantingmeta = (EnchantmentStorageMeta) book.getItemMeta();
        enchantingmeta.addStoredEnchant(enchantment, level, true);
        book.setItemMeta(enchantingmeta);
        return book;
    }
}
