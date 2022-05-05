package fr.uiytt.ageofempire.gui.villagergui

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.BuildingType
import fr.uiytt.ageofempire.base.TeamBase
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.game.getPlayerTeam
import fr.uiytt.ageofempire.utils.Utils.newItemStack
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

class ForumVillagerGUI : VillagerGUI() {
    private lateinit var teamBase: TeamBase
    private lateinit var player: Player

    init {
        super.inventory = SmartInventory.builder()
            .id("AOE_Forum")
            .size(5, 9)
            .title("Forum")
            .provider(this)
            .manager(AgeOfEmpire.invManager)
            .build()
    }

    override fun init(player: Player, contents: InventoryContents) {
        super.init(player, contents)
        val gameTeam: GameTeam = player.uniqueId.getPlayerTeam()!!
        this.teamBase = gameTeam.teamBase
        this.player = player
        contents[0, 4] = ClickableItem.empty(newItemStack(gameTeam.color.wool, "&fAge " + teamBase.age, null))
        contents[1, 3] = buildItemStackForBuilds(BuildingType.FORGE, Material.ANVIL, listOf("&7Vous permet d'acheter des", "&7armes et des outils."))
        contents[1, 4] = buildItemStackForBuilds(BuildingType.MILL, Material.BREAD, listOf("&7Vous permet d'acheter de", "&7la nourriture."))
        contents[1, 5] = buildItemStackForBuilds(BuildingType.ARMORY, Material.IRON_CHESTPLATE, listOf("&7Vous permet d'achter des", "&7armures."))
        contents[2, 2] = buildItemStackForBuilds(BuildingType.MINE, Material.ANDESITE, listOf("&7Vous donne 20 de stone","&7toutes les minutes."))
        contents[2, 3] = buildItemStackForBuilds(BuildingType.SAWMILL, Material.OAK_LOG, listOf("&7Vous donne 20 de bois","&7toutes les minutes."))
        contents[2, 4] = buildItemStackForBuilds(BuildingType.TRAINING_CAMP, Material.TARGET, listOf("&7Donne de l'xp régulièrement."))
        contents[2, 5] = buildItemStackForBuilds(BuildingType.ARCHERY, Material.BOW, listOf("&7Vous permet d'acheter arcs et boucliers."))
        contents[2, 6] = buildItemStackForBuilds(BuildingType.BANK, Material.GOLD_BLOCK, listOf("&7Vends blocs et pommes d'or'"))
        contents[3, 3] = buildItemStackForBuilds(BuildingType.STABLE, Material.SADDLE, listOf("&7Permet d'avoir des chevaux"))
        contents[3, 5] = buildItemStackForBuilds(BuildingType.LIBRARY, Material.BOOKSHELF, listOf("&7Vous permet de vous enchanter"))
        contents[3, 4] = buildItemStackForBuilds(BuildingType.TEMPLE, Material.BEACON, listOf("&7Provoque la colère de Dieu sur vos adversaires après 12 minutes"))
        val ageItem: ClickableItem = when (teamBase.age) {
            2 -> ClickableItem.of(newItemStack(Material.GOLD_INGOT, "&ePasser à l'âge 3", listOf(
                            "&7&lÂGE 3 :",
                            "&a400 de pierres",
                            "&a450 de bois"))
                ) { upgradeAge(3, 400, 450) }
            3 -> { ClickableItem.of(newItemStack(Material.DIAMOND, "&ePasser à l'âge 4", listOf(
                            "&7&lÂGE 4 :",
                            "&a1300 de pierres",
                            "&a1000 de bois"))
                ) { upgradeAge(4, 1300, 1000) }
            }
            4 -> ClickableItem.empty(newItemStack(Material.IRON_INGOT, "&eVous êtes Âge 4", null))
            else -> ClickableItem.of(newItemStack(Material.IRON_INGOT, "&ePasser à l'âge 2", listOf(
                        "&7&lÂGE 2 :",
                        "&a200 de pierres",
                        "&a250 de bois"))
            ) { upgradeAge(2, 200, 250) }
        }
        contents[4, 4] = ageItem
    }

    override fun update(player: Player, contents: InventoryContents) {}

    private fun buyBuilding(buildingType: BuildingType) {
        if (teamBase.stone >= buildingType.stoneCost && teamBase.wood >= buildingType.woodCost) {
            teamBase.stone -= buildingType.stoneCost
            teamBase.wood -= buildingType.woodCost
            teamBase.updateTeamScoreboard()
            player.inventory.addItem(
                newItemStack(teamBase.gameTeam.color.wool, buildingType.displayName, listOf("&8${buildingType.displayName}", "&8AOE"))
            )
        } else player.sendMessage(ChatColor.RED.toString() + "Vous n'avez pas assez de ressources.")
    }

    private fun upgradeAge(age: Int, costStone: Int, costWood: Int) {
        if (teamBase.age + 1 != age) {
            player.closeInventory()
            player.sendMessage("Vous êtes déjà à cette âge.")
            return
        }
        if (teamBase.stone < costStone || teamBase.wood < costWood) {
            player.sendMessage(ChatColor.RED.toString() + "Vous n'avez pas assez de ressources.")
            return
        }
        teamBase.age += 1
        teamBase.wood -= costWood
        teamBase.stone -= costStone
        teamBase.updateTeamScoreboard()
        teamBase.gameTeam.playersUUIDs.forEach { Bukkit.getPlayer(it)?.closeInventory() }
        Bukkit.broadcastMessage(teamBase.gameTeam.color.chatColor.toString() + "L'équipe ${teamBase.gameTeam.name} est passée âge ${teamBase.age}")
    }

    private fun buildItemStackForBuilds(buildingType: BuildingType, material: Material, fixLore: List<String?>): ClickableItem {
        val building = teamBase.builds[buildingType]
        val lore: MutableList<String?> = ArrayList(fixLore)
        lore.add("")
        lore.add("&a" + buildingType.stoneCost + " de pierres")
        lore.add("&a" + buildingType.woodCost + " de bois")
        return if (AgeOfEmpire.gameManager.teamTemple != null && AgeOfEmpire.gameManager.teamTemple != teamBase.gameTeam){
                ClickableItem.empty(
                    newItemStack(Material.RED_STAINED_GLASS, "&f" + buildingType.displayName, listOf("&cTemple ennemi en jeu"))
                )
        } else if (buildingType.age > teamBase.age) {
            ClickableItem.empty(
                newItemStack(Material.WHITE_STAINED_GLASS_PANE, "&f" + buildingType.displayName + "&7 - Age " + buildingType.age, lore))
        } else if (building != null && !building.isAvailable) {
            ClickableItem.empty(
                newItemStack(Material.BEDROCK, "&f" + buildingType.displayName + "&7 - Déjà construit", lore))
        } else {
            ClickableItem.of(
                newItemStack(material, "&e" + buildingType.displayName, lore)
            ) { buyBuilding(buildingType) }
        }
    }
}