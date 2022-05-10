package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.gui.GUIUtils.addBuyableItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class LaboratoryVillagerGUI : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Laboratory")
            .size(3, 9)
            .title(BuildingType.LIBRARY.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)

        addBuyableItem(contents, player, 1, 1, Material.ENDER_PEARL, 250)
        addBuyableItem(contents, player, 1, 2, Material.OAK_BOAT, 80)
        addBuyableItem(contents, player, 1, 3, potionItemBuilding(PotionEffect(PotionEffectType.HEAL, 2, 1), listOf("&7Instant health II")), 65)
        addBuyableItem(contents, player, 1, 4, potionItemBuilding(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0), listOf("&7Fire resistance")), 45)
        addBuyableItem(contents, player, 1, 5, potionItemBuilding(PotionEffect(PotionEffectType.JUMP, 60, 3), listOf("&7Jump Boost 4")), 80)
    }

    override fun update(player: Player, contents: InventoryContents) {}

    private fun potionItemBuilding(potionEffect: PotionEffect, lore: List<String>?): ItemStack {
        val potion = ItemStack(Material.SPLASH_POTION)
        val meta: PotionMeta = potion.itemMeta as PotionMeta
        if (lore != null) {
            val coloredLore: MutableList<String> = ArrayList()
            for (line in lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line))
            }
            meta.lore = coloredLore
        }

        meta.addCustomEffect(potionEffect, true)
        potion.itemMeta = meta
        return potion
    }
}