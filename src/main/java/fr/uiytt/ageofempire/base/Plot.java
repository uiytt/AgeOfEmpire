package fr.uiytt.ageofempire.base;

import fr.uiytt.ageofempire.AgeOfEmpire;
import fr.uiytt.ageofempire.game.GameTeam;
import fr.uiytt.ageofempire.structures.Structure;
import fr.uiytt.ageofempire.structures.StructureNotLoadedException;
import fr.uiytt.ageofempire.utils.ConfigParser;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashMap;

public class Plot {

    private static final int MAX_PLOT_SIZE = 30;

    private int size;
    private Location location;
    private boolean constructed = false;
    private boolean destroyed = false;
    private String side;

    /**
     * Create a new usable plot for building from yaml values and automatically register it
     * @param teamBase the TeamBase instance of the team
     * @param values A LinkedHashMap<String,String> , should contain a "size" and a "location"
     * Size should be a string of an int between 1 and 3
     * Location should be a string with the 3 lowest coordinates of the plot ex : "12 69 -15"
     */
    @SuppressWarnings("unchecked")
    public Plot(TeamBase teamBase, Object values) {
        try {
            LinkedHashMap<String,String> data = (LinkedHashMap<String, String>) values;
            this.size = Integer.parseInt(data.get("size"));
            location = ConfigParser.stringToLocation(data.get("location"));
            side = data.get("side");
            if(size > 3 || size < 1 || (location.getX() == 0 && location.getY() == 80 && location.getZ() == 0)) throw new Exception("Size or location load error in plot");
            teamBase.getPlots().put(location,this);
        } catch (Exception exception) {
            AgeOfEmpire.getLog().warning("Wrong value in plots of " + teamBase.getYamlBase().getFilePath());
            exception.printStackTrace();
        }
    }


    public void build(GameTeam playerTeam, BuildingType buildingType,Building building) {
        building.setInConstruction(true);
        building.setPlotLocation(location);
        constructed = true;
        File structureFile = new File("plugins" + File.separator + "AgeOfEmpire" + File.separator + playerTeam.getColor().name() + File.separator +  buildingType.name() + "-" + side + ".yml");
        Structure structure = new Structure(AgeOfEmpire.getInstance(), structureFile, building);
        try {
            long time = System.currentTimeMillis();
            structure.loadStructure();
            System.out.println("Loading structure took " + (System.currentTimeMillis() - time));
            structure.pastStructure(location, buildingType.getTime());
        } catch (StructureNotLoadedException e) {
            e.printStackTrace();
        }
    }


    /**
     * From a location, search around to locate if there is a plot of this team existing
     * @param teamBase the team of the plot it's looking for
     * @param location the location where it must look around
     * @return A plot instance if a plot is found, or null if no correct plot is found
     */
    @Nullable
    public static Plot checkForPlot(TeamBase teamBase, Location location) {
        //Substract 1 from x until a border is met or return null
        for(int x = 0; x<=MAX_PLOT_SIZE; x++) {
            if(Tag.WOOL.isTagged(location.subtract(1,0,0).getBlock().getType())) {
                location.add(1,0,0); //Add 1 because the registered location for the plot is not on the border
                break;
            }
            if(x == MAX_PLOT_SIZE) return null;
        }
        //Substract 1 from y until a border is met or return null
        for(int z = 0; z<=MAX_PLOT_SIZE; z++) {
            if(Tag.WOOL.isTagged(location.subtract(0,0,1).getBlock().getType())) {
                location.add(0,0,1); //Add 1 because the registered location for the plot is not on the border
                break;
            }
            if(z == MAX_PLOT_SIZE) return null;
        }
        return teamBase.getPlots().get(location);
    }

    public int getSize() {return size;}
    public boolean isPlotAvailable() {return !constructed && !destroyed;}


    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public void setConstructed(boolean constructed) {
        this.constructed = constructed;
    }
}
