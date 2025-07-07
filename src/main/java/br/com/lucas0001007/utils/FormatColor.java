package br.com.lucas0001007.utils;

import org.bukkit.ChatColor;

public class FormatColor {
    public static String run(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
