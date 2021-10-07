package fr.uiytt.ageofempire.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ColorLink {

	RED("Rouge", Material.RED_WOOL,ChatColor.DARK_RED,Material.RED_BANNER, org.bukkit.ChatColor.RED),
	YELLOW("Jaune", Material.YELLOW_WOOL,ChatColor.YELLOW,Material.YELLOW_BANNER, org.bukkit.ChatColor.YELLOW),
	BLUE("Bleu", Material.BLUE_WOOL,ChatColor.DARK_BLUE,Material.BLUE_BANNER, org.bukkit.ChatColor.DARK_BLUE),
	GREEN("Vert", Material.GREEN_WOOL,ChatColor.GREEN,Material.GREEN_BANNER, org.bukkit.ChatColor.GREEN);

	private final String name;
	private final Material wool;
	private final ChatColor chat;
	private final Material banner;
	private final org.bukkit.ChatColor tabColor;

	/**
	 * Set of colors with associated materials, chat color, etc..
	 * @param name name of the color
	 * @param wool Material for the wool of this color
	 * @param chat ChatColor
	 * @param banner Material for the Banner of this color
	 * @param tabColor org.bukkit.ChatColor, indicates the color of the team in the tab
	 */
	ColorLink(String name, Material wool, ChatColor chat, Material banner,org.bukkit.ChatColor tabColor) {
		this.name = name;
		this.wool = wool;
		this.chat = chat;
		this.banner = banner;
		this.tabColor = tabColor;
	}

	/**
	 * From a string with ONLY the color code, find the colorLink
	 * @param string two characters, one of them should be a "&" the other either r,e,1,a
	 * @return ColorLink if found, or null if not found
	 */
	@Nullable
	public static ColorLink getColorFromString(@NotNull String string) {
		ChatColor color = ChatColor.getByChar(string.charAt(1));

		if(color == null) return null;
		for(ColorLink colorLink : ColorLink.values()) {
			if(colorLink.getChatColor().getName().equals(color.getName())) return colorLink;
		}
		return null;
	}

	public String getName() {return name;}
	public Material getWool() {
		return wool;
	}
	public ChatColor getChatColor() {
		return chat;
	}
	public Material getBanner() {
		return banner;
	}
	public org.bukkit.ChatColor getTabColor() {return tabColor;}
}
