package br.com.lucas0001007.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.lucas0001007.RewardsPlugin;
import br.com.lucas0001007.menus.RewardMenu;
import br.com.lucas0001007.utils.FormatColor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class NPCClickListener implements Listener {

    RewardsPlugin plugin = RewardsPlugin.getInstance();
    Set<String> toDelete = new HashSet<>();

    @EventHandler
    public void rightClick(NPCRightClickEvent e) {
        NPC npc = e.getNPC();
        Player p = e.getClicker();

        if (npc.getName().equals(plugin.getConfig().getString("npc.name"))) {
            if (toDelete.contains(p.getName())) {
                toDelete.remove(p.getName());
                p.sendMessage(FormatColor.run("&e[Rewards] &fNPC removal cancelled."));
                p.sendMessage(FormatColor.run("npc removed"));
                return;
            }

            p.openInventory(new RewardMenu().menu(p));
        }
    }

    @EventHandler
    public void leftClick(NPCLeftClickEvent e) {
        NPC npc = e.getNPC();
        Player p = e.getClicker();

        if (npc.getName().equals(plugin.getConfig().getString("npc.name"))) {
//            p.sendMessage("a");
            if (p.getItemInHand().getType() == Material.valueOf(plugin.getConfig().getString("item_to_remove"))) {
                if (p.hasPermission("rewards.op")) {
                    e.setCancelled(true);

                    if (toDelete.contains(p.getName())) {
                        CitizensAPI.getNPCRegistry().deregister(npc);
                        p.sendMessage(FormatColor.run("&e[cRewards] &fNPC removed."));
                        toDelete.remove(p.getName());
                    } else {
                        toDelete.add(p.getName());
                        p.sendMessage(FormatColor.run("&e[cRewards] &fare you sure? left click to confirm or right click to cancel."));
//                        p.sendMessage(FormatColor.run(plugin.getConfig().getString("confirm_remove_message")));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (toDelete.contains(p.getName())) {
                                    toDelete.remove(p.getName());
                                    p.sendMessage(FormatColor.run("&e[cRewards] &fNPC removal cancelled."));
                                }
                            }
                        }.runTaskLater(plugin, 20 * 5);
                    }
                }
            }
            // p.sendMessage("debug");
        } // else p.sendMessage("b");
    }
}
