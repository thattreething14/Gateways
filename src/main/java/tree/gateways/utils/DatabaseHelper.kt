package tree.gateways.utils

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.bukkit.Bukkit
import org.bukkit.Location
import tree.gateways.GatewaysPlugin

object DatabaseHelper {
    private val dbFile = GatewaysPlugin.instance.dataFolder.resolve("gateways.db")
    private val url = "jdbc:sqlite:${dbFile.absolutePath}"

    init {
        if (!GatewaysPlugin.instance.dataFolder.exists()) {
            GatewaysPlugin.instance.dataFolder.mkdirs()
        }
        connect().use { connection ->
            connection.createStatement().executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS gateways (
                    name TEXT PRIMARY KEY,
                    world TEXT,
                    pos1_x REAL,
                    pos1_y REAL,
                    pos1_z REAL,
                    pos2_x REAL,
                    pos2_y REAL,
                    pos2_z REAL
                )
                """.trimIndent()
            )
        }
    }

    private fun connect(): Connection {
        return DriverManager.getConnection(url)
    }

    fun saveGateway(gateway: Gateway) {
        val sql = """
            INSERT OR REPLACE INTO gateways (name, world, pos1_x, pos1_y, pos1_z, pos2_x, pos2_y, pos2_z) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()

        connect().use { connection ->
            val statement: PreparedStatement = connection.prepareStatement(sql)
            statement.setString(1, gateway.name)
            statement.setString(2, gateway.world)
            statement.setDouble(3, gateway.pos1.x)
            statement.setDouble(4, gateway.pos1.y)
            statement.setDouble(5, gateway.pos1.z)
            statement.setDouble(6, gateway.pos2.x)
            statement.setDouble(7, gateway.pos2.y)
            statement.setDouble(8, gateway.pos2.z)
            statement.executeUpdate()
        }
    }

    fun loadGateways(): List<Gateway> {
        val gateways = mutableListOf<Gateway>()
        val sql = "SELECT * FROM gateways"

        connect().use { connection ->
            val statement: PreparedStatement = connection.prepareStatement(sql)
            val resultSet: ResultSet = statement.executeQuery()

            while (resultSet.next()) {
                val name = resultSet.getString("name")
                val world = resultSet.getString("world")
                val pos1 = Location(
                    Bukkit.getWorld(world),
                    resultSet.getDouble("pos1_x"),
                    resultSet.getDouble("pos1_y"),
                    resultSet.getDouble("pos1_z")
                )
                val pos2 = Location(
                    Bukkit.getWorld(world),
                    resultSet.getDouble("pos2_x"),
                    resultSet.getDouble("pos2_y"),
                    resultSet.getDouble("pos2_z")
                )
                gateways.add(Gateway(name, world, pos1, pos2))
            }
        }
        return gateways
    }
}
