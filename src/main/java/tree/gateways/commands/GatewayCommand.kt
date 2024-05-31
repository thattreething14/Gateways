package tree.gateways.commands

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import tree.gateways.GatewaysPlugin
import tree.gateways.utils.DatabaseHelper
import tree.gateways.utils.Gateway
import tree.gateways.utils.Wand
import tree.modelib.model.type.StaticEntity

object GatewayCommand : CommandExecutor {
    private var pos1: Location? = null
    private var pos2: Location? = null
    fun openGatewayGUI(player: Player) {
        val inventory = player.server.createInventory(null, 9, "Gateways")
        val gatewaySlots = HashMap<Int, Gateway>() // Map slot index to Gateway

        for ((index, gateway) in GatewaysPlugin.instance.gateways.values.withIndex()) {
            val icon = gateway.createIcon()
            inventory.setItem(index, icon)
            gatewaySlots[index] = gateway // Store gateway information with slot index
        }
        player.openInventory(inventory)

        // Register a Bukkit event listener to handle inventory click events
        // I could put it in the listener class but im lazy
        val listener = object : Listener {
            @EventHandler
            fun onInventoryClick(event: InventoryClickEvent) {
                if (event.clickedInventory == inventory && event.currentItem != null) {
                    val clickedSlot = event.slot
                    val clickedGateway = gatewaySlots[clickedSlot]
                    if (clickedGateway != null) {
                        // Teleport the player to the clicked gateway location
                        player.teleport(clickedGateway.getRandomLocationInside())
                    }
                    event.isCancelled = true
                }
            }
        }
        Bukkit.getPluginManager().registerEvents(listener, GatewaysPlugin.instance)
    }
    fun setPos1(location: Location) {
        pos1 = location
    }

    fun setPos2(location: Location) {
        pos2 = location
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.isEmpty()) {
            sender.sendMessage("Usage: /$label <set|list>")
            return true
        }

        when (args[0]) {
            "set" -> {
                if (args.size < 2) {
                    sender.sendMessage("Usage: /$label set <pos1|pos2|create>")
                    return true
                }
                when (args[1]) {
                    "pos1" -> {
                        Wand.giveTo(sender)
                        sender.sendMessage("Use the wand to set position 1.")
                    }
                    "pos2" -> {
                        Wand.giveTo(sender)
                        sender.sendMessage("Use the wand to set position 2.")
                    }
                    "create" -> {
                        if (pos1 != null && pos2 != null) {
                            val name = args.getOrNull(2) ?: run {
                                sender.sendMessage("Usage: /$label set create <name>")
                                return true
                            }

                            // Ensure pos1 is the minimum corner and pos2 is the maximum corner
                            val minX = minOf(pos1!!.x, pos2!!.x).toInt()
                            val maxX = maxOf(pos1!!.x, pos2!!.x).toInt()
                            val minY = minOf(pos1!!.y, pos2!!.y).toInt()
                            val maxY = maxOf(pos1!!.y, pos2!!.y).toInt()
                            val minZ = minOf(pos1!!.z, pos2!!.z).toInt()
                            val maxZ = maxOf(pos1!!.z, pos2!!.z).toInt()

                            // Set all blocks within the region to air
                            val world = sender.world
                            for (x in minX..maxX) {
                                for (y in minY..maxY) {
                                    for (z in minZ..maxZ) {
                                        val block = world.getBlockAt(x, y, z)
                                        block.type = Material.AIR
                                        StaticEntity.create("mist_block", Location(world, x.toDouble(), y.toDouble(), z.toDouble()))
                                    }
                                }
                            }

                            // Create and save the gateway
                            val gateway = Gateway(name, sender.world.name, pos1!!, pos2!!)
                            GatewaysPlugin.instance.gateways[name] = gateway
                            DatabaseHelper.saveGateway(gateway) // Save gateway to database
                            sender.sendMessage("Gateway $name created and saved")
                        } else {
                            sender.sendMessage("You need to set both positions first")
                        }
                    }

                    else -> {
                        sender.sendMessage("Usage: /$label set <pos1|pos2|create>")
                    }
                }
            }
            "list" -> {
                sender.sendMessage("Gateways: ${GatewaysPlugin.instance.gateways.keys.joinToString(", ")}")
            }
            else -> {
                sender.sendMessage("Usage: /$label <set|list>")
            }
        }
        return true
    }
}
