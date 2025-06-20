package com.example.xteleport.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private static FileConfiguration config;

    public static void init(JavaPlugin plugin) {
        config = plugin.getConfig();
    }

    public static int getScanRadius() {
        return config.getInt("scan.radius", 15);
    }

    public static int getScanDuration() {
        return config.getInt("scan.duration", 10);
    }

    public static int getScanXpCost() {
        return config.getInt("scan.xp_cost", 25);
    }

    public static int getSkulcShockRadius() {
        return config.getInt("skulcshock.radius", 20);
    }

    public static int getSkulcShockDuration() {
        return config.getInt("skulcshock.duration", 10);
    }

    public static int getSkulcShockXpCost() {
        return config.getInt("skulcshock.xp_cost", 200);
    }

    public static int getFireballXpCost() {
        return config.getInt("fireball.xp_cost", 100);
    }

    public static int getFullbrightXpCost() {
        return config.getInt("fullbright.xp_cost", 50);
    }

    public static int getFullbrightDuration() {
        return config.getInt("fullbright.duration", 30);
    }

    public static int getXBackXpCost() {
        return config.getInt("xback.xp_cost", 7);
    }

    public static String getTeleportXpCostMode() {
        return config.getString("teleport.xp_cost_mode", "constant");
    }

    public static float getTeleportXpCostValue() {
        return (float) config.getDouble("teleport.xp_cost_value", 5.0);
    }
}