package tree.gateways.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Gateway(
    val name: String,
    val world: String,
    val pos1: Location,
    val pos2: Location
) {
    fun getRandomLocationInside(): Location {
        val minX = pos1.blockX.coerceAtMost(pos2.blockX)
        val minY = pos1.blockY.coerceAtMost(pos2.blockY)
        val minZ = pos1.blockZ.coerceAtMost(pos2.blockZ)
        val maxX = pos1.blockX.coerceAtLeast(pos2.blockX)
        val maxY = pos1.blockY.coerceAtLeast(pos2.blockY)
        val maxZ = pos1.blockZ.coerceAtLeast(pos2.blockZ)

        // Generate random coordinates within the region
        val randomX = (minX..maxX).random().toDouble()
        val randomY = (minY..maxY).random().toDouble()
        val randomZ = (minZ..maxZ).random().toDouble()

        return Location(pos1.world, randomX, randomY, randomZ)
    }
    fun createIcon(): ItemStack {
        val icon = ItemStack(Material.ENDER_PEARL)
        val meta = icon.itemMeta
        meta.setDisplayName(name)
        meta.lore = listOf("Click to teleport to ${name}")
        icon.itemMeta = meta
        return icon
    }
    fun isLocationInside(location: Location): Boolean {
        val minX = pos1.blockX.coerceAtMost(pos2.blockX)
        val minY = pos1.blockY.coerceAtMost(pos2.blockY)
        val minZ = pos1.blockZ.coerceAtMost(pos2.blockZ)
        val maxX = pos1.blockX.coerceAtLeast(pos2.blockX)
        val maxY = pos1.blockY.coerceAtLeast(pos2.blockY)
        val maxZ = pos1.blockZ.coerceAtLeast(pos2.blockZ)

        return (location.blockX in minX..maxX) &&
                (location.blockY in minY..maxY) &&
                (location.blockZ in minZ..maxZ)
    }
}
