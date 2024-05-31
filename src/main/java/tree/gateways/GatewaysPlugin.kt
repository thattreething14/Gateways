package tree.gateways

import org.bukkit.plugin.java.JavaPlugin
import tree.gateways.commands.GatewayCommand
import tree.gateways.listeners.GatewayGUI
import tree.gateways.utils.DatabaseHelper
import tree.gateways.utils.Gateway
import tree.gateways.utils.Wand

class GatewaysPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: GatewaysPlugin
            private set
    }

    val gateways = mutableMapOf<String, Gateway>()
    override fun onEnable() {
        instance = this
        getCommand("gateway")?.setExecutor(GatewayCommand)
        server.pluginManager.registerEvents(GatewayGUI(), this)
        server.pluginManager.registerEvents(Wand, this)
        val loadedGateways = DatabaseHelper.loadGateways()
        for (gateway in loadedGateways) {
            gateways[gateway.name] = gateway
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
