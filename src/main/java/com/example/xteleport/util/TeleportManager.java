package com.example.xteleport.util;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;

import java.util.*;

public class TeleportManager implements Listener {
    private static String xpCostMode = "constant";
    private static float xpCostValue = 5.0f; // domyślnie 5 poziomów
    private final JavaPlugin plugin;
    private final Map<UUID, BukkitRunnable> pendingTeleports = new HashMap<>();
    private static final Map<UUID, Location> lastDeathLocation = new HashMap<>();

    public TeleportManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void setXpCostMode(String mode, float value) {
        if (mode.equalsIgnoreCase("constant") || mode.equalsIgnoreCase("by_distance")) {
            xpCostMode = mode.toLowerCase();
            xpCostValue = value;
        }
    }

    public static boolean hasEnoughXp(Player player, Location from, Location to) {
        int cost = getXpCost(from, to);
        return player.getLevel() >= cost;
    }

    public static int getXpCost(Location from, Location to) {
        if (xpCostMode.equals("by_distance")) {
            double distance = from.distance(to);
            return (int) Math.ceil(distance * xpCostValue);
        }
        return (int) Math.ceil(xpCostValue);
    }

    public void teleportPlayerToPlayer(Player player, Player target) {
        // Inform target player instantly
        target.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + " is creating a teleport link to you!");
        target.playSound(target.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1, 1);

