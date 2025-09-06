package io.qzz._0xflucas.listeners;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.qzz._0xflucas.RewardsPlugin;
import io.qzz._0xflucas.menus.RewardMenu;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class NPCClickListener implements Listener {

    private final Set<String> toDelete;
    private final RewardsPlugin main;

    public NPCClickListener(RewardsPlugin main) {
        this.main = main;
        this.toDelete = new HashSet<>();
        main.getServer().getPluginManager().registerEvents(this, main);
        main.getLogger().info("NPCClickListener registrado");
    }

    @EventHandler
    public void rightClick(NPCRightClickEvent e) {
        NPC npc = e.getNPC();
        Player p = e.getClicker();

        String npcName = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("npc.name"));

        //p.sendMessage("npc name: '" + npc.getName() + "'");
        //p.sendMessage("config npc name: '" + npcName + "'");
        if (npc.getName().equals(ChatColor.stripColor(npcName))) {
            if (toDelete.contains(p.getName())) {
                toDelete.remove(p.getName());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&e[0xfRewards] &fRemoção do NPC cancelada.")));
                return;
            }

            //p.sendMessage("inhand: ' " + p.getItemInHand() + "'");
            p.openInventory(new RewardMenu(main).menu(p));
        } else {
            //p.sendMessage(FormatColor.run("&cThis is not a valid NPC."));
        }
    }

    @EventHandler
    public void leftClick(NPCLeftClickEvent e) {
        NPC npc = e.getNPC();
        Player p = e.getClicker();

        String npcName = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("npc.name"));
        ItemStack handItem = null;

        try {
            Method getItemInMainHand = p.getInventory().getClass().getMethod("getItemInMainHand");
            Object result = getItemInMainHand.invoke(p.getInventory());
            if (result instanceof ItemStack) {
                handItem = (ItemStack) result;
            }
        } catch (NoSuchMethodException nsme) {
        } catch (Throwable t) {
            main.getLogger().warning("Erro ao tentar usar reflection getItemInMainHand(): " + t.getMessage());
        }

        if (handItem == null) {
            handItem = p.getItemInHand();
        }

        Material inMat = (handItem == null || handItem.getType() == null) ? Material.AIR : handItem.getType();

        String configItemStr = main.getConfig().getString("item_to_remove", "AIR");
        Material configMat;
        try {
            configMat = Material.valueOf(configItemStr.toUpperCase().replace(" ", "_"));
        } catch (Exception ex) {
            configMat = Material.AIR;
        }

        if (npc.getName().equals(ChatColor.stripColor(npcName)) && inMat == configMat) {
            e.setCancelled(true);

            if (toDelete.contains(p.getName())) {
                CitizensAPI.getNPCRegistry().deregister(npc);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&e[0xfRewards] &fNPC removido.")));
                toDelete.remove(p.getName());
            } else {
                toDelete.add(p.getName());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&e[0xfRewards] &fTem certeza? Clique com o botão esquerdo novamente para confirmar, clique com o botão direito para cancelar.")));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (toDelete.contains(p.getName())) {
                            toDelete.remove(p.getName());
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&e[0xfRewards] &fRemoção do NPC cancelada.")));
                        }
                    }
                }.runTaskLater(main, 20 * 5); // 5 segundos
            }
        }
    }
}
