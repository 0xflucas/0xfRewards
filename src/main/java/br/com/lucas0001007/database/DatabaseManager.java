    package br.com.lucas0001007.database;
    
    import br.com.lucas0001007.RewardsPlugin;
    import org.bukkit.Bukkit;
    
    import java.sql.*;
    import java.util.Map;
    import java.util.UUID;
    import java.util.concurrent.ConcurrentHashMap;
    
    public class DatabaseManager {
    
        private Connection connection;
        private RewardsPlugin plugin = RewardsPlugin.getInstance();
    
        private final Map<UUID, Map<String, Long>> cooldownCache = new ConcurrentHashMap<>();
    
        public void connect() throws SQLException {
            String host = plugin.getConfig().getString("mysql.host");
            int port = plugin.getConfig().getInt("mysql.port");
            String database = plugin.getConfig().getString("mysql.database");
            String username = plugin.getConfig().getString("mysql.username");
            String password = plugin.getConfig().getString("mysql.password");
    
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=true&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo";
            connection = DriverManager.getConnection(url, username, password);
    
            try (PreparedStatement stmt = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS cooldowns (uuid VARCHAR(36), reward VARCHAR(50), cooldown BIGINT, PRIMARY KEY(uuid, reward))")) {
                stmt.execute();
            }
    
            Bukkit.getConsoleSender().sendMessage("§b=> cRewards 2.0 - database '" + database + "' connected successfully");
        }
    
        public void disconnect() {
            if (connection != null) {
                try {
                    connection.close();
                    Bukkit.getConsoleSender().sendMessage("=> cRewards 2.0 - database connection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    
        public void loadPlayerCooldowns(UUID uuid) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    if (!isConnected()) {
                        connect();
                    }
                    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM cooldowns WHERE uuid = ?")) {
                        ps.setString(1, uuid.toString());
                        try (ResultSet rs = ps.executeQuery()) {
                            Map<String, Long> playerCooldowns = new ConcurrentHashMap<>();
                            while (rs.next()) {
                                playerCooldowns.put(rs.getString("reward"), rs.getLong("cooldown"));
                            }
    
                            cooldownCache.put(uuid, playerCooldowns);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    
        public long getCooldown(UUID uuid, String reward) {
            Map<String, Long> playerMap = cooldownCache.get(uuid);
            if (playerMap == null)
                return 0;
            return playerMap.getOrDefault(reward, 0L);
        }
    
        public void setCooldown(UUID uuid, String reward, long cooldown) {
            cooldownCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(reward, cooldown);
    
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    if (!isConnected()) {
                        connect();
                    }
                    try (PreparedStatement ps = connection.prepareStatement(
                            "REPLACE INTO cooldowns (uuid, reward, cooldown) VALUES (?, ?, ?)")) {
                        ps.setString(1, uuid.toString());
                        ps.setString(2, reward);
                        ps.setLong(3, cooldown);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    
        public void unloadPlayer(UUID uuid) {
            cooldownCache.remove(uuid);
        }
    
        private boolean isConnected() {
            try {
                return connection != null && !connection.isClosed() && connection.isValid(5);
            } catch (SQLException e) {
                return false;
            }
        }
    }