package fr.uiytt.ageofempire.game;

import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.ConfigManager;
import fr.uiytt.ageofempire.utils.PlayerFromUUIDNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameManager {
	private static GameManager gameInstance;
	private final GameData gameData;
	private final World world;

	/**
	 * Main methods linked to the game
	 * There can only be one instance of GameManager at the time, see {@link GameManager#getGameInstance()} to get it
	 * GameManager has a {@link GameData} to store all the information of the game
	 */
	public GameManager() {
		gameData = new GameData();
		world = ConfigManager.getWorld();
	}

	/**
	 * Start everything for the game
	 * @param players List of players playing
	 */
	public void init(List<Player> players) {
		List<UUID> playersUUID = new ArrayList<>();
		removePlayersFromConfig();
		for(Player player : players) {
			UUID playerUUID = player.getUniqueId();
			playersUUID.add(playerUUID);
			gameData.getGold().put(playerUUID, 0);
			new GameScoreboard(playerUUID).createScoreboard(player);
		}
		gameData.setAlivePlayers(playersUUID);
		fillTeams();
		initWorld();
		getGameData().getTeams().forEach(GameTeam::registerTeamBase);
		startPlayerTP(players);


		gameData.setGameRunning(true);
		//Start action at every second
		new ThreadEverySecond().init(AgeOfEmpire.getInstance(), this);
	}

	/**
	 * Fill all {@link GameTeam} with players who didn't join any team.
	 */
	private void fillTeams() {
		List<UUID> playersUUID = new ArrayList<>(gameData.getAlivePlayers());
		Collections.shuffle(playersUUID);
		for(UUID playerUUID : playersUUID) {
			if(gameData.getPlayersTeam().get(playerUUID) != null) {continue;}
			GameTeam smallestTeam = gameData.getTeams().get(0);
			for(GameTeam team : gameData.getTeams()) {
				if(team.getPlayersUUIDs().size() < smallestTeam.getPlayersUUIDs().size()) {
					smallestTeam = team;
				}
			}
			try {
				smallestTeam.addPlayer(playerUUID);
			} catch (PlayerFromUUIDNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Remove all opened inventory when the game start and delete variables linked to the config GUI
	 */
	private void removePlayersFromConfig() {
		Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
		//StartItemsMenu.getPlayersModifyingItems().clear();
		//StartItemsMenu.getPlayersGamemode().clear();
		//StartItemsMenu.getPlayersInventory().clear();
	}

	/**
	 * Define world border and time
	 */
	private void initWorld() {
		World world = ConfigManager.getWorld();
		world.setTime(0);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE,false);


	}

	/**
	 * Find spawn coordinates of the players, reset the player's data
	 * and spawn the player.
	 * @param players list of players to teleport
	 */
	private void startPlayerTP(List<Player> players) {
		players.forEach(player -> {
			for(PotionEffect potion : player.getActivePotionEffects()) {player.removePotionEffect(potion.getType());}
			Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20.0);
			player.setLevel(0);
			player.setExp(0);
			player.setFoodLevel(20);
			player.setHealth(20.0);
			player.setGameMode(GameMode.CREATIVE);
			player.getInventory().clear();
			player.setAbsorptionAmount(0);
		});

		for(GameTeam team : gameData.getTeams()) {
			for(UUID playerUUID : team.getPlayersUUIDs()) {
				Player player = Bukkit.getPlayer(playerUUID);
				if(player != null) {
					player.teleport(team.getTeamBase().getSpawnTeam());
				}
			}
		}

	}

	/**
	 * End the game and reset everything
	 * Always create a new instance of {@link GameManager} for a new game
	 */
	public void stopGame() {
		List<Player> players = world.getPlayers();
		Location spawn = world.getSpawnLocation();
		players.forEach(player -> {
			player.setGameMode(GameMode.CREATIVE);
			player.teleport(spawn);
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,600,4,false,false));
			GameScoreboard.removePlayerScoreboard(player);
		});

		GameScoreboard.getPlayersScoreboard().clear();
		gameData.setGameRunning(false);
		world.getEntitiesByClass(Villager.class).forEach(c -> c.setHealth(0));
		setGameInstance(new GameManager());
		GameTeam.reorganizeTeam();
	}

	public void enablePVP() {
		new BukkitRunnable() {

			@Override
			public void run() {
				//5 seconds timer
				for(int i=5;i>0;i--) {
					Bukkit.getServer().broadcastMessage("pvp dans %s secondes".replace("%s",String.valueOf(i)));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						gameData.setPvp(true);
						GameScoreboard.updatePvpTimer(-1);
						Bukkit.getServer().broadcastMessage("pvp activé");
					}
				}.runTask(AgeOfEmpire.getInstance());
			}
		}.runTaskAsynchronously(AgeOfEmpire.getInstance());
	}

	public void enableAssaults() {
		new BukkitRunnable() {

			@Override
			public void run() {
				//5 seconds timer
				for(int i=5;i>0;i--) {
					Bukkit.getServer().broadcastMessage("assauts dans %s secondes".replace("%s",String.valueOf(i)));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						gameData.setAssaults(true);
						GameScoreboard.updateAssaultTimer(-1);
						Bukkit.getServer().broadcastMessage("assauts activés");
					}
				}.runTask(AgeOfEmpire.getInstance());
			}
		}.runTaskAsynchronously(AgeOfEmpire.getInstance());
	}

	/**
	 * Check if it's the end of the game, and return last team if it's the end.
	 * @return null if it's not the end, or instance of {@link GameTeam} of the last team alive.
	 */
	public @Nullable GameTeam isGameEnd() {
		GameTeam teamAlive = null;
		for(GameTeam team : gameData.getTeams()) {
			boolean playerAlive = false;
			for (UUID playerUUID : team.getPlayersUUIDs()) {
				Player player = Bukkit.getPlayer(playerUUID);

				if(player != null && player.getGameMode() != GameMode.SPECTATOR && player.isOnline()) {
					playerAlive = true;
					break;
				}

			}

			if(playerAlive || team.getTeamBase().isForumAlive()) {
				if(teamAlive != null) {
					return null;
				} else {
					teamAlive = team;
				}
			}

		}
		return teamAlive;
	}


	public static void setGameInstance(GameManager gameManager) {
		gameInstance = gameManager;
	}
	public static GameManager getGameInstance() {
		return gameInstance;
	}
	public GameData getGameData() {
		return gameData;
	}
	public World getWorld() {
		return world;
	}
}
