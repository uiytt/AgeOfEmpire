package fr.uiytt.ageofempire.structures

import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.math.Vector3
import com.sk89q.worldedit.math.transform.AffineTransform
import com.sk89q.worldedit.session.ClipboardHolder

fun flipStruture(clipboard: Clipboard, xFlipped: Boolean): Clipboard {
    val resultClipboard = BlockArrayClipboard(clipboard.region)
    resultClipboard.origin = clipboard.origin

    val clipboardHolder = ClipboardHolder(clipboard)
    val affineTransform = AffineTransform()

    val vectorTransform = if(xFlipped)  Vector3.at(-1.0, 1.0, 1.0) else Vector3.at(1.0, 1.0, -1.0)
    clipboardHolder.transform = affineTransform.scale(vectorTransform)

    try {
        val pastingLocation = if(xFlipped) BlockVector3.at(clipboard.dimensions.blockX - 1, 0, 0) else BlockVector3.at(0, 0, clipboard.dimensions.blockZ - 1)
        val operation = clipboardHolder.createPaste(resultClipboard)
            .to(clipboard.origin.add(pastingLocation))
            .build()
        Operations.complete(operation)
    } catch (e: WorldEditException) {
        throw RuntimeException(e)
    }

    return resultClipboard
}