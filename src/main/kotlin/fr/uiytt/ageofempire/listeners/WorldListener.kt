package fr.uiytt.ageofempire.listeners

import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.isRunning
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.entity.EntityExplodeEvent

class WorldListener: Listener {

    @EventHandler
    fun onObsidianForming(event: BlockFormEvent) {
        if (!getGameManager().isRunning() || event.newState.block.type != Material.OBSIDIAN) return
        event.newState.type = Material.AIR
    }

    @EventHandler
    fun onExplosion(event: EntityExplodeEvent) {
        if (!getGameManager().isRunning()) return
        event.yield = 0f
        event.blockList().removeIf { block: Block -> Tag.WOOL.isTagged(block.type) }
    }
}