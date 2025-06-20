package com.example.xteleport.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpManager {
    private final JavaPlugin plugin;
    private final File warpsFile;
    private final YamlConfiguration warpsConfig;
    private final Map<String, Location> warps = new HashMap<>();

    public WarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        this.warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        loadWarps();
    }

    private void loadWarps() {
        warps.clear();
        for (String name : warpsConfig.getKeys(false)) {
            Location loc = warpsConfig.getLocation(name);
            if (loc != null && loc.getWorld() != null) {
                warps.put(name.toLowerCase(), loc);
            }
        }
    }

    public void saveWarps() {
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            warpsConfig.set(entry.getKey(), entry.getValue());
        }
        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save warps.yml: " + e.getMessage());
        }
    }

    public void setWarp(String name, Location location) {
        warps.put(name.toLowerCase(), location);
        saveWarps();
    }

    public Location getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public boolean warpExists(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public void deleteWarp(String name) {
        warps.remove(name.toLowerCase());
        warpsConfig.set(name.toLowerCase(), null);
        saveWarps();
    }

    public List<String> getAllWarpNames() {
        return new ArrayList<>(warps.keySet());
    }
}