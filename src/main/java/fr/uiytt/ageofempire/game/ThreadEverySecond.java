package fr.uiytt.ageofempire.game;

import fr.uiytt.ageofempire.ConfigManager;
import fr.uiytt.ageofempire.base.Building;
import fr.uiytt.ageofempire.base.BuildingType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ThreadEverySecond {

	/**
	 * Thread run every second that time the game and handle things like pvp timer and other.
	 * The thread automatically stop itself when the GameManager instance is no longer active.
	 * @param plugin Instance of the plugin
	 * @param game Current instance of {@link GameManager}
	 */
	public void init(Plugin plugin, GameManager game) {
		new SecondRunnable(game).runTaskTimer(plugin, 1, 20);
	}

	private static class SecondRunnable extends BukkitRunnable {
		private final int pvpTimer;
		private final int assaultTimer;
		private int secondFromStart = 0;
		private int timeMinutes = 0;
		private final GameManager game;

		private SecondRunnable(GameManager game) {
			pvpTimer = ConfigManager.getPvpTimer();
			assaultTimer = ConfigManager.getAssaultTimer();
			this.game = game;
		}

		@Override
		public void run() {
			//Get gamedata and stop game if the game is not running
			GameData gamedata = game.getGameData();

			if (!gamedata.isGameRunning()) {
				this.cancel();
				return;
			}

			//Increment timer
			secondFromStart += 1;
			GameScoreboard.updateGlobalTimer(secondFromStart);

			//Update pvp timer
			if (secondFromStart <= pvpTimer) {
				GameScoreboard.updatePvpTimer(pvpTimer - secondFromStart);
				if (secondFromStart == pvpTimer - 5) {
					game.enablePVP();
				}
			}

			//Update assault timer and events
			if (secondFromStart <= assaultTimer) {
				GameScoreboard.updateAssaultTimer(assaultTimer - secondFromStart);
				if (secondFromStart == assaultTimer - 5) {
					game.enableAssaults();
				}
			}

			timeMinutes += 1;

			if(timeMinutes != 60) return;
			timeMinutes = 0;

			for(GameTeam team : gamedata.getTeams()) {
				HashMap<BuildingType, Building> buildings = team.getTeamBase().getBuilds();
				if(buildings.get(BuildingType.MINE).isConstructed()) {
					team.getTeamBase().addStone(20);
					team.getTeamBase().updateTeamScoreboard();
				}
				if(buildings.get(BuildingType.SAWMILL).isConstructed()) {
					team.getTeamBase().addWood(20);
					team.getTeamBase().updateTeamScoreboard();
				}
				if(buildings.get(BuildingType.BANK).isConstructed()) {
					team.getPlayersUUIDs().forEach(playerUUID -> GameManager.getGameInstance().getGameData().addGold(playerUUID, 20));
					team.getTeamBase().updateTeamScoreboard();
				}
			}
		}

	}
}
