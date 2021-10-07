package fr.uiytt.ageofempire.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static ItemStack newItemStack(@NotNull Material material, @Nullable String name, @Nullable List<String> lore, @NotNull int ammount ) {
        ItemStack itemStack = new ItemStack(material,ammount);
        ItemMeta meta = itemStack.getItemMeta();
        if(meta != null) {
            if(lore != null) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(coloredLore);
            }
            if(name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public static ItemStack newItemStack(@NotNull Material material, @Nullable String name, @Nullable List<String> lore) {
        return newItemStack(material, name, lore, 1);
    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }
}
