package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.gui.GUIUtils.addBuyableItem
import org.bukkit.Material
import org.bukkit.entity.Player

class ArmoryVillagerGUI : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Armory")
            .size(6, 9)
            .title(BuildingType.ARMORY.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)
        val teamBase = gameData.playersTeam[player.uniqueId]!!.teamBase
        addBuyableItem(contents, player, 1, 1, Material.LEATHER_HELMET, 20)
        addBuyableItem(contents, player, 2, 1, Material.LEATHER_CHESTPLATE, 15)
        addBuyableItem(contents, player, 3, 1, Material.LEATHER_LEGGINGS, 15)
        addBuyableItem(contents, player, 4, 1, Material.LEATHER_BOOTS, 15)
        if (teamBase.age >= 2) {
            addBuyableItem(contents, player, 1, 2, Material.IRON_HELMET, 45)
            addBuyableItem(contents, player, 2, 2, Material.IRON_CHESTPLATE, 60)
            addBuyableItem(contents, player, 3, 2, Material.IRON_LEGGINGS, 50)
            addBuyableItem(contents, player, 4, 2, Material.IRON_BOOTS, 45)
        }
        if (teamBase.age >= 3) {
            addBuyableItem(contents, player, 1, 3, Material.DIAMOND_HELMET, 90)
            addBuyableItem(contents, player, 2, 3, Material.DIAMOND_CHESTPLATE, 110)
            addBuyableItem(contents, player, 3, 3, Material.DIAMOND_LEGGINGS, 105)
            addBuyableItem(contents, player, 4, 3, Material.DIAMOND_BOOTS, 90)
        }
        if (teamBase.age >= 4) {
            addBuyableItem(contents, player, 2, 4, Material.NETHERITE_CHESTPLATE, 250)
            addBuyableItem(contents, player, 2, 4, Material.NETHERITE_BOOTS, 200)
        }
    }

    override fun update(player: Player, contents: InventoryContents) {}
}