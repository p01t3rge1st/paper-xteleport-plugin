package com.example.xteleport;

import com.example.xteleport.commands.XBackCommand;
import com.example.xteleport.commands.XDeleteWarpCommand;
import com.example.xteleport.commands.XHomeCommand;
import com.example.xteleport.commands.XTpaCommand;
import com.example.xteleport.commands.XTpaConfCommand;
import com.example.xteleport.commands.XdCommand;
import com.example.xteleport.util.TeleportManager;
import com.example.xteleport.util.XpScoreboardManager;
import com.example.xteleport.util.WarpManager;
import com.example.xteleport.commands.XSetWarpCommand;
import com.example.xteleport.commands.XWarpCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class XTeleportPlugin extends JavaPlugin {
    private WarpManager warpManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        getCommand("xhome").setExecutor(new XHomeCommand(this));
        getCommand("xtpa").setExecutor(new XTpaCommand(this));
        getCommand("xtpaconf").setExecutor(new XTpaConfCommand());
        getCommand("xd").setExecutor(new XdCommand());
        getCommand("xback").setExecutor(new XBackCommand(this));

        warpManager = new WarpManager(this);
        teleportManager = new TeleportManager(this);

        getCommand("xsetwarp").setExecutor(new XSetWarpCommand(warpManager));
        getCommand("xwarp").setExecutor(new XWarpCommand(warpManager, teleportManager));
        getCommand("xdeletewarp").setExecutor(new XDeleteWarpCommand(warpManager));

        // Start XP HUD updater
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!XpScoreboardManager.isHidden(player)) {
                        int xp = TeleportManager.getTotalExperience(player);
                        XpScoreboardManager.showXp(player, xp);
                    }
                }
            }
        }.runTaskTimer(this, 20, 20); // co sekundę

        // Zarejestruj event śmierci
        tm.registerEvents();
    }
}