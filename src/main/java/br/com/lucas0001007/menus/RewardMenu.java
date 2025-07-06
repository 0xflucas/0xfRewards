package br.com.lucas0001007.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.lucas0001007.RewardsPlugin;
import br.com.lucas0001007.utils.FormatColor;

public class RewardMenu {

    RewardsPlugin plugin = RewardsPlugin.getInstance();

    public Inventory menu() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection rewards = config.getConfigurationSection("rewards");

        int chestSize = (9 * config.getInt("menu.rows", 5));

//        p.sendMessage("" + chestSize);
        Inventory inv = Bukkit.createInventory(null, chestSize, config.getString("menu.title"));
        if (rewards == null)
            return null;

        for (String reward : rewards.getKeys(false)) {
            ConfigurationSection section = rewards.getConfigurationSection(reward);

            if (section == null)
                continue;

            int slot = section.getInt("slot");
            String name = FormatColor.run(section.getString("name"));
            ItemStack item = new ItemStack(Material.valueOf(section.getString("item")));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            
            List<String> lore = section.getStringList("lore");
            if (lore != null) {
                List<String> formattedLore = new ArrayList<>();
                for (String line : lore) {
                    formattedLore.add(FormatColor.run(line));
                }
                meta.setLore(formattedLore);
            }
            
            item.setItemMeta(meta);

            inv.setItem(slot, item);
        }

        return inv;
    }
}
