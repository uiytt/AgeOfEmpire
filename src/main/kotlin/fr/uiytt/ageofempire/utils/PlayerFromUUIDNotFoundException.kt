package fr.uiytt.ageofempire.utils

import java.util.*

class PlayerFromUUIDNotFoundException(uuid: UUID) : Exception("The player with the UUID $uuid was not found by bukkit.")