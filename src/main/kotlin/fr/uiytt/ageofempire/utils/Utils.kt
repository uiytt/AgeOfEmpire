package fr.uiytt.ageofempire.utils

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Utils {
    @JvmStatic
    @JvmOverloads
    fun newItemStack(material: Material, name: String?, lore: List<String?>?, ammount: Int = 1): ItemStack {
        val itemStack = ItemStack(material, ammount)
        val meta = itemStack.itemMeta
        if (meta != null) {
            if (lore != null) {
                val coloredLore: MutableList<String> = ArrayList()
                for (line in lore) {
                    coloredLore.add(ChatColor.translateAlternateColorCodes('&', line!!))
                }
                meta.lore = coloredLore
            }
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name))
            }
            itemStack.itemMeta = meta
        }
        return itemStack
    }

    fun roundToHalf(d: Double): Double {
        return Math.round(d * 2) / 2.0
    }
}