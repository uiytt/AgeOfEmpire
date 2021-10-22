package fr.uiytt.ageofempire.gui.villagergui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameTeam;
import fr.uiytt.ageofempire.utils.PlayerFromUUIDNotFoundException;
import fr.uiytt.ageofempire.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamGui implements InventoryProvider {

  private SmartInventory inventory;

  public TeamGui() {
    inventory = SmartInventory.builder()
            .id("AOE_Team")
            .size(3, 9)
            .title("Team")
            .provider(this)
            .manager(AgeOfEmpire.getInvManager())
            .build();
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillBorders(ClickableItem.empty(Utils.newItemStack(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", List.of("") )));
  }

  @Override
  public void update(Player player, InventoryContents contents) {
    int[] slots = new int[]{3, 5, 1, 7};
    int i = 0;
    for(GameTeam team : GameManager.getGameInstance().getGameData().getTeams()) {
      contents.set(1, slots[i], ClickableItem.of(
          Utils.newItemStack(
              team.getColor().getBanner(),
              team.getColor().getChatColor() + team.getName(),
              loreBuilder(team),
              1),
          event -> {
            if(addPlayer(team, player)) {
              player.sendMessage("vous avez été ajouté à la team");
            }
          }
      ));
      i++;
    }

  }

  /**
   * For a given team, return a list of player and available slots in the team to be shown in the lore
   * @param team A team which may contains player
   * @return a list of players and slots for this team as Lore {@see List<String>}
   */
  private List<String> loreBuilder(GameTeam team) {
    List<String> lore = new ArrayList<>();
    for(int i=0;i<8;i++) {
      if(team.getPlayersUUIDs().size() - 1 >= i) {
        UUID playerUUID = team.getPlayersUUIDs().get(i);
        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null) {
          team.removePlayer(playerUUID);
          return loreBuilder(team);
        }
        lore.add(team.getColor().getChatColor() + "- " + player.getName());
      } else {
        lore.add(team.getColor().getChatColor() + "- _______");
      }
    }
    return lore;
  }

  /**
   * Try to add a player to a team
   * @param team the team to join
   * @param player the player that want to join
   * @return true if the player was added
   */
  private boolean addPlayer(GameTeam team, Player player) {
    if(team.getPlayersUUIDs().size() >= 8) {
      return false;
    }
    GameTeam previousTeam = GameManager.getGameInstance().getGameData().getPlayersTeam().get(player.getUniqueId());
    try {
      if(previousTeam != null) {
        previousTeam.removePlayer(player.getUniqueId());
      }
      team.addPlayer(player.getUniqueId());
    } catch (PlayerFromUUIDNotFoundException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void openGUI(Player player) {
    inventory.open(player);
  }
}
