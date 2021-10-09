package fr.uiytt.ageofempire.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class GameData {

	private boolean gameRunning = false;
	private boolean pvp = false;

	private List<UUID> alivePlayers = new ArrayList<>();
	private final List<GameTeam> teams = new ArrayList<>();
	private final HashMap<UUID, GameTeam> playersTeam = new HashMap<>();
	private final HashMap<UUID, Integer> gold = new HashMap<>();



	public boolean isGameRunning() { return gameRunning;}
	public void setGameRunning(boolean gameRunning) { this.gameRunning = gameRunning; }
	public List<UUID> getAlivePlayers() { return alivePlayers; }
	public void setAlivePlayers(List<UUID> alivePlayers) { this.alivePlayers = alivePlayers; }
	public boolean isPvp() { return pvp; }
	public void setPvp(boolean pvp) { this.pvp = pvp; }
	public List<GameTeam> getTeams() {
		return teams;
	}
	public HashMap<UUID,Integer> getGold() {return gold;}

	/**
	 * @return HashMap of team of each player
	 */
	public HashMap<UUID, GameTeam> getPlayersTeam() {
		return playersTeam;
	}

}