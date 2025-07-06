package br.com.lucas0001007.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.lucas0001007.RewardsPlugin;
import br.com.lucas0001007.utils.UtilsNPC;

public class RewardCommand implements CommandExecutor {

    RewardsPlugin plugin = RewardsPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("!!![cRewards] this command is only for players!");
            return false;
        }
        
        Player p = (Player) commandSender;
        
        if(p.hasPermission("rewards.op")) {
            if (args.length == 0) {
                p.sendMessage("§eusage: /" + label + " npc");
                p.sendMessage("§eusage: /" + label + " reload/rl");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("npc")) {
                /*
                if(args.length < 2) {
                    p.sendMessage("§cuse: /rewards npc <skin>");
                    return false;
                }
                */
                
                UtilsNPC utils = new UtilsNPC();
                utils.spawn(p.getLocation());
                p.sendMessage("§eRewards NPC spawned");
    
                return true;
            }
            
            if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                plugin.reloadConfig();
                p.sendMessage("§econfig reloaded successfully");
                return true;
            }
        } else {
            p.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        return false;
    }

}
