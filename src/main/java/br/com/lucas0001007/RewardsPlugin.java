package br.com.lucas0001007;

import br.com.lucas0001007.commands.RewardCommand;
import br.com.lucas0001007.database.DatabaseManager;
import br.com.lucas0001007.listeners.MenuClickListener;
import br.com.lucas0001007.listeners.NPCClickListener;
import br.com.lucas0001007.listeners.PlayerJoinQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RewardsPlugin extends JavaPlugin {
   private static RewardsPlugin instance;
   private static DatabaseManager database;

   public void onEnable() {
      instance = this;
      this.saveDefaultConfig();
      if (!this.setupDatabase()) {
         Bukkit.getPluginManager().disablePlugin(this);
         Bukkit.getConsoleSender().sendMessage("=> cRewards 2.0 - database connection failed! Plugin disabled!");
      } else {
         this.registerCommands();
         this.registerListeners();
         this.sendMessages();
         this.updateOnlinePlayerCooldowns();
      }
   }

   public void onDisable() {
      if (database != null) {
         database.disconnect();
      }

      Bukkit.getConsoleSender().sendMessage("=> cRewards 2.0 - plugin disabled.");
   }

   void sendMessages() {
      Bukkit.getConsoleSender().sendMessage("==========");
      Bukkit.getConsoleSender().sendMessage("=> cRewards 2.0 - plugin enabled successfully!");
      Bukkit.getConsoleSender().sendMessage("===========");
   }

   void updateOnlinePlayerCooldowns() {
      int count = 0;
      
      for (Player p : Bukkit.getOnlinePlayers()) {
         this.getDatabaseManager().loadPlayerCooldowns(p.getUniqueId());
         count ++;
      }
      
      Bukkit.getConsoleSender().sendMessage("[cRewards] updated " + count + " players cooldowns");
   }

   boolean setupDatabase() {
      database = new DatabaseManager();

      try {
         database.connect();
         return true;
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }

   void registerCommands() {
      this.getCommand("rewards").setExecutor(new RewardCommand());
   }

   void registerListeners() {
      this.getServer().getPluginManager().registerEvents(new NPCClickListener(), instance);
      this.getServer().getPluginManager().registerEvents(new MenuClickListener(), instance);
      this.getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), instance);
   }

   public static RewardsPlugin getInstance() {
      return instance;
   }

   public DatabaseManager getDatabaseManager() {
      return database;
   }
}