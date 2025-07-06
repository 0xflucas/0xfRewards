package br.com.lucas0001007.listeners;

import br.com.lucas0001007.RewardsPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private RewardsPlugin plugin = RewardsPlugin.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getDatabaseManager().loadPlayerCooldowns(e.getPlayer().getUniqueId());
        System.out.println("loaded cooldowns for " + e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getDatabaseManager().unloadPlayer(e.getPlayer().getUniqueId());
        System.out.println("unloaded cooldowns for " + e.getPlayer().getName());
    }

}
