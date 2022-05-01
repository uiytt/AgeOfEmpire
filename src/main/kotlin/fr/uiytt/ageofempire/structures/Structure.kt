/*
 I used a bit of : https://gitlab.com/SamB440/Schematics-Extended/-/blob/master/src/main/java/net/islandearth/schematics/extended/Schematic.java
 to create my own system.
 */
package fr.uiytt.ageofempire.structures

import com.sk89q.worldedit.MaxChangedBlocksException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BaseBlock
import de.leonhard.storage.Yaml
import fr.uiytt.ageofempire.AgeOfEmpire
import fr.uiytt.ageofempire.base.Building
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.stringToLocation
import org.bukkit.Location
import java.io.File
import java.io.FileInputStream

/**
 * Create a Structure object handling loading and pasting of a schematic
 * At the end, it spawns a villager
 * @param plugin JavaPlugin instance
 * @param teamStructureYaml Yaml File with information such as schematic location, and villager coordinates
 * @param building [Building] of a Team, mainly to prevent multiple instancies of this building in the same team.
 * @param rightSide is the plot at the left (facing from the center)
 */
class Structure(private val plugin: AgeOfEmpire, private val teamStructureYaml: Yaml, private val building: Building, private val rightSide: Boolean) {
    private var height = 0
    private var width = 0
    private var length = 0
    private var villagerRelativeCoordinates: Location? = null
    private var xFlipped = false
    private val undergroundBlocks: LinkedHashMap<BlockVector3, BaseBlock> = LinkedHashMap()
    var blocks: LinkedHashMap<BlockVector3, BaseBlock> = LinkedHashMap()

    /**
     * Load blocks from schematics
     * Should be called before [Structure.pastStructure]
     * @return Structure to paste
     * @exception StructureNotLoadedException if the schematic could not be found.
     */
    @Throws(StructureNotLoadedException::class)
    fun loadStructure(): Structure {
        val schematicFile = File("${teamStructureYaml.file.parent}${File.separator}schematics${File.separator}" +
                teamStructureYaml.getOrDefault("${building.buildingType.name}.schem", ""))
        villagerRelativeCoordinates = stringToLocation(teamStructureYaml.getOrDefault("${building.buildingType.name}.villager.${if(rightSide) "RIGHT" else "LEFT"}", "0 0 0"))
        xFlipped = teamStructureYaml.getOrSetDefault("xFlipped", false)

        val format: ClipboardFormat = ClipboardFormats.findByFile(schematicFile) ?: throw StructureNotLoadedException("File ${schematicFile.absolutePath} could not be loaded.")
        var clipboard: Clipboard
        try {
            val reader: ClipboardReader = format.getReader(FileInputStream(schematicFile))
            clipboard = reader.read()
            width = clipboard.dimensions.blockX
            height = clipboard.dimensions.blockY
            length = clipboard.dimensions.blockZ
        } catch (e: Exception) {
            throw StructureNotLoadedException("File " + schematicFile.absolutePath + " was not found.")
        }

        if(!rightSide) clipboard = flipStruture(clipboard, xFlipped)

        val allBlocks: LinkedHashMap<BlockVector3, BaseBlock> = LinkedHashMap()
        for (y in clipboard.minimumPoint.subtract(clipboard.origin).y until clipboard.minimumPoint.subtract(clipboard.origin).y + height) {
            for (x in 0 until width) {
                for (z in 0 until length) {
                    val vector = BlockVector3.at(x, y, z).add(clipboard.origin)
                    val block = clipboard.getFullBlock(vector)
                    if(!block.blockType.material.isAir) {
                        allBlocks[BlockVector3.at(x, y, z)] = block
                    }
                }
            }
        }
        undergroundBlocks.putAll(allBlocks.filter { it.key.y < -1})
        blocks.putAll(allBlocks.filter { it.key.y >= -1})

        return this
    }

    /**
     * Past a previously loaded structure on a specified ammount of time
     * @param location where to past
     * @param time in seconds, how much time the build should take (the build might take a bit less time)
     * @throws StructureNotLoadedException if the structure was not loaded before
     * @see .loadStructure
     */
    @Throws(StructureNotLoadedException::class)
    fun pastStructure(location: Location, time: Int) {
        if (width == 0 || height == 0 || length == 0 || blocks.isEmpty()) throw StructureNotLoadedException("Data has not been loaded yet")
        try {
            val editSession = WorldEdit.getInstance().newEditSession(BukkitWorld(getGameManager().world))
            for ((key, value) in undergroundBlocks) {
                editSession.setBlock(key.add(location.blockX, location.blockY, location.blockZ), value)
            }
        } catch (e: MaxChangedBlocksException) {
            e.printStackTrace()
        }

        StructureBuilderRunnable(this, location, time)
            {   //Execute once finish
                building.inConstruction = false
                building.isConstructed = true
                val villagerCoordinate = Location(getGameManager().world, 0.0, 0.0, 0.0, villagerRelativeCoordinates!!.yaw, villagerRelativeCoordinates!!.pitch)
                villagerCoordinate.x = villagerRelativeCoordinates!!.x + location.blockX
                villagerCoordinate.y = villagerRelativeCoordinates!!.y + location.blockY
                villagerCoordinate.z = villagerRelativeCoordinates!!.z + location.blockZ
                building.summonBuildingVillager(villagerCoordinate)
            }.runTaskTimer(plugin, 0, 20)
    }
}