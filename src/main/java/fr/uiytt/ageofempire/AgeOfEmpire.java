package fr.uiytt.ageofempire;

import fr.minuskube.inv.InventoryManager;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.game.GameTeam;
import fr.uiytt.ageofempire.listeners.GameListener;
import fr.uiytt.ageofempire.listeners.VillagerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class AgeOfEmpire extends JavaPlugin {

    private static AgeOfEmpire instance;
    private static Logger logger;
    private static InventoryManager InvManager;
    public static long time;

    public void onEnable() {
        instance = this;
        logger = getLogger();

        //Hook in the SmartInv API for GUI
        InvManager = new InventoryManager(this);
        InvManager.init();

        getCommand("ageofempire").setExecutor(new Command());
        getCommand("ageofempire").setTabCompleter(new Command());
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new VillagerListener(), this);


        ConfigManager.init();
        ConfigManager.setWorld(Bukkit.getWorld("world"));

        //Create a new empty instance of the game to avoid null error
        GameManager.setGameInstance(new GameManager());

        //Reorganize teams in case of /reload
        GameTeam.reorganizeTeam();
    }



    public static AgeOfEmpire getInstance() {
        return instance;
    }
    public static Logger getLog() {return logger;}
    public static InventoryManager getInvManager() {
        return InvManager;
    }
}
