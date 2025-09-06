package io.qzz._0xflucas.database;

import io.qzz._0xflucas.RewardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {

    private Connection connection;
    private final RewardsPlugin main;
    private final Map<UUID, Map<String, Long>> cooldownCache;
    private final boolean useMySQL;

    private File cooldownFile;
    private FileConfiguration cooldownYML;

    public DatabaseManager(RewardsPlugin main) {
        this.main = main;
        this.cooldownCache = new ConcurrentHashMap<>();
        this.useMySQL = main.getConfig().getBoolean("mysql.use", false);

        if (!useMySQL) {
            initYAML();
        }
    }

    private void initYAML() {
        cooldownFile = new File(main.getDataFolder(), "cooldowns.yml");
        if (!cooldownFile.exists()) {
            try {
                cooldownFile.getParentFile().mkdirs();
                cooldownFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cooldownYML = YamlConfiguration.loadConfiguration(cooldownFile);
        for (String uuidStr : cooldownYML.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            Map<String, Long> playerMap = new ConcurrentHashMap<>();
            for (String reward : cooldownYML.getConfigurationSection(uuidStr).getKeys(false)) {
                playerMap.put(reward, cooldownYML.getLong(uuidStr + "." + reward));
            }
            cooldownCache.put(uuid, playerMap);
        }
        Bukkit.getConsoleSender().sendMessage("§b[0xfRewards] YAML cooldowns.yml loaded!");
    }

    public void connect() throws SQLException {
        if (!useMySQL) return;

        String host = main.getConfig().getString("mysql.host");
        int port = main.getConfig().getInt("mysql.port");
        String database = main.getConfig().getString("mysql.database");
        String username = main.getConfig().getString("mysql.username");
        String password = main.getConfig().getString("mysql.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useSSL=true&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo";
        connection = DriverManager.getConnection(url, username, password);

        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS cooldowns (" +
                        "uuid VARCHAR(36), reward VARCHAR(50), cooldown BIGINT, PRIMARY KEY(uuid, reward))")) {
            stmt.execute();
        }

        Bukkit.getConsoleSender().sendMessage("§b=> 0xfRewards 2.0 - database '" + database + "' connected successfully");
    }

    public void disconnect() {
        if (useMySQL && connection != null) {
            try {
                connection.close();
                Bukkit.getConsoleSender().sendMessage("=> 0xfRewards 2.0 - database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (!useMySQL) {
            saveYAML();
        }
    }

    public void loadPlayerCooldowns(UUID uuid) {
        if (useMySQL) {
            Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                try {
                    if (isConnected()) connect();
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
        } else {
            Map<String, Long> playerCooldowns = new ConcurrentHashMap<>();
            if (cooldownYML.contains(uuid.toString())) {
                ConfigurationSection section = cooldownYML.getConfigurationSection(uuid.toString());
                for (String reward : section.getKeys(false)) {
                    playerCooldowns.put(reward, cooldownYML.getLong(uuid.toString() + "." + reward));
                }
            }
            cooldownCache.put(uuid, playerCooldowns);
        }
    }

    public long getCooldown(UUID uuid, String reward) {
        Map<String, Long> playerMap = cooldownCache.get(uuid);
        if (playerMap == null) return 0L;
        return playerMap.getOrDefault(reward, 0L);
    }

    public void setCooldown(UUID uuid, String reward, long cooldown) {
        cooldownCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(reward, cooldown);

        if (useMySQL) {
            Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                try {
                    if (isConnected()) connect();
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
        } else {
            cooldownYML.set(uuid.toString() + "." + reward, cooldown);
            saveYAML();
        }
    }

    public void unloadPlayer(UUID uuid) {
        cooldownCache.remove(uuid);
    }

    private boolean isConnected() {
        if (!useMySQL) return false;
        try {
            return connection == null || connection.isClosed() || !connection.isValid(5);
        } catch (SQLException e) {
            return true;
        }
    }

    private void saveYAML() {
        try {
            cooldownYML.save(cooldownFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
