package tree.gateways.listeners

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import tree.gateways.GatewaysPlugin
import tree.gateways.commands.GatewayCommand

class GatewayGUI: Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val to = event.to
        val from = event.from
        if (from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ) {
            val gatewayCommand = GatewaysPlugin.instance.getCommand("gateway")?.executor as? GatewayCommand ?: return
            val gateways = GatewaysPlugin.instance.gateways.values
            for (gateway in gateways) {
                if (gateway.isLocationInside(to)) {
                    gatewayCommand.openGatewayGUI(player)
                    break
                }
            }
        }
    }
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR) {
            val player = event.player
            val item = event.item ?: return
            if (item.type == Material.COMPASS && item.hasItemMeta()) {
                event.isCancelled = true
                // Handle the compass click event
                val gatewayCommand = GatewaysPlugin.instance.getCommand("gateway")?.executor as? GatewayCommand ?: return
                gatewayCommand.openGatewayGUI(player)
            }
        }
    }
}
