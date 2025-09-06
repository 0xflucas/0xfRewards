package io.qzz._0xflucas.listeners;

import io.qzz._0xflucas.RewardsPlugin;
import io.qzz._0xflucas.utils.TimeFormat;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuClickListener implements Listener {

    private final RewardsPlugin main;
    private final FileConfiguration config;
    private final String menuTitle;

    public MenuClickListener(RewardsPlugin main) {
        this.main = main;
        this.config = main.getConfig();
        this.menuTitle = config.getString("menu.title");
        // main.getLogger().info("MenuClickListener registrado");
    }

    @EventHandler
    public void onClickInMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (!e.getView().getTitle().equalsIgnoreCase(menuTitle)) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        ConfigurationSection rewards = config.getConfigurationSection("rewards");
        if (rewards == null) return;

        for (String key : rewards.getKeys(false)) {
            ConfigurationSection reward = rewards.getConfigurationSection(key);
            if (reward == null) continue;
            if (reward.getInt("slot") != slot) continue;

            String perm = reward.getString("permission");
            if (perm != null && !p.hasPermission(perm)) {
                p.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', (config.getString("messages.no_perm", "§cVocê não tem permissão para esta recompensa."))));
                p.closeInventory();
                return;
            }

            int cooldownSeconds = reward.getInt("cooldown", 0);
            if (isInCooldown(p, key, reward, true)) return;

            List<String> commands = reward.getStringList("commands");
            for (String command : commands) {
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command.replace("{player}", p.getName()).replace("{reward}", key)
                );
            }

            String msg = ChatColor.translateAlternateColorCodes('&', reward.getString("message", "§aVocê recebeu a recompensa!"));
            p.sendMessage(msg);

            if (cooldownSeconds > 0) {
                long now = System.currentTimeMillis();
                main.getDatabaseManager().setCooldown(p.getUniqueId(), key, now);
            }

            p.closeInventory();
            return;
        }
    }

    public boolean isInCooldown(Player p, String key, ConfigurationSection reward, boolean sendMessage) {
        int cooldownSeconds = reward.getInt("cooldown", 0);
        long now = System.currentTimeMillis();
        long lastUsed = main.getDatabaseManager().getCooldown(p.getUniqueId(), key);

        if (cooldownSeconds > 0 && lastUsed > 0) {
            long elapsed = (now - lastUsed) / 1000;
            if (elapsed < cooldownSeconds) {
                if (sendMessage) {
                    long remainingTime = cooldownSeconds - elapsed;
                    String formattedTime = TimeFormat.formatTimeRemaining(remainingTime);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            config.getString("messages.in_cooldown", "§cVocê precisa esperar {cooldown} para usar esta recompensa novamente.")
                                    .replace("{cooldown}", formattedTime)
                    ));
                    p.closeInventory();
                }
                return true;
            }
        }
        return false;
    }

}
