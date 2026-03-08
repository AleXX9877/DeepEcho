package com.deepecho.listeners;

import com.deepecho.DeepEcho;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class SonarListener implements Listener {

    private final DeepEcho plugin;

    private final Set<Material> valuableBlocks = new HashSet<>();

    public SonarListener(DeepEcho plugin) {

        this.plugin = plugin;

        valuableBlocks.add(Material.DIAMOND_ORE);
        valuableBlocks.add(Material.DEEPSLATE_DIAMOND_ORE);
        valuableBlocks.add(Material.EMERALD_ORE);
        valuableBlocks.add(Material.DEEPSLATE_EMERALD_ORE);

        valuableBlocks.add(Material.END_STONE_BRICKS);
        valuableBlocks.add(Material.POLISHED_DEEPSLATE);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {

        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        if (event.getHitBlock() == null) return;

        if (player.getLocation().getY() >= 0) return;

        triggerPulse(player, arrow.getLocation());
    }



    @EventHandler
    public void onShieldBlock(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;

        if (!player.isBlocking()) return;

        if (player.getLocation().getY() >= 0) return;

        triggerPulse(player, player.getLocation());
    }

    private void triggerPulse(Player player, Location origin) {

        World world = player.getWorld();

        int radius = 10;
        int found = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    Location loc = origin.clone().add(x, y, z);
                    Block block = world.getBlockAt(loc);

                    if (valuableBlocks.contains(block.getType())) {

                        found++;

                        spawnEchoMarker(block.getLocation().add(0.5,0.5,0.5));
                    }
                }
            }
        }

        aggroNearbyMobs(player);

        playEchoSound(player, found);
    }

    /*
     PARTICELLA CHE ATTRAVERSA I BLOCCHI
     */

    private void spawnEchoMarker(Location loc) {

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks > 60) {
                    cancel();
                    return;
                }

                loc.getWorld().spawnParticle(
                        Particle.GLOW_SQUID_INK,
                        loc,
                        2,
                        0.2,0.2,0.2,
                        0
                );

                ticks += 10;
            }

        }.runTaskTimer(plugin,0,10);
    }

    /*
     AGGRO MOB
     */

    private void aggroNearbyMobs(Player player) {

        for (Entity entity : player.getNearbyEntities(15,15,15)) {

            if (!(entity instanceof Monster mob)) continue;

            mob.setTarget(player);

            mob.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.GLOWING,
                            100,
                            1
                    )
            );
        }
    }

    /*
     AUDIO DINAMICO
     */

    private void playEchoSound(Player player, int found) {

        float pitch = 0.5f;
        float volume = 1f;

        if (found >= 1) pitch = 0.7f;
        if (found >= 3) pitch = 1.0f;
        if (found >= 6) pitch = 1.3f;

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_SCULK_SENSOR_CLICKING,
                volume,
                pitch
        );
    }
}