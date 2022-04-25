package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.game.GameScoreboard.Companion.playersScoreboard
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.utils.Utils.newItemStack
import net.md_5.bungee.api.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class StableVillagerGUI : VillagerGUI() {
    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Stable")
            .size(3, 9)
            .title(BuildingType.STABLE.displayName)
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        val teamBase = player.uniqueId.getPlayerTeam()!!.teamBase
        contents.fillBorders(
            ClickableItem.empty(newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY.toString() + "", listOf("")))
        )
        contents[1, 1] = ClickableItem.of(
            newItemStack(Material.WHEAT,"Cheval", listOf("&e50 OR"))
        ) { buy(player, 50) { summonHorse(player.location, 4.0, null) } }
        contents[1, 3] = ClickableItem.of(
            newItemStack(Material.RED_MUSHROOM, "Cheval rapide", listOf("&e90 OR"))
        ) { buy(player, 90) { summonHorse(player.location, 6.0, null) } }

        if (teamBase.age >= 4) {
            contents[1, 5] = ClickableItem.of(
                newItemStack(Material.IRON_HORSE_ARMOR, "Cheval & armure", listOf("&e100 OR"))
            ) { buy(player, 100) { summonHorse(player.location, 4.0, Material.IRON_HORSE_ARMOR) } }
            contents[1, 7] = ClickableItem.of(
                newItemStack(Material.IRON_HORSE_ARMOR, "Cheval rapide & armure", listOf("&e150 OR"))
            ) { buy(player, 150) {summonHorse(player.location, 6.0, Material.IRON_HORSE_ARMOR) } }
        }
    }

    /**
     * Summon a custom horse at the player who bought it
     * @param location player's location
     * @param speed speed of the horse
     * @param armor an itemstack of an item wearable by a horse
     */
    private fun summonHorse(location: Location, speed: Double, armor: Material?) {
        val horse = getGameManager().world.spawnEntity(location, EntityType.HORSE) as Horse
        horse.setAI(false)
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = speed
        if (armor != null) horse.inventory.armor = ItemStack(armor)
    }

    private fun buy(player: Player, goldPrice: Int, consumer: Consumer<Any?>) {
        val playerUUID = player.uniqueId
        if (goldPrice > gameData.gold[playerUUID]!!) {
            player.sendMessage("Vous n'avez pas assez d'or.")
            return
        }
        val newGold = gameData.addGold(playerUUID, -goldPrice)
        playersScoreboard[playerUUID]!!.updateGoldAmmount(newGold)
        consumer.accept(null)
    }

    override fun update(player: Player, contents: InventoryContents) {}
}