package fr.uiytt.ageofempire.game

import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.getConfigManager
import fr.uiytt.ageofempire.utils.PlayerFromUUIDNotFoundException
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Consumer

class GameManager {

    var teamTemple: GameTeam? = null
    private lateinit var teamRunnable: BukkitRunnable
    val gameData: GameData = GameData()
    val world: World = getConfigManager().world!!

    /**
     * Start everything for the game
     * @param players List of players playing
     */
    fun init(players: List<Player>) {
        removePlayersFromConfig()

        val playersUUID: MutableList<UUID> = ArrayList()
        for (player in players) {
            playersUUID.add(player.uniqueId)
            GameScoreboard(player.uniqueId).createScoreboard(player)
        }
        gameData.alivePlayers = playersUUID
        initWorld()
        fillTeams()
        gameData.teams.forEach { it.registerTeamBase() }
        startPlayerTP(players)

        gameData.isGameRunning = true
        //Start runnable run every second (timer, etc..)
        SecondRunnable().runTaskTimer(AgeOfEmpire.instance, 1, 20)
    }

    /**
     * Fill all [GameTeam] with players who didn't join any team.
     */
    private fun fillTeams() {
        val playersUUIDs: List<UUID> = gameData.alivePlayers.toMutableList().shuffled()

        for (playerUUID in playersUUIDs) {
            if (playerUUID.getPlayerTeam() != null) continue

            var smallestTeam = gameData.teams[0]
            for (team in gameData.teams) {
                if (team.getImmuablePlayersUUIDs().size < smallestTeam.getImmuablePlayersUUIDs().size) {
                    smallestTeam = team
                }
            }
            try {
                smallestTeam.addPlayer(playerUUID)
            } catch (e: PlayerFromUUIDNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Remove all opened inventory when the game start and delete variables linked to the config GUI
     */
    private fun removePlayersFromConfig() {
        Bukkit.getOnlinePlayers().forEach { it.closeInventory() }
    }

    /**
     * Define world border and time
     */
    private fun initWorld() {
        world.time = 0
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        getConfigManager().getSetOfSeaLanterns().forEach { world.getBlockAt(it).type = Material.BEDROCK}
    }

    /**
     * Find spawn coordinates of the players, reset the player's data
     * and spawn the player.
     * @param players list of players to teleport
     */
    private fun startPlayerTP(players: List<Player>) {
        players.forEach(Consumer { player: Player ->
            player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = 20.0
            player.level = 0
            player.exp = 0f
            player.foodLevel = 20
            player.health = 20.0
            player.gameMode = GameMode.CREATIVE
            player.inventory.clear()
            player.absorptionAmount = 0.0
        })
        for (team in gameData.teams) {
            for (playerUUID in team.playersUUIDs) {
                Bukkit.getPlayer(playerUUID)?.teleport(team.teamBase.spawnTeam)
            }
        }
    }

    /**
     * End the game and reset everything
     * Always create a new instance of [GameManager] for a new game
     */
    fun stopGame() {
        val players = world.players
        val spawn = world.spawnLocation
        players.forEach {
            it.gameMode = GameMode.SPECTATOR
            it.teleport(spawn)
            it.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 4, false, false))
            GameScoreboard.removePlayerScoreboard(it)
        }
        GameScoreboard.playersScoreboard.clear()
        gameData.isGameRunning = false
        world.getEntitiesByClass(Villager::class.java).forEach {it.health = 0.0 }
        AgeOfEmpire.gameManager = GameManager()
        GameTeam.reorganizeTeam()
    }

    fun enablePVP() {
        object : BukkitRunnable() {
            override fun run() {
                //5 seconds timer
                for (i in 5 downTo 1) {
                    Bukkit.getServer().broadcastMessage("Pvp dans %s secondes".replace("%s", i.toString()))
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                object : BukkitRunnable() {
                    override fun run() {
                        gameData.isPvp = true
                        getConfigManager().getSetOfSeaLanterns().forEach { world.getBlockAt(it).type = Material.SEA_LANTERN }
                        GameScoreboard.updatePvpTimer(-1)
                        Bukkit.getServer().broadcastMessage("pvp activé")
                    }
                }.runTask(AgeOfEmpire.instance)
            }
        }.runTaskAsynchronously(AgeOfEmpire.instance)
    }

    fun enableAssaults() {
        object : BukkitRunnable() {
            override fun run() {
                //5 seconds timer
                for (i in 5 downTo 1) {
                    Bukkit.getServer().broadcastMessage("assauts dans %s secondes".replace("%s", i.toString()))
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                object : BukkitRunnable() {
                    override fun run() {
                        gameData.isAssaults = true
                        GameScoreboard.updateAssaultTimer(-1)
                        Bukkit.getServer().broadcastMessage("assauts activés")
                    }
                }.runTask(AgeOfEmpire.instance)
            }
        }.runTaskAsynchronously(AgeOfEmpire.instance)
    }

    fun triggerTemple(gameTeam: GameTeam) {
        for(team in gameData.teams){
            if(team != gameTeam) {
                team.broadcastMessage("L'équipe ${gameTeam.name} a construit un temple! Vous avez 12 minutes avant que la colère de Dieu ne s'abatte sur vous!")
                team.broadcastMessage("Vous ne pouvez plus construire de bâtiments tant que le temple est encore debout!")
            }
        }
        this.teamTemple = gameTeam
        teamRunnable = object: BukkitRunnable() {
            override fun run() {
                for(team in gameData.teams){
                    if(teamTemple != null && team != teamTemple){
                        for(player in team.playersUUIDs.map { uuid -> Bukkit.getPlayer(uuid) }){
                            if(player != null){
                                player.world.strikeLightningEffect(player.location)
                                player.health = 0.0
                            }
                        }
                    }
                }
            }
        }

        teamRunnable.runTaskLater(AgeOfEmpire.instance, 20 * 60 * 12)

    }

    fun destroyTemple() {
        if(teamTemple != null){
            teamRunnable.cancel()
            Bukkit.broadcastMessage("Le temple de l'équipe ${teamTemple!!.name} a été détruit!")
            teamTemple = null
        }
    }

    /**
     * Check if it's the end of the game, and return last team if it's the end.
     * @return null if it's not the end, or instance of [GameTeam] of the last team alive.
     */
    val isGameEnd: GameTeam?
        get() {
            var teamAlive: GameTeam? = null
            for (team in gameData.teams) {
                var playerAlive = false
                for (playerUUID in team.playersUUIDs) {
                    val player = Bukkit.getPlayer(playerUUID)
                    if (player != null && player.gameMode != GameMode.SPECTATOR && player.isOnline) {
                        playerAlive = true
                        break
                    }
                }
                if (playerAlive || team.teamBase.isForumAlive) {
                    teamAlive = if (teamAlive != null) {
                        return null
                    } else {
                        team
                    }
                }
            }
            return teamAlive
        }
}

fun getGameManager() = AgeOfEmpire.gameManager