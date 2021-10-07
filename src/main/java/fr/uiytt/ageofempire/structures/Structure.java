/*
 I used a bit of : https://gitlab.com/SamB440/Schematics-Extended/-/blob/master/src/main/java/net/islandearth/schematics/extended/Schematic.java
 to create my own system.
 */

package fr.uiytt.ageofempire.structures;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Allow to copy & past structure using information from files
 *
 */
public class Structure {

    private final AgeOfEmpire plugin;
    private final File structureInfoFile;
    private final Building building;

    private int width = 0;
    private int height = 0;
    private int length = 0;
    private Location structureCoordinates;
    private File schematicFile;
    private Location villagerRelativeCoordinates;

    private final LinkedHashMap<BlockVector3, BaseBlock> blocks = new LinkedHashMap<>();

    /**
     * @param plugin your plugin instance
     * @param structureInfo file to the info of the structure
     */
    public Structure(AgeOfEmpire plugin, File structureInfo, Building building) {
        this.plugin = plugin;
        this.structureInfoFile = structureInfo;
        this.building = building;
    }

    /**
     * Load the structure & villager information
     */
    @Nullable
    public Structure loadStructure() {
        Yaml structureYaml = new Yaml(structureInfoFile);

        structureCoordinates = ConfigParser.stringToLocation(structureYaml.getOrDefault("structure.coordinates","0 0 0"));
        schematicFile = new File(structureInfoFile.getParent() + File.separator + "schematics" + File.separator + structureYaml.get("schem-path"));

        villagerRelativeCoordinates = ConfigParser.stringToLocation(structureYaml.getOrDefault("villager.coordinates","0 0 0"));
        HashMap<BlockVector3, BaseBlock> delayedBlocks = new HashMap<>();

        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        Clipboard clipboard;
        try {
            ClipboardReader reader = format.getReader(new FileInputStream(schematicFile));
            clipboard = reader.read();
            width = clipboard.getDimensions().getBlockX();
            height = clipboard.getDimensions().getBlockY();
            length = clipboard.getDimensions().getBlockZ();
        } catch (Exception e) {
            AgeOfEmpire.getLog().severe("File " + schematicFile.getAbsolutePath() + " was not found.");
            return null;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    BlockVector3 vector = BlockVector3.at(x, y - 1, z).add(clipboard.getOrigin());
                    BaseBlock block = clipboard.getFullBlock(vector);
                    if(block.getBlockType().getMaterial().isAir()) continue;
                    StructureMaterial structureMaterial;
                    try {
                        structureMaterial = StructureMaterial.valueOf(block.getBlockType().getMaterial().toString());
                    }  catch (IllegalArgumentException illegalArgumentException) {
                        structureMaterial = null;
                    }
                    if (structureMaterial == null || !structureMaterial.isDelayed()) {
                        blocks.put(BlockVector3.at(x, y, z), block);
                    } else {
                        delayedBlocks.put(BlockVector3.at(x, y, z), block);
                    }
                }
            }
        }
        blocks.putAll(delayedBlocks);
        /*
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    Vector vector = new Vector(x, y, z);
                    Block block = ConfigManager.getWorld().getBlockAt(structureCoordinates.getBlockX() + x, structureCoordinates.getBlockY() + y, structureCoordinates.getBlockZ() + z);

                    if(block.getType() == Material.AIR) continue;
                    StructureMaterial structureMaterial = StructureMaterial.fromBukkit(block.getType());
                    if (StructureMaterial.fromBukkit(block.getType()) == null || structureMaterial == null || !structureMaterial.isDelayed()) {
                        blocks.put(vector, block.getBlockData());
                    } else {
                        delayedBlocks.put(vector, block.getBlockData());
                    }

                }
            }
        }
        //Add delayed blocks at the end of the LinkedHashMap
        delayedBlocks.forEach(blocks::put);
        */
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