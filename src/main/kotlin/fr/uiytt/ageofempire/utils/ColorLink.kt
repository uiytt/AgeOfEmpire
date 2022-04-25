package fr.uiytt.ageofempire.utils

import net.md_5.bungee.api.ChatColor
import org.bukkit.Material

enum class ColorLink
/**
 * Set of colors with associated materials, chat color, etc..
 * @param stringName name of the color
 * @param wool Material for the wool of this color
 * @param chatColor ChatColor
 * @param banner Material for the Banner of this color
 * @param tabColor org.bukkit.ChatColor, indicates the color of the team in the tab
 */
    (private val stringName: String, val wool: Material, val chatColor: ChatColor, val banner: Material, val tabColor: org.bukkit.ChatColor) {
    RED("Rouge", Material.RED_WOOL, ChatColor.DARK_RED, Material.RED_BANNER, org.bukkit.ChatColor.RED),
    YELLOW("Jaune", Material.YELLOW_WOOL, ChatColor.YELLOW, Material.YELLOW_BANNER, org.bukkit.ChatColor.YELLOW),
    BLUE("Bleu", Material.BLUE_WOOL, ChatColor.DARK_BLUE, Material.BLUE_BANNER, org.bukkit.ChatColor.DARK_BLUE),
    GREEN("Vert", Material.GREEN_WOOL, ChatColor.GREEN, Material.GREEN_BANNER, org.bukkit.ChatColor.GREEN);

    companion object {

        /**
         * From a string with ONLY the color code, find the colorLink
         * @param string two characters, one of them should be a "&" the other either r,e,1,a
         * @return ColorLink if found, or null if not found
         */
        @JvmStatic
        fun getColorFromString(string: String): ColorLink? {
            val color = ChatColor.getByChar(string[1]) ?: return null
            for (colorLink in values()) {
                if (colorLink.chatColor.name == color.name) return colorLink
            }
            return null
        }
    }
}