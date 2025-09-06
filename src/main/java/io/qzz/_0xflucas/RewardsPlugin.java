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

   public void onEnable() {

      this.saveDefaultConfig();
      if (!this.setupDatabase()) {
         Bukkit.getPluginManager().disablePlugin(this);
         Bukkit.getConsoleSender().sendMessage("=> 0xfRewards 2.0 - database connection failed! Plugin disabled!");
      } else {
         registerListeners();
         new RewardCommand(this);
         new RewardMenu(this);
         new UtilsNPC(this);
         sendMessages();
         updateOnlinePlayerCooldowns();
      }
   }

   public void onDisable() {
      if (database != null) {
         database.disconnect();
      }

      Bukkit.getConsoleSender().sendMessage("=> 0xfRewards 2.0 - plugin disabled.");
   }

   void sendMessages() {
      Bukkit.getConsoleSender().sendMessage("==========");
      Bukkit.getConsoleSender().sendMessage("=> 0xfRewards 2.0 - plugin enabled successfully!");
      Bukkit.getConsoleSender().sendMessage("===========");
   }

   void updateOnlinePlayerCooldowns() {
      int count = 0;
      
      for (Player p : Bukkit.getOnlinePlayers()) {
         this.getDatabaseManager().loadPlayerCooldowns(p.getUniqueId());
         count ++;
      }
      
      Bukkit.getConsoleSender().sendMessage("[0xfRewards] updated " + count + " players cooldowns");
   }

   boolean setupDatabase() {
      database = new DatabaseManager(this);

      try {
         database.connect();
         return true;
      } catch (Exception e) {
         // e.printStackTrace();
         this.getLogger().info("Database connection error: " + e.getMessage());
         this.getLogger().info("[0xfRewards] Disabling plugin...");
         return false;
      }
   }

   void registerListeners() {
      this.getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);
      new NPCClickListener(this);
      new PlayerJoinQuitListener(this);
   }

   public DatabaseManager getDatabaseManager() {
      return database;
   }
}