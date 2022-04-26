package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.uiytt.ageofempire.game.GameData
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.gui.GUIUtils.initGui
import org.bukkit.entity.Player

abstract class VillagerGUI protected constructor() : InventoryProvider {
    protected val gameData: GameData = getGameManager().gameData
    protected lateinit var inventory: SmartInventory

    fun open(player: Player) {
        if(player.getPlayerTeam() != null) {
            inventory.open(player)
        }
    }

    override fun init(player: Player, contents: InventoryContents) {
        initGui(contents)
    }
}