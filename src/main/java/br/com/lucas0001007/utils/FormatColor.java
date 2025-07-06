package br.com.lucas0001007.utils;

import org.bukkit.ChatColor;

public class FormatColor {
    public static String run(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
