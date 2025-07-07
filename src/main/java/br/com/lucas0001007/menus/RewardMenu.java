package br.com.lucas0001007.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.lucas0001007.RewardsPlugin;
import br.com.lucas0001007.listeners.MenuClickListener;
import br.com.lucas0001007.utils.FormatColor;
import br.com.lucas0001007.utils.TimeFormat;

public class RewardMenu {

    RewardsPlugin plugin = RewardsPlugin.getInstance();
    MenuClickListener menuClickListener = new MenuClickListener(); // usa o isInCooldown já feito

    public Inventory menu(Player p) {
        FileConfiguration config = plugin.getConfig();
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
            String name = FormatColor.run(section.getString("name"));
            ItemStack item = new ItemStack(Material.valueOf(section.getString("item")));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            
            List<String> initialLore = section.getStringList("lore");
            List<String> finalLore   = new ArrayList<>();
            
            for (String il : initialLore) {
                finalLore.add(FormatColor.run(il));
            }
            
            String perm = section.getString("permission");
            if (!p.hasPermission(perm)) {
                finalLore.add(FormatColor.run(section.getString("lore_no_permission")));
            } else if(menuClickListener.isInCooldown(p, reward, section, false)){
                int cooldownSeconds = section.getInt("cooldown", 0);
                long now = System.currentTimeMillis();
                long lastUsed = plugin.getDatabaseManager().getCooldown(p.getUniqueId(), reward);
                long elapsed = (now - lastUsed) / 1000;
                long remaining = cooldownSeconds - elapsed;
                String formatted = TimeFormat.formatTimeRemaining(remaining);
                
                finalLore.add(FormatColor.run(section.getString("lore_in_cooldown").replace("{cooldown}", formatted)));
            } else {
                finalLore.add(FormatColor.run(section.getString("lore_ready")));
            }
            
            meta.setLore(finalLore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        return inv;
    }
}
