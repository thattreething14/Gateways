package tree.gateways.utils

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import tree.gateways.commands.GatewayCommand

object Wand : Listener {

    fun giveTo(player: Player) {
        val wand = createWand()
        player.inventory.addItem(wand)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action
        val item = event.item ?: return
        if (!isWand(item)) return

        if (action == Action.LEFT_CLICK_BLOCK) {
            val clickedBlock: Block = event.clickedBlock ?: return
            GatewayCommand.setPos1(clickedBlock.location)
            player.sendMessage("Position 1 set at ${clickedBlock.location.blockX}, ${clickedBlock.location.blockY}, ${clickedBlock.location.blockZ}")
            event.isCancelled = true
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            val clickedBlock: Block = event.clickedBlock ?: return
            GatewayCommand.setPos2(clickedBlock.location)
            player.sendMessage("Position 2 set at ${clickedBlock.location.blockX}, ${clickedBlock.location.blockY}, ${clickedBlock.location.blockZ}")
            event.isCancelled = true
        }
    }

    private fun createWand(): ItemStack {
        val wand = ItemStack(Material.BLAZE_ROD)
        val meta = wand.itemMeta
        meta?.setDisplayName("${ChatColor.GREEN}${ChatColor.BOLD}Gateway Wand")
        meta?.lore = listOf("${ChatColor.GRAY}A magical wand for setting gateways.")
        meta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        wand.itemMeta = meta
        return wand
    }


    private fun isWand(item: ItemStack): Boolean {
        val meta = item.itemMeta
        return meta != null && meta.displayName == "${ChatColor.GREEN}${ChatColor.BOLD}Gateway Wand"
    }
}
