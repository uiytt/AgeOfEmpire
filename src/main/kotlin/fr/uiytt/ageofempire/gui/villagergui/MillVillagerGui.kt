package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.gui.GUIUtils.addBuyableItem
import org.bukkit.Material
import org.bukkit.entity.Player

class MillVillagerGui : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Mill")
            .size(5, 9)
            .title(BuildingType.MILL.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)
        val teamBase = gameData.playersTeam[player.uniqueId]!!.teamBase
        addBuyableItem(contents, player, 1, 1, Material.COOKED_SALMON, 40, 16)
        addBuyableItem(contents, player, 2, 1, Material.APPLE, 30, 16)
        addBuyableItem(contents, player, 3, 1, Material.CARROT, 20, 16)
        if (teamBase.age >= 2) {
            addBuyableItem(contents, player, 1, 2, Material.BREAD, 60, 16)
            addBuyableItem(contents, player, 2, 2, Material.RABBIT_STEW, 10, 16)
            addBuyableItem(contents, player, 3, 2, Material.BAKED_POTATO, 50, 16)
        }
        if (teamBase.age >= 3) {
            addBuyableItem(contents, player, 1, 3, Material.COOKED_BEEF, 100, 16)
            addBuyableItem(contents, player, 2, 3, Material.COOKED_MUTTON, 90, 16)
            addBuyableItem(contents, player, 3, 3, Material.COOKED_CHICKEN, 80, 16)
        }
        if (teamBase.age >= 4) {
            addBuyableItem(contents, player, 1, 4, Material.GOLDEN_CARROT, 200, 16)
            addBuyableItem(contents, player, 2, 4, Material.CAKE, 200, 16)
        }
    }

    override fun update(player: Player, contents: InventoryContents) {}
}