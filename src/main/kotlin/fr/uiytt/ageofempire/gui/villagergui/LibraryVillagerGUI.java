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
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));

        contents.set(1,1, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 1, List.of("&e60 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 1, null), 60)));
        contents.set(2,1, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DIG_SPEED, 1, List.of("&e50 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 1, null), 50)));
        contents.set(3,1, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DURABILITY, 1, List.of("&e20 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DURABILITY, 1, null), 20)));
        contents.set(4,1, ClickableItem.of(enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 1, List.of("&e30 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 1, null), 30)));

        if(teamBase.getAge() >= 3) {
            contents.set(1,2, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 2, List.of("&e110 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 2, null), 110)));
            contents.set(2,2, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DIG_SPEED, 2, List.of("&e100 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 2, null), 100)));
            contents.set(3,2, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DURABILITY, 2, List.of("&e40 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DURABILITY, 2, null), 40)));
            contents.set(4,2, ClickableItem.of(enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 2, List.of("&e60 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 1, null), 60)));

            contents.set(1, 7, ClickableItem.of(enchantingBookItemBuilding(Enchantment.ARROW_DAMAGE, 1, List.of("&e70 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DURABILITY, 1, null), 70)));
        }

        if(teamBase.getAge() >= 4) {
            contents.set(1,3, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 3, List.of("&e110 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 3, null), 110)));
            contents.set(2,3, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DIG_SPEED, 3, List.of("&e150 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 3, null), 150)));
            contents.set(3,3, ClickableItem.of(enchantingBookItemBuilding(Enchantment.DURABILITY, 3, List.of("&e65 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.DURABILITY, 3, null), 65)));
            contents.set(4,3, ClickableItem.of(enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 3, List.of("&e90 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 3, null), 90)));

            contents.set(2,7, ClickableItem.of(enchantingBookItemBuilding(Enchantment.FROST_WALKER, 1, List.of("&e50 OR")), event -> buy(player, enchantingBookItemBuilding(Enchantment.FROST_WALKER, 1, null), 50)));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    @NotNull
    private ItemStack enchantingBookItemBuilding(@NotNull Enchantment enchantment, @NotNull int level, @Nullable List<String> lore) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        if(lore != null) {
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
