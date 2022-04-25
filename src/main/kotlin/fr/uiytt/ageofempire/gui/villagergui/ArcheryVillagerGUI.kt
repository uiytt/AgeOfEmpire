package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.gui.GUIUtils.addBuyableItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ArcheryVillagerGUI : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Archery")
            .size(3, 9)
            .title(BuildingType.LIBRARY.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)
        val teamBase = player.uniqueId.getPlayerTeam()!!.teamBase
        addBuyableItem(contents, player, 1, 1, Material.BOW, 100)
        addBuyableItem(contents, player, 1, 2, Material.ARROW, 20, 8)
        if (teamBase.age >= 3) {
            addBuyableItem(contents, player, 1, 3, Material.SPECTRAL_ARROW, 25, 8)
            addBuyableItem(contents, player, 1, 4, Material.SHIELD, 60)
        }
        if (teamBase.age >= 4) {
            val harmArrow = ItemStack(Material.TIPPED_ARROW, 8)
            val potionMeta = harmArrow.itemMeta as PotionMeta?
            potionMeta!!.addCustomEffect(PotionEffect(PotionEffectType.HARM, 2, 0), true)
            harmArrow.itemMeta = potionMeta
            addBuyableItem(contents, player, 1, 5, harmArrow, 45)
        }
    }

    override fun update(player: Player, contents: InventoryContents) {}
}