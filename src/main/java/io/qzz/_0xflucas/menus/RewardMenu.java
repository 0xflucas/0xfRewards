package io.qzz._0xflucas.menus;

import java.util.ArrayList;
import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.qzz._0xflucas.RewardsPlugin;
import io.qzz._0xflucas.listeners.MenuClickListener;
import io.qzz._0xflucas.utils.TimeFormat;

public class RewardMenu {

    private final RewardsPlugin main;
    private final MenuClickListener menuClickListener;
    public RewardMenu(RewardsPlugin main) {
        this.main = main;
        this.menuClickListener = new MenuClickListener(main);
    }

    public Inventory menu(Player p) {
        FileConfiguration config = main.getConfig();
        ConfigurationSection rewards = config.getConfigurationSection("rewards");

        int chestSize = (9 * config.getInt("menu.rows", 5));

//        p.sendMessage("" + chestSize);
        Inventory inv = Bukkit.createInventory(null, chestSize, config.getString("menu.title"));
        if (rewards == null)
            return null;

        for (String reward : rewards.getKeys(false)) {
            ConfigurationSection section = rewards.getConfigurationSection(reward);

            if (section == null) continue;

            int slot = section.getInt("slot");
            String name = ChatColor.translateAlternateColorCodes('&', (section.getString("name")));
            ItemStack item = XMaterial.matchXMaterial(section.getString("item"))
                    .orElse(XMaterial.STONE)
                    .parseItem();

            assert item != null;
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            
            List<String> initialLore = section.getStringList("lore");
            List<String> finalLore   = new ArrayList<>();
            
            for (String il : initialLore) {
                finalLore.add(ChatColor.translateAlternateColorCodes('&', (il)));
            }
            
            String perm = section.getString("permission");
            if (!p.hasPermission(perm)) {
                finalLore.add(ChatColor.translateAlternateColorCodes('&', (section.getString("lore_no_permission"))));
            } else if(menuClickListener.isInCooldown(p, reward, section, false)){
                int cooldownSeconds = section.getInt("cooldown", 0);
                long now = System.currentTimeMillis();
                long lastUsed = main.getDatabaseManager().getCooldown(p.getUniqueId(), reward);
                long elapsed = (now - lastUsed) / 1000;
                long remaining = cooldownSeconds - elapsed;
                String formatted = TimeFormat.formatTimeRemaining(remaining);
                
                finalLore.add(ChatColor.translateAlternateColorCodes('&', (section.getString("lore_in_cooldown").replace("{cooldown}", formatted))));
            } else {
                finalLore.add(ChatColor.translateAlternateColorCodes('&', (section.getString("lore_ready"))));
            }
            
            meta.setLore(finalLore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        return inv;
    }
}
