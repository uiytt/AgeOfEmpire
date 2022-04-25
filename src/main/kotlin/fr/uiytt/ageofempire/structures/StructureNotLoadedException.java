package fr.uiytt.ageofempire.structures;

/**
 * Called when a schematic's data has not been loaded.
 * @author SamB440
 */
public class StructureNotLoadedException extends Exception {

    public StructureNotLoadedException(String message) {
        super(message);
    }
}
