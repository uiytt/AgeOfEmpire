package fr.uiytt.ageofempire

import fr.minuskube.inv.InventoryManager
import fr.uiytt.ageofempire.game.GameManager
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.listeners.PlayerListener
import fr.uiytt.ageofempire.listeners.VillagerListener
import fr.uiytt.ageofempire.listeners.WorldListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class AgeOfEmpire : JavaPlugin() {
    override fun onEnable() {
        instance = this
        log = logger

        //Hook in the SmartInv API for GUI
        invManager = InventoryManager(this)
        invManager.init()

        getCommand("ageofempire")!!.setExecutor(Command())
        getCommand("ageofempire")!!.tabCompleter = Command()
        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(WorldListener(), this)
        server.pluginManager.registerEvents(VillagerListener(), this)

        configManager.init()
        configManager.world = Bukkit.getWorld("world")

        gameManager = GameManager()
        //Reorganize teams in case of /reload
        GameTeam.reorganizeTeam()
    }

    companion object {
        val configManager = ConfigManager()
        lateinit var  gameManager: GameManager
        lateinit var instance: AgeOfEmpire
            private set
        lateinit var log: Logger
            private set
        lateinit var invManager: InventoryManager
            private set
    }
}