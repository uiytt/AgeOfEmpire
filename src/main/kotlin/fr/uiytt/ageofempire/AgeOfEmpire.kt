package fr.uiytt.ageofempire

import fr.minuskube.inv.InventoryManager
import fr.uiytt.ageofempire.game.GameManager
import fr.uiytt.ageofempire.game.GameTeam
import fr.uiytt.ageofempire.listeners.GameListener
import fr.uiytt.ageofempire.listeners.VillagerListener
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
        server.pluginManager.registerEvents(GameListener(), this)
        server.pluginManager.registerEvents(VillagerListener(), this)
        configManager.init()
        configManager.world = Bukkit.getWorld("world")

        //Reorganize teams in case of /reload
        GameTeam.reorganizeTeam()
    }

    companion object {
        val configManager = ConfigManager()
        var gameManager: GameManager = GameManager()
        lateinit var instance: AgeOfEmpire
            private set
        lateinit var log: Logger
            private set
        lateinit var invManager: InventoryManager
            private set
    }
}