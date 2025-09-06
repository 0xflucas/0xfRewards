package io.qzz._0xflucas.utils;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import io.qzz._0xflucas.RewardsPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class UtilsNPC {

    private final RewardsPlugin main;

    public UtilsNPC(RewardsPlugin main) {
        this.main = main;
    }
    public void spawn(Location loc) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, main.getConfig().getString("npc.name"));
        npc.spawn(loc);
    }

}
