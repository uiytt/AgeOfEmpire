package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.ConfigManager;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import fr.uiytt.ageofempire.game.GameScoreboard;
import fr.uiytt.ageofempire.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class StableVillagerGUI extends VillagerGUI {

    public StableVillagerGUI() {
        super.inventory = SmartInventory.builder()
                .id("AOE_Stable")
                .size(3, 9)
                .title(BuildingType.STABLE.getDisplayName())
                .provider(this)
                .manager(AgeOfEmpire.getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));

        contents.set(1, 1, ClickableItem.of(
                Utils.newItemStack(
                        Material.WHEAT,
                        "Cheval",
                        List.of("&e50 OR")),
                event -> buy(player, 50, t -> summonHorse(player.getLocation(), 4d, null)
        )));

        contents.set(1, 3, ClickableItem.of(
                Utils.newItemStack(
                        Material.RED_MUSHROOM,
                        "Cheval rapide",
                        List.of("&e90 OR")),
                event -> buy(player, 90, t -> summonHorse(player.getLocation(), 6d, null)
        )));

        if(teamBase.getAge() >= 4) {
            contents.set(1, 5, ClickableItem.of(
                    Utils.newItemStack(
                            Material.IRON_HORSE_ARMOR,
                            "Cheval & armure",
                            List.of("&e100 OR")),
                    event -> buy(player, 100, t -> summonHorse(player.getLocation(), 4d, Material.IRON_HORSE_ARMOR)
                    )));
            contents.set(1, 7, ClickableItem.of(
                    Utils.newItemStack(
                            Material.IRON_HORSE_ARMOR,
                            "Cheval rapide & armure",
                            List.of("&e150 OR")),
                    event -> buy(player, 150, t -> summonHorse(player.getLocation(), 6d, Material.IRON_HORSE_ARMOR)
                    )));
        }


    }

    /**
     * Summon a custom horse at the player who bought it
     * @param location player's location
     * @param speed speed of the horse
     * @param armor an itemstack of an item wearable by a horse
     */
    private void summonHorse(@NotNull Location location, double speed, @Nullable Material armor) {
        Horse horse = (Horse) ConfigManager.getWorld().spawnEntity(location, EntityType.HORSE);
        horse.setAI(false);
        Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(speed);
        if(armor != null) {
            horse.getInventory().setArmor(new ItemStack(armor));
        }
    }

    private void buy(Player player, int goldPrice, Consumer<Object> consumer) {
        UUID playerUUID = player.getUniqueId();
        int gold = gameData.getGold().get(playerUUID);

        if(goldPrice > gold) {
            player.sendMessage("Vous n'avez pas assez d'or.");
            return;
        }

        int newGold = gameData.addGold(playerUUID, -goldPrice);
        GameScoreboard.getPlayersScoreboard().get(playerUUID).updateGoldAmmount(newGold);
        consumer.accept(null);
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
