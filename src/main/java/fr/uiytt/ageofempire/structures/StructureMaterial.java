/*
This part was heavily taken from https://gitlab.com/SamB440/Schematics-Extended/-/blob/master/src/main/java/net/islandearth/schematics/extended/Schematic.java
 */

package fr.uiytt.ageofempire.structures;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public enum StructureMaterial {
    SIGN(true),
    CHEST(false),
    TRAPPED_CHEST(false),

    OAK_SIGN(true),
    SPRUCE_SIGN(true),
    BIRCH_SIGN(true),
    JUNGLE_SIGN(true),
    ACACIA_SIGN(true),
    DARK_OAK_SIGN(true),
    CRIMSON_SIGN(true),
    WARPED_SIGN(true),
    OAK_WALL_SIGN(true),
    SPRUCE_WALL_SIGN(true),
    BIRCH_WALL_SIGN(true),
    JUNGLE_WALL_SIGN(true),
    ACACIA_WALL_SIGN(true),
    DARK_OAK_WALL_SIGN(true),
    CRIMSON_WALL_SIGN(true),
    WARPED_WALL_SIGN(true),

    LAVA(true),
    VINE(true),
    WATER(true),
    ARMOR_STAND(true),
    BLACK_BANNER(true),
    BLACK_WALL_BANNER(true),
    BLUE_BANNER(true),
    BLUE_WALL_BANNER(true),
    BROWN_BANNER(true),
    BROWN_WALL_BANNER(true),
    CYAN_BANNER(true),
    CYAN_WALL_BANNER(true),
    GRAY_BANNER(true),
    GRAY_WALL_BANNER(true),
    GREEN_BANNER(true),
    GREEN_WALL_BANNER(true),
    LIGHT_BLUE_BANNER(true),
    LIGHT_BLUE_WALL_BANNER(true),
    LIGHT_GRAY_BANNER(true),
    LIGHT_GRAY_WALL_BANNER(true),
    LIME_BANNER(true),
    LIME_WALL_BANNER(true),
    MAGENTA_BANNER(true),
    MAGENTA_WALL_BANNER(true),
    ORANGE_BANNER(true),
    ORANGE_WALL_BANNER(true),
    PINK_BANNER(true),
    PINK_WALL_BANNER(true),
    PURPLE_BANNER(true),
    PURPLE_WALL_BANNER(true),
    RED_BANNER(true),
    RED_WALL_BANNER(true),
    WHITE_BANNER(true),
    WHITE_WALL_BANNER(true),
    YELLOW_BANNER(true),
    YELLOW_WALL_BANNER(true),

    GRASS(true),
    TALL_GRASS(true),
    SEAGRASS(true),
    TALL_SEAGRASS(true),
    FLOWER_POT(true),
    SUNFLOWER(true),
    CHORUS_FLOWER(true),
    OXEYE_DAISY(true),
    DEAD_BUSH(true),
    FERN(true),
    DANDELION(true),
    POPPY(true),
    BLUE_ORCHID(true),
    ALLIUM(true),
    AZURE_BLUET(true),
    RED_TULIP(true),
    ORANGE_TULIP(true),
    WHITE_TULIP(true),
    PINK_TULIP(true),
    BROWN_MUSHROOM(true),
    RED_MUSHROOM(true),
    END_ROD(true),
    ROSE_BUSH(true),
    PEONY(true),
    LARGE_FERN(true),
    REDSTONE(true),
    REPEATER(true),
    COMPARATOR(true),
    LEVER(true),
    SEA_PICKLE(true),
    SUGAR_CANE(true),
    FIRE(true),
    WHEAT(true),
    WHEAT_SEEDS(true),
    CARROTS(true),
    BEETROOT(true),
    BEETROOT_SEEDS(true),
    MELON(true),
    MELON_STEM(true),
    MELON_SEEDS(true),
    POTATOES(true),
    PUMPKIN(true),
    PUMPKIN_STEM(true),
    PUMPKIN_SEEDS(true),
    TORCH(true),
    RAIL(true),
    ACTIVATOR_RAIL(true),
    DETECTOR_RAIL(true),
    POWERED_RAIL(true),

    ACACIA_FENCE(true),
    ACACIA_FENCE_GATE(true),
    BIRCH_FENCE(true),
    BIRCH_FENCE_GATE(true),
    DARK_OAK_FENCE(true),
    DARK_OAK_FENCE_GATE(true),
    JUNGLE_FENCE(true),
    JUNGLE_FENCE_GATE(true),
    NETHER_BRICK_FENCE(true),
    OAK_FENCE(true),
    OAK_FENCE_GATE(true),
    SPRUCE_FENCE(true),
    SPRUCE_FENCE_GATE(true),

    OAK_DOOR(true),
    ACACIA_DOOR(true),
    BIRCH_DOOR(true),
    DARK_OAK_DOOR(true),
    JUNGLE_DOOR(true),
    SPRUCE_DOOR(true),
    IRON_DOOR(true);

    private final boolean delayed;
    
    StructureMaterial(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isDelayed() {
        return delayed;
    }
    

/*    @Nullable
    public static StructureMaterial fromTag(NBTTagCompound nbtTagCompound) {
        try {
            return StructureMaterial.valueOf(nbtTagCompound.getString("Id").
                    replace("minecraft:", "").
                    toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
*/
    @Nullable
    public static StructureMaterial fromBukkit(Material material) {
        try {
            return StructureMaterial.valueOf(material.toString().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
