package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.base.Building;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameTeam;
import fr.uiytt.ageofempire.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ForumVillagerGUI extends VillagerGUI {

    private TeamBase teamBase;
    private Player player;
    public ForumVillagerGUI() {
        super.inventory = SmartInventory.builder()
                .id("AOE_Forum")
                .size(5, 9)
                .title("Forum")
                .provider(this)
                .manager(AgeOfEmpire.getInvManager())
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        GameTeam gameTeam = GameManager.getGameInstance().getGameData().getPlayersTeam().get(player.getUniqueId());
        teamBase = gameTeam.getTeamBase();
        this.player = player;

        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));
        contents.set(0, 4, ClickableItem.empty(Utils.newItemStack(gameTeam.getColor().getWool(), "&fAge " + teamBase.getAge(), null)));

        contents.set(1, 3, buildItemStackForBuilds(BuildingType.FORGE, Material.ANVIL, List.of("&7Vous permet d'acheter des", "&7armes et des outils.")));
        contents.set(1, 4, buildItemStackForBuilds(BuildingType.MILL, Material.BREAD, List.of("&7Vous permet d'acheter de", "&7la nourriture.")));
        contents.set(1, 5, buildItemStackForBuilds(BuildingType.ARMORY, Material.IRON_CHESTPLATE, List.of("&7Vous permet d'achter des", "&7armures.")));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
    private void buyBuilding(BuildingType buildingType) {
        if(teamBase.getStone() >= buildingType.getStoneCost() && teamBase.getWood() >= buildingType.getWoodCost()) {
            teamBase.addStone(-buildingType.getStoneCost());
            teamBase.addWood(-buildingType.getWoodCost());
            teamBase.updateTeamScoreboard();
            player.getInventory().addItem(Utils.newItemStack(teamBase.getGameTeam().getColor().getWool(), buildingType.getDisplayName(), List.of("&8" + buildingType.getDisplayName(), "&8AOE")));
        } else {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez de ressources.");
        }
    }

    private ClickableItem buildItemStackForBuilds(BuildingType buildingType, Material material, List<String> fixLore) {
        Building building = teamBase.getBuilds().get(buildingType);
        List<String> lore = new ArrayList<>(fixLore);
        lore.add("");
        lore.add("&a" + buildingType.getStoneCost() + " de pierres");
        lore.add("&a" + buildingType.getWoodCost() + " de bois");
        if(buildingType.getAge() > teamBase.getAge()) {
            return ClickableItem.empty(Utils.newItemStack(Material.WHITE_STAINED_GLASS_PANE, "&f" + buildingType.getDisplayName() + "&7 - Age " + buildingType.getAge(), lore));
        } else if(building != null && !building.isAvailable()) {
            return ClickableItem.empty(Utils.newItemStack(Material.BEDROCK, "&f" + buildingType.getDisplayName() + "&7 - Déjà construit", lore));
        } else {
            return ClickableItem.of(Utils.newItemStack(material, "&e" + buildingType.getDisplayName(), lore), event -> buyBuilding(buildingType));
        }
    }


}
