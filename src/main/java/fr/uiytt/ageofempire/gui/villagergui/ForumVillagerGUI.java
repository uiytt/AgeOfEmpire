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
import org.bukkit.Bukkit;
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

        if(teamBase.getAge() >= 2) {
            contents.set(2, 2, buildItemStackForBuilds(BuildingType.MINE, Material.ANDESITE, List.of("&7Vous donne 20 de stone", "&7toutes les minutes.")));
            contents.set(2, 3, buildItemStackForBuilds(BuildingType.SAWMILL, Material.OAK_LOG, List.of("&7Vous donne 20 de bois", "&7toutes les minutes.")));
            contents.set(2, 4, buildItemStackForBuilds(BuildingType.LIBRARY, Material.BOOKSHELF, List.of("&7Vous permet de vous enchanter")));
        }

        ClickableItem ageItem;
        switch (teamBase.getAge()) {
            case 2 -> {
                ageItem = ClickableItem.of(Utils.newItemStack(Material.GOLD_INGOT, "&ePasser à l'âge 3", List.of(
                                "&7&lÂGE 3 :",
                                "&a400 de pierres",
                                "&a450 de bois"))
                        , event -> upgradeAge(3, 400,450));
            }
            case 3 -> {
                ageItem = ClickableItem.of(Utils.newItemStack(Material.DIAMOND, "&ePasser à l'âge 4", List.of(
                                "&7&lÂGE 4 :",
                                "&a1300 de pierres",
                                "&a1000 de bois"))
                        , event -> upgradeAge(4,1300,1000));
            }
            case 4 -> {
                ageItem = ClickableItem.empty(Utils.newItemStack(Material.IRON_INGOT, "&eVous êtes Âge 4", null));
            }
            default -> ageItem = ClickableItem.of(Utils.newItemStack(Material.IRON_INGOT, "&ePasser à l'âge 2", List.of(
                    "&7&lÂGE 2 :",
                    "&a200 de pierres",
                    "&a250 de bois"))
                    , event -> upgradeAge(2, 200,250));
        }
        contents.set(4,4, ageItem);
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

    private void upgradeAge(int age, int costStone, int costWood) {
        if(teamBase.getAge() + 1 != age) {
            player.closeInventory();
            player.sendMessage("Vous êtes déjà à cette âge.");
            return;
        }
        if(teamBase.getStone() < costStone || teamBase.getWood() < costWood) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez de ressources.");
            return;
        }
        teamBase.setAge(teamBase.getAge() + 1);
        teamBase.addStone(-costStone);
        teamBase.addWood(-costWood);
        teamBase.updateTeamScoreboard();
        teamBase.getGameTeam().getPlayersUUIDs().forEach(playerUUID -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player == null) return;
            player.closeInventory();
        });
        Bukkit.broadcastMessage(teamBase.getGameTeam().getColor().getChatColor() + "L'équipe " + teamBase.getGameTeam().getName() + " est passée âge " + teamBase.getAge());
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
