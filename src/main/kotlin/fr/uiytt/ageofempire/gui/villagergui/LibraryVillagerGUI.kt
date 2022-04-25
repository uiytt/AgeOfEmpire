package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.gui.GUIUtils.addBuyableItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class LibraryVillagerGUI : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Library")
            .size(6, 9)
            .title(BuildingType.LIBRARY.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)
        val teamBase = player.uniqueId.getPlayerTeam()!!.teamBase
        addBuyableItem(contents, player, 1, 1, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 1, null), 60)
        addBuyableItem(contents, player, 2, 1, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 1, null), 50)
        addBuyableItem(contents, player, 3, 1, enchantingBookItemBuilding(Enchantment.DURABILITY, 1, null), 20)
        addBuyableItem(contents, player, 4, 1, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 1, null), 30)
        if (teamBase.age >= 3) {
            addBuyableItem(contents, player, 1, 2, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 2, null), 110)
            addBuyableItem(contents, player, 2, 2, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 2, null), 100)
            addBuyableItem(contents, player, 3, 2, enchantingBookItemBuilding(Enchantment.DURABILITY, 2, null), 40)
            addBuyableItem(contents, player, 4, 2, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 2, null), 60)
            addBuyableItem(contents, player, 1, 7, enchantingBookItemBuilding(Enchantment.ARROW_DAMAGE, 2, null), 70)
        }
        if (teamBase.age >= 4) {
            addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.DAMAGE_ALL, 3, null), 110)
            addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.DIG_SPEED, 3, null), 150)
            addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.DURABILITY, 3, null), 65)
            addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.PROTECTION_ENVIRONMENTAL, 3, null), 90)
            addBuyableItem(contents, player, 1, 3, enchantingBookItemBuilding(Enchantment.FROST_WALKER, 3, null), 50)
        }
    }

    override fun update(player: Player, contents: InventoryContents) {}

    private fun enchantingBookItemBuilding(enchantment: Enchantment, level: Int, lore: List<String>?): ItemStack {
        val book = ItemStack(Material.ENCHANTED_BOOK)
        if (lore != null) {
            val meta = book.itemMeta
            val coloredLore: MutableList<String> = ArrayList()
            for (line in lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line))
            }
            meta!!.lore = coloredLore
            book.itemMeta = meta
        }
        val enchantingmeta = book.itemMeta as EnchantmentStorageMeta?
        enchantingmeta!!.addStoredEnchant(enchantment, level, true)
        book.itemMeta = enchantingmeta
        return book
    }
}