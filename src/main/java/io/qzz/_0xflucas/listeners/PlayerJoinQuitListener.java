package io.qzz._0xflucas.listeners;

import io.qzz._0xflucas.RewardsPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private final RewardsPlugin main;

    public PlayerJoinQuitListener(RewardsPlugin main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
        main.getLogger().info("PlayerJoinQuitListener registrado");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        main.getDatabaseManager().loadPlayerCooldowns(e.getPlayer().getUniqueId());
        System.out.println("loaded cooldowns for " + e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        main.getDatabaseManager().unloadPlayer(e.getPlayer().getUniqueId());
        System.out.println("unloaded cooldowns for " + e.getPlayer().getName());
    }

}
