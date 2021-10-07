package fr.uiytt.ageofempire.utils;

import java.util.UUID;

public class PlayerFromUUIDNotFoundException extends Exception{

    public PlayerFromUUIDNotFoundException(UUID uuid) {
        super("The player with the UUID " + uuid.toString() + " was not found by bukkit.");
    }

}
