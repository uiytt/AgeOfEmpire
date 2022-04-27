package fr.uiytt.ageofempire.structures

import com.sk89q.worldedit.MaxChangedBlocksException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.math.BlockVector3
import fr.uiytt.ageofempire.game.getGameManager
import fr.uiytt.ageofempire.game.isRunning
import org.apache.commons.lang.ObjectUtils.Null
import org.bukkit.*
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Consumer
import kotlin.math.ceil


class StructureBuilderRunnable(
    structure: Structure,
    private val pasteLocation: Location,
    private val timeToFinish: Int,
    private val consumerFinish: Consumer<Null>?): BukkitRunnable() {

    private val world: World = getGameManager().world
    private val blocksPerSecond =  ceil(structure.blocks.size.toDouble() / timeToFinish).toInt()
    private val blocks = structure.blocks.iterator()
    private var timer = 0



    override fun run() {
        if (!getGameManager().isRunning()) {
            cancel()
            return
        }
        var vector3: BlockVector3? = null
        try {
            WorldEdit.getInstance().newEditSession(BukkitWorld(world)).run {
                for (i in 0 until blocksPerSecond) {
                    if (!blocks.hasNext()) break
                    val block = blocks.next()
                    vector3 = block.key.add(pasteLocation.blockX, pasteLocation.blockY, pasteLocation.blockZ)
                    this.setBlock(vector3, block.value)
                }
            }
        } catch (e: MaxChangedBlocksException) {
            e.printStackTrace()
        }
        if (vector3 != null) {
            val blockLocation = Location(world, vector3!!.blockX.toDouble(), vector3!!.blockY.toDouble(), vector3!!.blockZ.toDouble())
            world.spawnParticle(Particle.CLOUD, blockLocation, 6)
            world.playEffect(blockLocation, Effect.STEP_SOUND, Material.STONE)
        }
        timer++
        if (!blocks.hasNext() && timer >= timeToFinish) {
            cancel()
            consumerFinish?.accept(null)
        }
    }
}
