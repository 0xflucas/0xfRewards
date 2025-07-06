package br.com.lucas0001007.listeners;

import br.com.lucas0001007.RewardsPlugin;
import br.com.lucas0001007.utils.FormatColor;
import br.com.lucas0001007.utils.TimeFormat;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuClickListener implements Listener {

    RewardsPlugin plugin = RewardsPlugin.getInstance();

    @EventHandler
    public void onClickInMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (!e.getView().getTitle().equalsIgnoreCase(plugin.getConfig().getString("menu.title"))) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection rewards = config.getConfigurationSection(
            "rewards"
        );

        for (String key : rewards.getKeys(false)) {
            ConfigurationSection reward = rewards.getConfigurationSection(key);
            if (reward.getInt("slot") != slot) continue;

            String perm = reward.getString("permission");
            if (perm != null && !p.hasPermission(perm)) {
                p.sendMessage(
                    FormatColor.run(config.getString("messages.no_perm"))
                );
                // p.sendMessage("§cVocê não tem permissão.");
                p.closeInventory();
                return;
            }

            int cooldownSeconds = reward.getInt("cooldown", 0);
            long now = System.currentTimeMillis();
            long lastUsed = plugin
                .getDatabaseManager()
                .getCooldown(p.getUniqueId(), key);

            if (cooldownSeconds > 0 && lastUsed > 0) {
                long elapsed = (now - lastUsed) / 1000;
                if (elapsed < cooldownSeconds) {
                    long remainingTime = cooldownSeconds - elapsed;
                    
                    String formattedTime = TimeFormat.formatTimeRemaining(remainingTime);
                    p.sendMessage(
                        FormatColor.run(config.getString("messages.in_cooldown").replace("{cooldown}", formattedTime))
                    );
                    
                    p.closeInventory();
                    return;
                }
            }

            List<String> commands = reward.getStringList("commands");
            for (String command : commands) {
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command
                        .replace("{player}", p.getName())
                        .replace("{reward}", key)
                );
            }

            String msg = FormatColor.run(reward.getString("message"));
            if (msg != null) {
                p.sendMessage(msg);
            }

            if (cooldownSeconds > 0) {
                plugin
                    .getDatabaseManager()
                    .setCooldown(p.getUniqueId(), key, now);
            }

            p.closeInventory();
            return;
        }
    }
}
