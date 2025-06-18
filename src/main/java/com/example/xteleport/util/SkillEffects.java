package com.example.xteleport.util;

import com.example.xteleport.commands.XSkillCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SkillEffects {
    // Skulc Shock: zakłóca sensory i wardena, efekt jak w ancient city
    public static void skulcShock(Player player) {
        int cost = 200;
        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need 200 XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        Location center = player.getLocation();
        int radius = 30;
        int height = 5;
        List<Location> sculks = new ArrayList<>();
        List<LivingEntity> wardens = new ArrayList<>();

        // Zbierz wszystkie sculk sensory i shriekery w promieniu 30
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                for (int y = 0; y < height; y++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    if (block.getType() == Material.SCULK_SENSOR || block.getType().name().contains("SCULK_SHRIEKER")) {
                        sculks.add(loc.clone());
                    }
                }
            }
        }
        // Znajdź wardena w promieniu 30
        for (Entity e : player.getWorld().getNearbyEntities(center, radius, height, radius)) {
            if (e.getType() == EntityType.WARDEN && e instanceof LivingEntity living) {
                wardens.add(living);
            }
        }

        // Efekt dźwiękowy na starcie
        player.getWorld().playSound(center, Sound.ENTITY_WITHER_AMBIENT, 1.2f, 0.7f);
        player.getWorld().playSound(center, Sound.BLOCK_PORTAL_AMBIENT, 1.2f, 1.2f);

        // Pulsowanie przez 10 sekund (20 razy co 0.5s)
        int pulses = 20;
        for (int i = 0; i < pulses; i++) {
            int delay = i * 10;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
                for (Location loc : sculks) {
                    loc.getWorld().playSound(loc, Sound.BLOCK_SCULK_SENSOR_CLICKING, 1, 1);
                    loc.getWorld().spawnParticle(Particle.SONIC_BOOM, loc.clone().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0);
                }
                for (LivingEntity warden : wardens) {
                    warden.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 10, false, false, false));
                    warden.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, warden.getLocation().add(0,2,0), 30, 1, 1, 1, 0.2);
                }
            }, delay);
        }

        // Hotbar info
        player.sendActionBar(ChatColor.AQUA + "SCULC SHOCK: " + ChatColor.YELLOW + "Disrupting sensors & shriekers for 10s!");

        // Po 10s info
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
            player.sendActionBar(ChatColor.GRAY + "Disruption ended.");
        }, 200);
    }

    // SCAN: laser od guardiana, sensory na jasno, shrine na czerwono
    public static void scan(Player player) {
        int cost = 25;
        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need 25 XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        Location center = player.getLocation().clone().add(0, 1, 0); // waist height
        World world = player.getWorld();
        int radius = 15;
        boolean found = false;

        // Przeszukaj kulę o promieniu 15 bloków
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx*dx + dy*dy + dz*dz > radius*radius) continue;
                    Location loc = center.clone().add(dx, dy, dz);
                    Block block = world.getBlockAt(loc);
                    boolean isSensor = block.getType() == Material.SCULK_SENSOR;
                    boolean isShrieker = block.getType().name().contains("SCULK_SHRIEKER");
                    if (isSensor || isShrieker) {
                        // Laser od gracza do bloku
                        Location laserStart = center.clone();
                        Location laserEnd = block.getLocation().clone().add(0.5, 0.5, 0.5);
                        Vector laserDir = laserEnd.toVector().subtract(laserStart.toVector()).normalize();
                        double distance = laserStart.distance(laserEnd);
                        Particle.DustOptions dust = isSensor
                            ? new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 200, 255), 2.0f) // blue
                            : new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 0, 0), 2.0f);   // red
                        for (double d = 0; d < distance; d += 0.5) {
                            Location laserPoint = laserStart.clone().add(laserDir.clone().multiply(d));
                            world.spawnParticle(Particle.DUST, laserPoint, 1, 0, 0, 0, 0, dust);
                        }

                        // Glowing armor stand
                        ArmorStand stand = (ArmorStand) world.spawnEntity(
                                block.getLocation().clone().add(0.5, 0.5, 0.5),
                                EntityType.ARMOR_STAND
                        );
                        stand.setVisible(false);
                        stand.setMarker(true);
                        stand.setSmall(true);
                        stand.setInvulnerable(true);
                        stand.setGravity(false);
                        stand.setSilent(true);
                        stand.setCustomNameVisible(false);
                        stand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1, false, false, false));
                        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), stand::remove, 200);

                        // Efekty
                        if (isSensor) {
                            world.spawnParticle(Particle.END_ROD, block.getLocation().clone().add(0.5,0.5,0.5), 20, 0.2, 0.2, 0.2, 0);
                            world.playSound(block.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1.8f);
                        } else {
                            world.spawnParticle(Particle.CRIT, block.getLocation().clone().add(0.5,0.5,0.5), 20, 0.2, 0.2, 0.2, 0);
                            world.playSound(block.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 0.5f);
                        }
                        found = true;
                    }
                }
            }
        }

        // Glowing dla wszystkich mobów w promieniu 40 bloków
        for (Entity entity : world.getNearbyEntities(player.getLocation(), 40, 40, 40)) {
            if (entity instanceof LivingEntity living && !(living instanceof Player)) {
                living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1, false, false, false));
            }
        }

        if (!found) {
            player.sendMessage(ChatColor.GRAY + "No sculk sensors or shriekers detected nearby.");
        } else {
            player.sendActionBar(ChatColor.AQUA + "SCAN: " + ChatColor.YELLOW + "Target detected!");
        }
        world.playSound(player.getLocation(), Sound.ENTITY_GUARDIAN_ATTACK, 1, 1.2f);
    }

    // Fireball: wystrzeliwuje fireballa od gracza
    public static void fireball(Player player) {
        int cost = 100;
        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need 100 XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();
        Fireball fireball = player.getWorld().spawn(loc.add(dir.multiply(1.5)), Fireball.class);
        fireball.setDirection(dir);
        fireball.setShooter(player);
        fireball.setYield(2.5f);
        fireball.setIsIncendiary(true);

        player.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 1, 1);

        player.sendActionBar(ChatColor.GOLD + "FIREBALL!");
    }

}