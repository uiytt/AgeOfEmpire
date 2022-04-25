package fr.uiytt.ageofempire.utils

import fr.uiytt.ageofempire.ConfigManager
import fr.uiytt.ageofempire.getConfigManager
import org.bukkit.Location

object ConfigParser {
    /**
     * For a given string of coordinates, return a location in the Config World
     * @param locString a string either "x y z" or "x y z yaw pitch"
     * @return a location in [ConfigManager.world]
     */
    @JvmStatic
    fun stringToLocation(locString: String): Location {
        val string = locString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return try {
            val loc = Location(getConfigManager().world, string[0].toDouble(), string[1].toDouble(), string[2].toDouble())
            if (string.size > 3) {
                loc.yaw = string[3].toFloat()
                loc.pitch = string[4].toFloat()
            }
            loc
        } catch (exception: NumberFormatException) {
            exception.printStackTrace()
            Location(getConfigManager().world, 0.0, 80.0, 0.0)
        }
    }
}