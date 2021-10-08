package fr.uiytt.ageofempire.utils;

import fr.uiytt.ageofempire.ConfigManager;
import org.bukkit.Location;

public class ConfigParser {

    /**
     * For a given string of coordinates, return a location in the Config World
     * @param locString a string either "x y z" or "x y z yaw pitch"
     * @return a location in {@link ConfigManager#getWorld()}
     */
    public static Location stringToLocation(String locString) {
        String[] string = locString.split(" ");

        try {
            Location loc = new Location(ConfigManager.getWorld(), Double.parseDouble(string[0]), Double.parseDouble(string[1]), Double.parseDouble(string[2]));
            if (string.length > 3) {
                loc.setYaw(Float.parseFloat(string[3]));
                loc.setPitch(Float.parseFloat(string[4]));
            }
            return loc;
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return new Location(ConfigManager.getWorld(),0d,80d,0d);
        }

    }
}
