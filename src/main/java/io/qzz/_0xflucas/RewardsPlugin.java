package io.qzz._0xflucas;

import io.qzz._0xflucas.commands.RewardCommand;
import io.qzz._0xflucas.database.DatabaseManager;
import io.qzz._0xflucas.listeners.MenuClickListener;
import io.qzz._0xflucas.listeners.NPCClickListener;
import io.qzz._0xflucas.listeners.PlayerJoinQuitListener;
import io.qzz._0xflucas.menus.RewardMenu;
import io.qzz._0xflucas.utils.UtilsNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RewardsPlugin extends JavaPlugin {

   private DatabaseManager database;

   @Override
   public void onEnable() {

      this.saveDefaultConfig();
      database = new DatabaseManager(this);

      if (this.getConfig().getBoolean("mysql.use")) {
         try {
            database.connect();
         } catch (Exception e) {
            this.getLogger().severe("=> 0xfRewards 2.0.2 - MySQL connection failed! Plugin disabled!");
            this.getLogger().severe("Error: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
         }
      }

      registerListeners();
      new RewardCommand(this);
      new RewardMenu(this);
      new UtilsNPC(this);

      sendConsoleMessages();
      updateOnlinePlayerCooldowns();
   }

   @Override
   public void onDisable() {
      if (database != null) {
         database.disconnect();
      }

      Bukkit.getConsoleSender().sendMessage("=> 0xfRewards 2.0.2 - plugin disabled.");
   }

   private void sendConsoleMessages() {
      Bukkit.getConsoleSender().sendMessage("==========");
      Bukkit.getConsoleSender().sendMessage("=> 0xfRewards 2.0.2 - plugin enabled successfully!");
      Bukkit.getConsoleSender().sendMessage("===========");
   }

   private void updateOnlinePlayerCooldowns() {
      int count = 0;

      for (Player p : Bukkit.getOnlinePlayers()) {
         database.loadPlayerCooldowns(p.getUniqueId());
         count++;
      }

      Bukkit.getConsoleSender().sendMessage("[0xfRewards] updated " + count + " players cooldowns");
   }

   private void registerListeners() {
      this.getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);
      this.getServer().getPluginManager().registerEvents(new NPCClickListener(this), this);
      this.getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
   }

   public DatabaseManager getDatabaseManager() {
      return database;
   }
}
