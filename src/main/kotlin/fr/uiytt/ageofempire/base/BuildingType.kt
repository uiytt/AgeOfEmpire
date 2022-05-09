package fr.uiytt.ageofempire.base

import fr.uiytt.ageofempire.gui.villagergui.*
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * All building with their fix data no matter the time of the game nor the team
 * @param displayName Name of the build, can be anything, do not use this to find the schem file name, use .name() instead
 * @param woodCost int
 * @param stoneCost int
 * @param size size should be either 1, 2 or 3 and indicate which plot should be used to build this building
 */
enum class BuildingType(
    val displayName: String,
    val age: Int,
    val stoneCost: Int,
    val woodCost: Int,
    val size: Int,
    val health: Double,
    val time: Int,
    private val openVillagerInventory: Consumer<Player>
) {
    FORUM("Forum",0,0,0,1,3000.0,0, Consumer { player: Player -> ForumVillagerGUI().open(player) }),
    FORGE("Forge",1,250,200,1,1500.0,120, Consumer { player: Player -> ForgeVillagerGui().open(player) }),
    MILL("Moulin",1,110,150,1,1300.0,90, Consumer { player: Player -> MillVillagerGui().open(player) }),
    ARMORY("Armurie",1,200,175,1, 1000.0,120, Consumer { player: Player -> ArmoryVillagerGUI().open(player) }),
    MINE("Mine", 2, 300, 120, 1, 800.0, 200, Consumer { }),
    SAWMILL("Scierie",2,100,250,1,800.0,200, Consumer { }),
    LIBRARY("Bibliothèque", 3, 320, 250, 2, 1100.0, 180, Consumer { player: Player -> LibraryVillagerGUI().open(player) }),
    ARCHERY("Archerie", 2, 200, 300, 2, 700.0, 150, Consumer { player: Player -> ArcheryVillagerGUI().open(player) }),
    BANK("Banque", 3, 400, 220, 2, 800.0, 200, Consumer { }),
    STABLE("Écurie", 3, 100, 400, 2, 700.0, 125, Consumer { player: Player -> StableVillagerGUI().open(player) }),
    TEMPLE("Temple", 4, 1000, 1000, 3, 1000.0, 2, Consumer { } ),
    TRAINING_CAMP("Centre d'entrainement", 2, 200, 300, 3, 1000.0, 250, Consumer { }),
    MARKET("Marché", 2, 50, 300, 2, 600.0, 150, Consumer {player: Player -> MarketVillagerGui().open(player)});

    fun openVillagerInventory(player: Player) {
        openVillagerInventory.accept(player)
    }

    companion object {
        @JvmStatic
        fun getBuildingTypeFromName(name: String?): BuildingType? {
            for (buildingType in values()) {
                if (buildingType.displayName.equals(name, ignoreCase = true)) return buildingType
            }
            return null
        }
    }
}