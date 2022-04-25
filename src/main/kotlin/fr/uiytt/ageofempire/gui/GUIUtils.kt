package fr.uiytt.ageofempire.gui

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.game.GameScoreboard.Companion.playersScoreboard
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.utils.Utils.newItemStack
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object GUIUtils {

    @JvmStatic
    fun initGui(contents: InventoryContents) {
        contents.fillBorders(
            ClickableItem.empty(newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY.toString() + "", listOf("")))
        )
    }

    @JvmOverloads
    fun addBuyableItem(contents: InventoryContents, player: Player, row: Int, col: Int, material: Material, goldPrice: Int, amount: Int = 1) {
        val item = ItemStack(material, amount)
        addGoldToLore(item, goldPrice)
        contents[row, col] = ClickableItem.of(item) {
            buy(player, ItemStack(material, amount), goldPrice)
        }
    }

    private fun addGoldToLore(item: ItemStack, goldPrice: Int) {
        val priceInLoreStr = ChatColor.COLOR_CHAR.toString() + "e" + goldPrice + " OR"
        val meta = item.itemMeta?: return
        if (meta.hasLore()) {
            val lore = item.itemMeta!!.lore
            lore?.add(0, priceInLoreStr)
        } else {
            meta.lore = listOf(priceInLoreStr)
        }
        item.itemMeta = meta
    }

    /**
     * Adds a buyable item. By default, it will strip the item sold of its lore but the lore will still be shown on the GUI.
     * @param contents
     * @param player
     * @param row
     * @param col
     * @param item
     * @param goldPrice
     */
    fun addBuyableItem(contents: InventoryContents, player: Player, row: Int, col: Int, item: ItemStack, goldPrice: Int) {
        addGoldToLore(item, goldPrice)
        val noLoreItem = item.clone()
        if (noLoreItem.itemMeta != null) noLoreItem.itemMeta!!.lore = listOf("")
        contents[row, col] = ClickableItem.of(item) { buy(player, noLoreItem, goldPrice) }
    }

    /**
     * Adds a buyable item. By default it will strip the item sold of its lore but the lore will still be shown on the GUI.
     * @param contents
     * @param player
     * @param row
     * @param col
     * @param item
     * @param goldPrice
     */
    fun addBuyableItem(contents: InventoryContents, player: Player, row: Int, col: Int, item: ItemStack, goldPrice: Int, itemLore: List<String?>?) {
        addGoldToLore(item, goldPrice)
        val noLoreItem: ItemStack = if (item.itemMeta != null) newItemStack(item.type, item.itemMeta!!.displayName, itemLore, item.amount)
                                    else newItemStack(item.type, null, itemLore, item.amount)
        contents[row, col] = ClickableItem.of(item) { buy(player, noLoreItem, goldPrice) }
    }

    /**
     * Buy item for gold
     * @param player Player buying the item
     * @param itemStack Item to buy
     * @param goldPrice Ammount of gold
     */
    private fun buy(player: Player, itemStack: ItemStack?, goldPrice: Int) {
        val gameData = getGameManager().gameData
        val playerUUID = player.uniqueId
        val gold: Int = gameData.gold[playerUUID] ?: 0
        if (goldPrice > gold) {
            player.sendMessage("Vous n'avez pas assez d'or.")
            return
        }
        player.inventory.addItem(itemStack)
        gameData.addGold(playerUUID, -goldPrice)
        playersScoreboard[playerUUID]!!.updateGoldAmmount(gold - goldPrice)
    }
}