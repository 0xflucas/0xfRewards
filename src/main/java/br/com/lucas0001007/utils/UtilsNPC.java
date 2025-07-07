package br.com.lucas0001007.utils;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import br.com.lucas0001007.RewardsPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class UtilsNPC {

    private final RewardsPlugin plugin = RewardsPlugin.getInstance();

    public void spawn(Location loc) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, plugin.getConfig().getString("npc.name"));
        npc.spawn(loc);
    }

}
