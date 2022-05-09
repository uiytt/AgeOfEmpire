package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.gui.GUIUtils.addBuyableItem
import org.bukkit.Material
import org.bukkit.entity.Player

class MarketVillagerGui : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Market")
            .size(3, 9)
            .title(BuildingType.MILL.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)
        val teamBase = player.uniqueId.getPlayerTeam()!!.teamBase
        addBuyableItem(contents, player, 1, 1, Material.GOLDEN_APPLE, 60, 1)
        addBuyableItem(contents, player, 1, 2, Material.LILY_PAD, 60, 16)
        if (teamBase.age >= 3) {
            addBuyableItem(contents, player, 1, 3, Material.OAK_PLANKS, 100, 64)
            addBuyableItem(contents, player, 1, 4, Material.COBWEB, 50, 3)
            addBuyableItem(contents, player, 1, 5, Material.WATER_BUCKET, 50, 1)
        }
        if (teamBase.age >= 4) {
            addBuyableItem(contents, player, 1, 6, Material.END_STONE, 200, 16)
            addBuyableItem(contents, player, 2, 7, Material.LAVA_BUCKET, 200, 1)
        }
    }

    override fun update(player: Player, contents: InventoryContents) {}
}