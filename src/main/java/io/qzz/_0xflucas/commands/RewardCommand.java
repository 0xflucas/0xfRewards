package io.qzz._0xflucas.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.qzz._0xflucas.RewardsPlugin;
import io.qzz._0xflucas.utils.UtilsNPC;

public class RewardCommand implements CommandExecutor {

    private final RewardsPlugin main;

    public RewardCommand(RewardsPlugin main) {
        this.main = main;
        main.getCommand("rewards").setExecutor(this);
        main.getLogger().info("Comando /rewards registrado");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§c[0xfRewards] Este comando só pode ser usado por jogadores!");
            return false;
        }

        Player p = (Player) commandSender;

        if(p.hasPermission("rewards.op")) {
            if (args.length == 0) {
                p.sendMessage("§eUse: /" + label + " npc");
                p.sendMessage("§eUse: /" + label + " reload/rl");
                //p.sendMessage("§eUse: /" + label + " gui");
                return true;
            }

            if (args[0].equalsIgnoreCase("npc")) {
                /*
                if(args.length < 2) {
                    p.sendMessage("§cUse: /rewards npc <skin>");
                    return false;
                }
                */

                UtilsNPC utils = new UtilsNPC(main);
                utils.spawn(p.getLocation());
                p.sendMessage("§eNPC de Recompensas criado");

                return true;
            }

            if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                main.reloadConfig();
                p.sendMessage("§aConfig recarregada com sucesso");
                return true;
            }
        } else {
            p.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }

        return false;
    }

}
