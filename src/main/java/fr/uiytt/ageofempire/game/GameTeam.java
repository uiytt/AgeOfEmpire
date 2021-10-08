package fr.uiytt.ageofempire.game;

import fr.uiytt.ageofempire.base.TeamBase;
import fr.uiytt.ageofempire.utils.ColorLink;
import fr.uiytt.ageofempire.utils.PlayerFromUUIDNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameTeam {

	private final List<UUID> playersUUIDs = new ArrayList<>();

	private final ColorLink color;
	private final String name;
	private TeamBase teamBase;

	/**
	 * A team of players, and their base
	 * Teams already exist before the game start, to allow players to join.
	 * @param color {@link ColorLink} of the team
	 * @param name Name of the team
	 *
	 * @see GameData for all the instances of the teams
	 */
	public GameTeam(ColorLink color, String name) {
		this.color = color;
		this.name = name;
	}

	/**
	 * At the start of game, assign a new {@link TeamBase}
	 */
	public void registerTeamBase() {
		teamBase = new TeamBase(this);
	}

	/**
	 * This return only a COPY of the list of players, you cannot modify the players here,
	 * @see #addPlayer(UUID)
	 * @see #removePlayer(UUID)
	 */
	public List<UUID> getPlayersUUIDs() {
		return List.copyOf(playersUUIDs);
	}
	
	/**
	 * This adds a player to the team
	 * @param playerUUID if of the player, produce an error if the player is not online
	 */
	public void addPlayer(UUID playerUUID) throws PlayerFromUUIDNotFoundException {
		Player player = Bukkit.getPlayer(playerUUID);
		if(player == null) {
			throw new PlayerFromUUIDNotFoundException(playerUUID);
		}
		player.setPlayerListName(color.getTabColor() + player.getDisplayName());
		playersUUIDs.add(playerUUID);
		GameManager.getGameInstance().getGameData().getPlayersTeam().put(playerUUID, this);
	}

	public void removePlayer(UUID playerUUID) {
		Player player = Bukkit.getPlayer(playerUUID);
		//player.setPlayerListName();
		playersUUIDs.remove(playerUUID);
		GameManager.getGameInstance().getGameData().getPlayersTeam().remove(playerUUID);
	}

	public static void removePlayerFromAllTeams(UUID playerUUID) {
		GameTeam team = GameManager.getGameInstance().getGameData().getPlayersTeam().get(playerUUID);
		if(team != null) {
			team.removePlayer(playerUUID);
		}
	}

	/**
	 * Remove all players from this team.
	 */
	public void removeAllPlayers() {
		GameManager.getGameInstance().getGameData().getPlayersTeam().clear();
		playersUUIDs.clear();
	}
	
	/**
	 * This register new teams depending on the number of player, the size of the teams etc...
	 */
	public static void reorganizeTeam() {
		GameData gameData = GameManager.getGameInstance().getGameData();
		int numberTeam = Bukkit.getOnlinePlayers().size() >= 16 ? 4 : 2;
		if(numberTeam == gameData.getTeams().size()) {return;}

		gameData.getTeams().forEach(GameTeam::removeAllPlayers);
		gameData.getTeams().clear();

		List<ColorLink> colors = Arrays.asList(ColorLink.values());

		for(int i=0;i<numberTeam;i++ ) {
			ColorLink color = colors.get(i);
			gameData.getTeams().add(new GameTeam(color,color.getName()));
		}
		
	}

	public TeamBase getTeamBase() {return teamBase;}
	public ColorLink getColor() {
		return color;
	}
	public String getName() {
		return name;
	}
}
