package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.gui.GUIUtils.addBuyableItem
import org.bukkit.Material
import org.bukkit.entity.Player

class ForgeVillagerGui : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Forge")
            .size(5, 9)
            .title(BuildingType.FORGE.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)
        val teamBase = player.uniqueId.getPlayerTeam()!!.teamBase
        addBuyableItem(contents, player, 1, 1, Material.STONE_SWORD, 20)
        addBuyableItem(contents, player, 2, 1, Material.STONE_PICKAXE, 15)
        addBuyableItem(contents, player, 3, 1, Material.STONE_AXE, 15)
        if (teamBase.age >= 2) {
            addBuyableItem(contents, player, 1, 2, Material.IRON_SWORD, 60)
            addBuyableItem(contents, player, 2, 2, Material.IRON_PICKAXE, 40)
            addBuyableItem(contents, player, 3, 2, Material.IRON_AXE, 40)
        }
        if (teamBase.age >= 3) {
            addBuyableItem(contents, player, 1, 3, Material.DIAMOND_SWORD, 180)
            addBuyableItem(contents, player, 2, 3, Material.DIAMOND_PICKAXE, 100)
            addBuyableItem(contents, player, 3, 3, Material.DIAMOND_AXE, 100)
        }
        if (teamBase.age >= 4) {
            addBuyableItem(contents, player, 1, 4, Material.NETHERITE_SWORD, 300)
            addBuyableItem(contents, player, 2, 4, Material.NETHERITE_PICKAXE, 200)
            addBuyableItem(contents, player, 3, 4, Material.NETHERITE_AXE, 200)
        }
    }

    override fun update(player: Player, contents: InventoryContents) {}
}