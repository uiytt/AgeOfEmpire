package fr.uiytt.ageofempire.game;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameScoreboard {

	private static HashMap<UUID, GameScoreboard> playersScoreboard = new HashMap<>();

	private final UUID playerUUID;
	private final Scoreboard scoreboard;

	private final List<Score> scorelist = new ArrayList<>();

	public GameScoreboard(UUID playerUUID) {
		this.playerUUID = playerUUID;
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		scoreboard = Objects.requireNonNull(scoreboardManager).getNewScoreboard();

		playersScoreboard.put(playerUUID, this);
	}

	//Based on https://www.spigotmc.org/wiki/making-scoreboard-with-teams-no-flicker/ 
	public void createScoreboard(Player player) {

		//Title
		Objective obj = scoreboard.registerNewObjective("title","dummy",ChatColor.DARK_GRAY + "»" + ChatColor.YELLOW + "" + ChatColor.BOLD + "AgeOfEmpire" + ChatColor.DARK_GRAY + "«");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		scorelist.add(obj.getScore(ChatColor.MAGIC + "" + ChatColor.GRAY));

		//Timer
		scorelist.add(obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + "      Timer"));

		Team timeCounter = scoreboard.registerNewTeam("AOE_Timer");
		timeCounter.addEntry(ChatColor.AQUA + "");
		timeCounter.setPrefix(ChatColor.GRAY + "Timer: " + ChatColor.RED + "00:00");
		scorelist.add(obj.getScore(ChatColor.AQUA + ""));

		Team pvpCounter = scoreboard.registerNewTeam("AOE_pvp");
		pvpCounter.addEntry(ChatColor.BLACK + "");
		pvpCounter.setPrefix(ChatColor.GRAY + "Pvp: " + ChatColor.RED + "00:00");
		scorelist.add(obj.getScore(ChatColor.BLACK + ""));

		Team assaultCounter = scoreboard.registerNewTeam("AOE_assault");
		assaultCounter.addEntry(ChatColor.BOLD + "");
		assaultCounter.setPrefix(ChatColor.GRAY + "Assaut: " + ChatColor.RED + "00:00");
		scorelist.add(obj.getScore(ChatColor.BOLD + ""));

		scorelist.add(obj.getScore(ChatColor.MAGIC + "" + ChatColor.BOLD));
		//Ressources
		scorelist.add(obj.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "  Ressources"));

		Team stoneAmmount = scoreboard.registerNewTeam("AOE_stone");
		stoneAmmount.addEntry(ChatColor.BLUE + "");
		stoneAmmount.setPrefix(ChatColor.GRAY + "Pierre: " + ChatColor.RESET + "" + ChatColor.YELLOW + "0");
		scorelist.add(obj.getScore(ChatColor.BLUE + ""));

		Team woodAmmount = scoreboard.registerNewTeam("AOE_wood");
		woodAmmount.addEntry(ChatColor.DARK_BLUE + "");
		woodAmmount.setPrefix(ChatColor.GRAY + "Bois: " + ChatColor.RESET + "" + ChatColor.YELLOW + "0");
		scorelist.add(obj.getScore(ChatColor.DARK_BLUE + ""));

		Team goldAmmount = scoreboard.registerNewTeam("AOE_gold");
		goldAmmount.addEntry(ChatColor.DARK_GRAY + "");
		goldAmmount.setPrefix(ChatColor.GRAY + "" + ChatColor.BOLD + "Or: " + ChatColor.RESET + "" + ChatColor.YELLOW + "0");
		scorelist.add(obj.getScore(ChatColor.DARK_GRAY + ""));
		
		int n = scorelist.size();
		for (Score score : scorelist) {
			score.setScore(n);
			n--;
		}

		player.setScoreboard(scoreboard);
	}

	public static void updateGlobalTimer(int time) {
		playersScoreboard.forEach((uuid, gameScoreboard) -> {
			Team timeCounter = gameScoreboard.scoreboard.getTeam("AOE_Timer");
			if(timeCounter == null) {
				return;
			}
			timeCounter.setPrefix(ChatColor.GRAY + "Timer: " + ChatColor.RED + intToTime(time) );
		});
	}

	public static void updatePvpTimer(int time) {
		playersScoreboard.forEach((uuid, gameScoreboard) -> {
			Team pvpCounter = gameScoreboard.scoreboard.getTeam("AOE_pvp");
			if(pvpCounter == null) return;
			if(time != -1) {
				pvpCounter.setPrefix(ChatColor.GRAY + "Pvp: " + ChatColor.RED + intToTime(time) );
				return;
			}
			gameScoreboard.scorelist.removeIf(l -> l.getEntry().equals(ChatColor.BLACK + ""));
			gameScoreboard.scoreboard.resetScores(ChatColor.BLACK + "");
			gameScoreboard.updateScores();
		});
	}

	public static void updateAssaultTimer(int time) {
		playersScoreboard.forEach((uuid, gameScoreboard) -> {
			Team assaultTimer = gameScoreboard.scoreboard.getTeam("AOE_assault");
			if(assaultTimer == null) return;
			if(time != -1) {
				assaultTimer.setPrefix(ChatColor.GRAY + "Assaut: " + ChatColor.RED + intToTime(time));
				return;
			}
			gameScoreboard.scorelist.removeIf(l -> l.getEntry().equals(ChatColor.BOLD + ""));
			gameScoreboard.scoreboard.resetScores(ChatColor.BOLD + "");
			gameScoreboard.updateScores();
		});
	}

	public void updateStoneAmmount(int stoneAmmount) {
		Team stoneCounter = scoreboard.getTeam("AOE_stone");
		if(stoneCounter == null) return;
		stoneCounter.setPrefix(ChatColor.GREEN +  "" + ChatColor.BOLD +"Pierre: " + ChatColor.RESET + "" + ChatColor.GRAY + stoneAmmount);
	}
	public void updateWoodAmmount(int woodAmmount) {
		Team woodCounter = scoreboard.getTeam("AOE_wood");
		if(woodCounter == null) return;
		woodCounter.setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + "Bois: " + ChatColor.RESET + "" + ChatColor.GRAY + woodAmmount);
	}
	public void updateGoldAmmount(int goldAmmount) {
		Team borderCounter = scoreboard.getTeam("AOE_gold");
		if(borderCounter == null) return;
		borderCounter.setPrefix(ChatColor.YELLOW + "" + ChatColor.BOLD + "Or: " + ChatColor.RESET + "" + ChatColor.GRAY + goldAmmount);
	}

	
	private static String intToTime(int time) {
		String hour = "";
		String min = "00";
		String sec = "";
		if(time > 59) {
			int temp;
			if(time > 3599) {
				temp = (int) Math.floor(time / (float)3600);
				hour = String.valueOf(temp);
				hour += ":";
				time -= temp * 3600;
			}
			temp = (int) Math.floor(time / (float)60);
			min = String.valueOf(temp);
			time -= temp * 60;
		}
		if(time < 10) sec = "0";
		sec += String.valueOf(time);
		return hour+min+":"+sec;
	}

	private void updateScores() {
		int n = scorelist.size();
		for (Score score : scorelist) {
			Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
			assert obj != null;
			obj.getScore(score.getEntry()).setScore(n);
			n--;
		}
	}

	public static void removePlayerScoreboard(Player player) {
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		player.setScoreboard(Objects.requireNonNull(scoreboardManager).getNewScoreboard());
	}

	public static HashMap<UUID, GameScoreboard> getPlayersScoreboard() { return playersScoreboard; }
}
