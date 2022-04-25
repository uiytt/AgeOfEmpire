package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArcheryVillagerGUI extends VillagerGUI {

    public ArcheryVillagerGUI() {
        super.inventory = SmartInventory.builder()
                .id("AOE_Archery")
                .size(3, 9)
                .title(BuildingType.LIBRARY.getDisplayName())
                .provider(this)
                .manager(AgeOfEmpire.getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();


        GUIUtils.addBuyableItem(contents, player, 1, 1, Material.BOW, 100);
        GUIUtils.addBuyableItem(contents, player, 1, 2, Material.ARROW, 20, 8);

        if (teamBase.getAge() >= 3) {
            GUIUtils.addBuyableItem(contents, player, 1, 3, Material.SPECTRAL_ARROW, 25, 8);
            GUIUtils.addBuyableItem(contents, player, 1, 4, Material.SHIELD, 60);
        }

        if (teamBase.getAge() >= 4) {
            ItemStack harmArrow = new ItemStack(Material.TIPPED_ARROW, 8);
            PotionMeta potionMeta = (PotionMeta) harmArrow.getItemMeta();
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 2, 0), true);
            harmArrow.setItemMeta(potionMeta);
            GUIUtils.addBuyableItem(contents, player, 1, 5, harmArrow, 45);
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
