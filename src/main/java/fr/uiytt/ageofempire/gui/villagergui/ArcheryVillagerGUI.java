package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.base.BuildingType;
import fr.uiytt.ageofempire.base.TeamBase;
import fr.uiytt.ageofempire.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

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
        TeamBase teamBase = gameData.getPlayersTeam().get(player.getUniqueId()).getTeamBase();

        contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));

        contents.set(1,1, ClickableItem.of(new ItemStack(Material.BOW), event -> buy(player, new ItemStack(Material.BOW),100)));
        contents.set(1,2, ClickableItem.of(new ItemStack(Material.ARROW, 8), event -> buy(player, new ItemStack(Material.ARROW, 8),20)));

        if(teamBase.getAge() >= 3) {
            contents.set(1,3, ClickableItem.of(new ItemStack(Material.SPECTRAL_ARROW, 8), event -> buy(player, new ItemStack(Material.SPECTRAL_ARROW, 8),25)));
            contents.set(1,4, ClickableItem.of(new ItemStack(Material.SHIELD), event -> buy(player, new ItemStack(Material.SHIELD),60)));
        }

        if(teamBase.getAge() >= 4) {
            ItemStack harmArrow = new ItemStack(Material.TIPPED_ARROW, 8);
            PotionMeta potionMeta = (PotionMeta) harmArrow.getItemMeta();
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 2, 0), true);
            harmArrow.setItemMeta(potionMeta);
            contents.set(1,5, ClickableItem.of(harmArrow, event -> buy(player, harmArrow,45)));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