        teleportWithDelay(player, target.getLocation());
    }

    public void teleportPlayerToLocation(Player player, Location location) {
        teleportWithDelay(player, location);
    }

    private void teleportWithDelay(Player player, Location target) {
        if (pendingTeleports.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Teleportation already in progress!");
            return;
        }

        double distance = player.getLocation().distance(target);
        int cost = getXpCost(player.getLocation(), target);

        // NOWY KOD: czas zależny od dystansu
        int seconds = (int) Math.ceil(distance / 100.0);
        if (seconds < 1) seconds = 1;
        if (seconds > 5) seconds = 5;

        if (getTotalExperience(player) < cost) {
            player.sendMessage(ChatColor.RED + "You don't have enough experience points to teleport!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "Teleporting in " + seconds + " seconds for " + cost + " XP (" +
                String.format("%.1f", distance) + " blocks).");
        player.sendMessage(ChatColor.GRAY + "Do not move! Moving will cancel the teleportation.");

        final int secondsFinal = seconds;

        BukkitRunnable particleTask = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ >= secondsFinal * 20) {
                    cancel();
                    return;
                }
                // Rozszerzające się koło particles
                double progress = (double) ticks / (secondsFinal * 20);
                double radius = 0.5 + 2.5 * progress; // od 0.5 do 3.0
                int points = 32;
                Location center = player.getLocation().add(0, 0.1, 0);
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location particleLoc = center.clone().add(x, 0, z);
                    // Migający efekt: co drugi tick inny kolor
                    Particle.DustOptions dust = new Particle.DustOptions(
                        (ticks % 10 < 5) ? org.bukkit.Color.fromRGB(0, 255, 255) : org.bukkit.Color.fromRGB(255, 255, 0), // cyan/żółty
                        1.5f
                    );
                    player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, dust);
                }
            }
        };
        particleTask.runTaskTimer(plugin, 0, 2);

        final BukkitRunnable particleTaskFinal = particleTask; // Dodaj to przed kolejnym BukkitRunnable

        // Teleport delay
        BukkitRunnable teleportTask = new BukkitRunnable() {
            Location startLoc = player.getLocation().clone();
            int timeLeft = secondsFinal;
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    player.sendMessage(ChatColor.RED + "Teleportation cancelled.");
                    player.removePotionEffect(PotionEffectType.SPEED);
                    cancel();
                    particleTask.cancel();
                    pendingTeleports.remove(player.getUniqueId());
                    return;
                }
                if (player.getLocation().distance(startLoc) > 0.2) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    if (timeLeft == 0) {
                        player.getWorld().strikeLightning(player.getLocation());
                        player.sendMessage(ChatColor.RED + "You moved in the last second! You have been struck by lightning!");
                    } else {
                        player.sendMessage(ChatColor.RED + "You moved! Teleportation cancelled.");
                    }
                    cancel();
                    particleTask.cancel();
                    pendingTeleports.remove(player.getUniqueId());
                    return;
                }
                // Give speed effect for FOV
                int speedLevel = 0;
                if (timeLeft <= 1) speedLevel = 3;
                else if (timeLeft == 2) speedLevel = 2;
                else if (timeLeft == 3) speedLevel = 1;
                if (speedLevel > 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, speedLevel - 1, true, false, false));
                } else {
                    player.removePotionEffect(PotionEffectType.SPEED);
                }

                if (timeLeft > 0) {
                    player.sendMessage(ChatColor.AQUA + "Teleporting in " + timeLeft + " seconds...");
                }
                if (timeLeft-- <= 0) {
                    player.removePotionEffect(PotionEffectType.SPEED);

                    // --- ANCIENT CITY / DEEP DARK CHECK ---
                    Biome biome = player.getLocation().getBlock().getBiome();
                    if (biome == Biome.DEEP_DARK || biome.name().contains("ANCIENT_CITY")) {
                        // Dźwięki
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1);
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);

                        // Particle - jaskrawy kolor (np. cyan)
                        Particle.DustOptions dust = new Particle.DustOptions(org.bukkit.Color.fromRGB(0,255,255), 2.0f);
                        player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0,1,0), 100, 1, 1, 1, 0, dust);

                        // Pulsujące aktywowanie skulk sensorów w walcu: promień 30, wysokość 5 (od stóp gracza)
                        int radius = 30;
                        int height = 5;
                        Location center = player.getLocation().clone();
                        List<Location> skulkSensors = new ArrayList<>();

                        // Zbierz skulk sensory tylko raz (przy wywołaniu efektu)
                        for (int x = -radius; x <= radius; x++) {
                            for (int z = -radius; z <= radius; z++) {
                                if (x * x + z * z > radius * radius) continue; // tylko w obrębie koła
                                for (int y = 0; y < height; y++) {
                                    Location loc = center.clone().add(x, y, z);
                                    if (loc.getBlock().getType() == Material.SCULK_SENSOR) {
                                        skulkSensors.add(loc.clone());
                                    }
                                }
                            }
                        }

                        // Pulsowanie przez 5 sekund (10 razy co 0.5s)
                        int pulses = 10;
                        for (int i = 0; i < pulses; i++) {
                            int delay = i * 10; // 10 ticków = 0.5 sekundy
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                for (Location loc : skulkSensors) {
                                    loc.getWorld().playSound(loc, Sound.BLOCK_SCULK_SENSOR_CLICKING, 1, 1);
                                    loc.getWorld().spawnParticle(Particle.SONIC_BOOM, loc.clone().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0);
                                }
                            }, delay);
                        }

                        flashFlicker(player, plugin);

                        player.sendTitle(
                            ChatColor.RED + "Link disrupted!",
                            ChatColor.DARK_RED + "by the dark energy of the Deep Dark",
                            5, 40, 10
                        );

                        cancel();
                        particleTask.cancel();
                        pendingTeleports.remove(player.getUniqueId());
                        return;
                    }
                    // --- KONIEC ANCIENT CITY CHECK ---

                    // Deduct XP
                    takeRawXp(player, cost);

                    // Teleport and effects
                    player.teleport(target);
                    player.getWorld().strikeLightningEffect(target);
                    player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 40, 1, false, false, false)); // 2 sekundy

                    // Szybkie migotanie: usuwanie efektu po 0.5 sekundy
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }, 10L); // 0.5 sekundy (10 ticków)

                    flashFlicker(player, plugin);

                    player.sendMessage(ChatColor.GREEN + "Teleportation complete!");
                    cancel();
                    particleTask.cancel();
                    pendingTeleports.remove(player.getUniqueId());
                }
            }
        };
        teleportTask.runTaskTimer(plugin, 0, 20);
        pendingTeleports.put(player.getUniqueId(), teleportTask);
    }

    private void flashFlicker(Player player, JavaPlugin plugin) {
        Random random = new Random();
        int flashes = 6 + random.nextInt(4); // 6-9 mignięć
        int tick = 0;
        for (int i = 0; i < flashes; i++) {
            int onTicks = 4 + random.nextInt(4);  // 0.2-0.35s
            int offTicks = 2 + random.nextInt(4); // 0.1-0.3s
            // Włącz NIGHT_VISION
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20, 1, false, false, false));
            }, tick);
            tick += onTicks;
            // Wyłącz NIGHT_VISION
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }, tick);
            tick += offTicks;
        }
    }

    public boolean isTeleporting(Player player) {
        return pendingTeleports.containsKey(player.getUniqueId());
    }

    public void cancelTeleport(Player player) {
        BukkitRunnable task = pendingTeleports.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            player.sendMessage(ChatColor.RED + "Teleportation cancelled due to movement.");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isTeleporting(event.getPlayer())) {
            cancelTeleport(event.getPlayer());
        }
    }

    // Zarejestruj event śmierci
    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        lastDeathLocation.put(player.getUniqueId(), player.getLocation());
    }

    public static Location getLastDeathLocation(Player player) {
        return lastDeathLocation.get(player.getUniqueId());
    }

    // Zwraca całkowitą ilość punktów XP gracza
    public static int getTotalExperience(Player player) {
        int level = player.getLevel();
        float exp = player.getExp();
        int total = 0;

        // Punkty za poprzednie poziomy
        for (int i = 0; i < level; i++) {
            total += getXpToLevel(i);
        }
        // Punkty za aktualny pasek exp
        total += Math.round(getXpToLevel(level) * exp);
        return total;
    }

    // Zwraca ile XP potrzeba na dany poziom
    public static int getXpToLevel(int level) {
        if (level >= 31) return 112 + (level - 30) * 9;
        if (level >= 16) return 37 + (level - 15) * 5;
        return 7 + level * 2;
    }

    // Odejmij surowe XP
    public static void takeRawXp(Player player, int xp) {
        int total = getTotalExperience(player) - xp;
        if (total < 0) total = 0;
        player.setExp(0);
        player.setLevel(0);
        player.giveExp(total);
    }
}