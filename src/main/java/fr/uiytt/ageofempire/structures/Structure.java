/*
 I used a bit of : https://gitlab.com/SamB440/Schematics-Extended/-/blob/master/src/main/java/net/islandearth/schematics/extended/Schematic.java
 to create my own system.
 */

package fr.uiytt.ageofempire.structures;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import de.leonhard.storage.Yaml;
import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.ConfigManager;
import fr.uiytt.ageofempire.base.Building;
import fr.uiytt.ageofempire.game.GameManager;
import fr.uiytt.ageofempire.utils.ConfigParser;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;


public class Structure {

    private final AgeOfEmpire plugin;
    private final File structureInfoFile;
    private final Building building;

    private int width = 0;
    private int height = 0;
    private int length = 0;
    private final String side;
    private Location villagerRelativeCoordinates;

    private final LinkedHashMap<BlockVector3, BaseBlock> blocks = new LinkedHashMap<>();

    /**
     * Create a Structure object handling loading and pasting of a schematic
     * At the end, it spawns a villager
     * @param plugin JavaPlugin instance
     * @param structureInfo Yaml File with information such as schematic location, and villager coordinates
     * @param building {@link Building} of a Team, mainly to prevent multiple instancies of this building in the same team.
     * @param side of the plot, either RIGHT or LEFT
     */
    public Structure(AgeOfEmpire plugin, File structureInfo, Building building, String side) {
        this.plugin = plugin;
        this.structureInfoFile = structureInfo;
        this.building = building;
        this.side = side;
    }

    /**
     * Load blocks from schematics
     * Should be called before {@link #pastStructure(Location, int)}
     * @return Structure to paste
     * @exception StructureNotLoadedException if the schematic could not be found.
     */
    public Structure loadStructure() throws StructureNotLoadedException {
        Yaml structureYaml = new Yaml(structureInfoFile);
        File schematicFile = new File(structureInfoFile.getParent() + File.separator + "schematics" + File.separator + structureYaml.get("schem-" + side));
        villagerRelativeCoordinates = ConfigParser.stringToLocation(structureYaml.getOrDefault("villager." + side,"0 0 0"));

        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        Clipboard clipboard;
        try {
            ClipboardReader reader = format.getReader(new FileInputStream(schematicFile));
            clipboard = reader.read();
            width = clipboard.getDimensions().getBlockX();
            height = clipboard.getDimensions().getBlockY();
            length = clipboard.getDimensions().getBlockZ();
        } catch (Exception e) {
            throw new StructureNotLoadedException("File " + schematicFile.getAbsolutePath() + " was not found.");
        }

        for (int y = 0; y < height; y++) { //For y first to have the structure built by layer.
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    BlockVector3 vector = BlockVector3.at(x, y - 1, z).add(clipboard.getOrigin());
                    BaseBlock block = clipboard.getFullBlock(vector);
                    if(block.getBlockType().getMaterial().isAir()) continue;

                    blocks.put(BlockVector3.at(x, y, z), block);
                }
            }
        }
        return this;
    }

    /**
     * Past a previously loaded structure on a specified ammount of time
     * @param location where to past
     * @param time in seconds, how much time the build should take (the build might take a bit less time)
     * @throws StructureNotLoadedException if the structure was not loaded before
     * @see #loadStructure()
     */
    public void pastStructure(Location location, int time) throws StructureNotLoadedException {
        if (width == 0
                || height == 0
                || length == 0
                || blocks.isEmpty()
        ) throw new StructureNotLoadedException("Data has not been loaded yet");

        int blockPerSecond = (int) Math.ceil((double) blocks.size() / time);

        new BukkitRunnable() {

            private final Iterator<BlockVector3> vectors = blocks.keySet().iterator();
            private final Iterator<BaseBlock> baseBlocks = blocks.values().iterator();
            private final World world = ConfigManager.getWorld();
            private int timer = 0;

            /**
             * Paste a number of blocks per second.
             */
            @Override
            public void run() {
                if(!GameManager.getGameInstance().getGameData().isGameRunning()) {
                    this.cancel();
                    return;
                }
                BlockVector3 vector3 = null;
                try(EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(world))) {
                    for(int i=0; i<blockPerSecond; i++) {
                        if(!vectors.hasNext()) break;
                        vector3 = vectors.next().add(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                        editSession.setBlock(vector3, baseBlocks.next());
                    }
                } catch (MaxChangedBlocksException e) {
                    e.printStackTrace();
                }

                if(vector3 != null) {
                    Location blockLocation = new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
                    world.spawnParticle(Particle.CLOUD, blockLocation, 6);
                    world.playEffect(blockLocation, Effect.STEP_SOUND, Material.STONE);
                }


                timer++;

                if(!vectors.hasNext() && timer >= time) {
                    this.cancel();
                    building.setInConstruction(false);
                    building.setConstructed(true);
                    Location villagerCoordinate = new Location(world, 0, 0, 0, villagerRelativeCoordinates.getYaw(), villagerRelativeCoordinates.getPitch());
                    villagerCoordinate.setX(villagerRelativeCoordinates.getX() + location.getBlockX());
                    villagerCoordinate.setY(villagerRelativeCoordinates.getY() + location.getBlockY());
                    villagerCoordinate.setZ(villagerRelativeCoordinates.getZ() + location.getBlockZ());
                    building.summonBuildingVillager(villagerCoordinate);
                }
            }
        }.runTaskTimer(plugin, 0, 20);



    }


}