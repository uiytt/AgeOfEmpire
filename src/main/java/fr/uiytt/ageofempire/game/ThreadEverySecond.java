package fr.uiytt.ageofempire.game;

import fr.uiytt.ageofempire.ConfigManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ThreadEverySecond {

	public void init(Plugin plugin, GameManager game) {
		new SecondRunnable(game).runTaskTimer(plugin, 1, 20);
	}

	private static class SecondRunnable extends BukkitRunnable {
		private final int pvpTimer;
		private final int assaultTimer;
		private int secondFromStart = 0;
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
					//game.enableAssaults();
				}
			}

		}

	}
}
