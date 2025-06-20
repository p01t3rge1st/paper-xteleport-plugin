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
    public static volatile boolean disruptionActive = false;

    public static void skulcShock(Player player) {
        int cost = ConfigManager.getSkulcShockXpCost();
        int radius = ConfigManager.getSkulcShockRadius();
        int duration = ConfigManager.getSkulcShockDuration();
        // Skulc Shock: zakłóca sensory i wardena, efekt jak w ancient city
        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        Location center = player.getLocation();
        int minY = -2, maxY = 2; // przeszukuj od 2 bloki pod do 2 bloki nad graczem
        List<Location> sculks = new ArrayList<>();
        List<LivingEntity> mobs = new ArrayList<>();

        // Zbierz sculk sensory i shriekery w okręgu r=20 i wysokości od -2 do +2
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                for (int y = minY; y <= maxY; y++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    if (block.getType() == Material.SCULK_SENSOR || block.getType().name().contains("SCULK_SHRIEKER")) {
                        sculks.add(loc.clone());
                    }
                }
            }
        }
        // Znajdź wszystkie moby (nie tylko wardena) w promieniu 20
        for (Entity e : player.getWorld().getNearbyEntities(center, radius, 5, radius)) {
            if (e instanceof LivingEntity living && !(living instanceof Player)) {
                mobs.add(living);
            }
        }

        // Efekt dźwiękowy na starcie
        player.getWorld().playSound(center, Sound.ENTITY_WITHER_AMBIENT, 1.2f, 0.7f);
        player.getWorld().playSound(center, Sound.BLOCK_PORTAL_AMBIENT, 1.2f, 1.2f);

        // Wskaźnik czasu na hotbarze
        for (int i = 0; i < duration; i++) {
            int secondsLeft = duration - i;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
                player.sendActionBar(ChatColor.AQUA + "SCULC SHOCK: " + ChatColor.YELLOW + "Disrupting (" + secondsLeft + "s left)");
            }, i * 20L);
        }

        // Pulsowanie przez 10 sekund (20 razy co 0.5s)
        int pulses = 20;
        for (int i = 0; i < pulses; i++) {
            int delay = i * 10;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
                for (Location loc : sculks) {
                    loc.getWorld().playSound(loc, Sound.BLOCK_SCULK_SENSOR_CLICKING, 1, 1);
                    loc.getWorld().spawnParticle(Particle.SONIC_BOOM, loc.clone().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0);
                }
                for (LivingEntity mob : mobs) {
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 10, false, false, false));
                    mob.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, mob.getLocation().add(0,2,0), 30, 1, 1, 1, 0.2);
                }
            }, delay);
        }

        // Po 10s info
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
            player.sendActionBar(ChatColor.GRAY + "Disruption ended.");
            disruptionActive = false;
        }, duration * 20L);
        disruptionActive = true;
    }

    // SCAN: laser od guardiana, sensory na jasno, shrine na czerwono
    public static void scan(Player player) {
        int cost = ConfigManager.getScanXpCost();
        int radius = ConfigManager.getScanRadius();
        int duration = ConfigManager.getScanDuration();
        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        Location center = player.getLocation().clone().add(0, 1, 0); // waist height
        World world = player.getWorld();
        boolean found = false;

        for (int i = 0; i < duration; i++) {
            int secondsLeft = duration - i;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
                player.sendActionBar(ChatColor.AQUA + "SCAN: " + ChatColor.YELLOW + "Active (" + secondsLeft + "s left)");
            }, i * 20L);
        }

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
                        // Laser od gracza do bloku (dwukrotnie gęstszy)
                        Location laserStart = center.clone();
                        Location laserEnd = block.getLocation().clone().add(0.5, 0.5, 0.5);
                        Vector laserDir = laserEnd.toVector().subtract(laserStart.toVector()).normalize();
                        double distance = laserStart.distance(laserEnd);
                        Particle.DustOptions dust = isSensor
                            ? new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 200, 255), 2.0f) // blue
                            : new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 0, 0), 2.0f);   // red
                        for (double d = 0; d < distance; d += 0.25) { // DWUKROTNIE GĘSTSZY LASER
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
        final int[] mobCount = {0};
        int glowingTicks = duration * 20; // POPRAWKA: czas z configu
        for (Entity entity : world.getNearbyEntities(player.getLocation(), 40, 40, 40)) {
            if (entity instanceof LivingEntity living) {
                living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, glowingTicks, 1, false, false, false));
                mobCount[0]++;
            }
        }

        // WYŚWIETL OD RAZU
        player.sendMessage(ChatColor.AQUA + "Scan detected " + ChatColor.YELLOW + mobCount[0] + ChatColor.AQUA + " mobs.");

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
            player.sendActionBar(ChatColor.GRAY + "Scan ended.");
        }, duration * 20L);

        if (!found) {
            player.sendMessage(ChatColor.GRAY + "No sculk sensors or shriekers detected nearby.");
        }
        world.playSound(player.getLocation(), Sound.ENTITY_GUARDIAN_ATTACK, 1, 1.2f);
    }

    // Fireball: wystrzeliwuje fireballa od gracza
    public static void fireball(Player player) {
        int cost = ConfigManager.getFireballXpCost();
        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        int duration = 5;
        for (int i = 0; i < duration; i++) {
            int secondsLeft = duration - i;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("xlink"), () -> {
                player.sendActionBar(ChatColor.GOLD + "FIREBALL: " + ChatColor.YELLOW + secondsLeft + "s cooldown");
            }, i * 20L);
        }

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();
        Fireball fireball = player.getWorld().spawn(loc.add(dir.multiply(1.5)), Fireball.class);
        fireball.setDirection(dir);
        fireball.setShooter(player);
        fireball.setYield(2.5f);
        fireball.setIsIncendiary(true);

        player.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 1, 1);
    }

    // NOWY SKILL: fullbright
    public static void fullbright(Player player) {
        int cost = ConfigManager.getFullbrightXpCost();
        int duration = ConfigManager.getFullbrightDuration();
        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration * 20, 0, false, false, false));
        player.sendMessage(ChatColor.GOLD + "Fullbright enabled for " + duration + " seconds! (-" + cost + " XP)");
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1.5f);
    }

    public static void decoy(Player player) {
        int cost = ConfigManager.getDecoyXpCost();
        int duration = ConfigManager.getDecoyDuration();
        int radius = ConfigManager.getDecoyRadius();

        if (TeleportManager.getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " XP!");
            return;
        }
        TeleportManager.takeRawXp(player, cost);

        // Zbierz moby, które będą focusować gracza
        List<org.bukkit.entity.Mob> focusedMobs = new ArrayList<>();
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (entity instanceof org.bukkit.entity.Mob mob) {
                mob.setTarget(player);
                focusedMobs.add(mob);
            }
        }
        player.sendMessage(ChatColor.GREEN + "Decoy activated! " + focusedMobs.size() + " mobs are now focusing you.");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_TURTLE_EGG_CRACK, 1, 1.2f);

        // Przez kilka sekund ponawiaj setTarget, by moby nie przestały gonić
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ >= duration) {
                    // Po zakończeniu efektu resetuj targety
                    for (org.bukkit.entity.Mob mob : focusedMobs) {
                        if (mob.getTarget() != null && mob.getTarget().equals(player)) {
                            mob.setTarget(null);
                        }
                    }
                    player.sendMessage(ChatColor.GRAY + "Decoy effect ended. Mobs will now behave normally.");
                    cancel();
                    return;
                }
                for (org.bukkit.entity.Mob mob : focusedMobs) {
                    mob.setTarget(player);
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("xlink"), 0, 20); // co sekundę przez czas trwania
    }
}