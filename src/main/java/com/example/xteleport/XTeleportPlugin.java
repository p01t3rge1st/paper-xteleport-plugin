package com.example.xteleport;

import com.example.xteleport.commands.XBackCommand;
import com.example.xteleport.commands.XDeleteWarpCommand;
import com.example.xteleport.commands.XHomeCommand;
import com.example.xteleport.commands.XMenuCommand;
import com.example.xteleport.commands.XTpaCommand;
import com.example.xteleport.commands.XTpaConfCommand;
import com.example.xteleport.commands.XdCommand;
import com.example.xteleport.listeners.SkillsMenuListener;
import com.example.xteleport.util.SkillsMenuManager;
import com.example.xteleport.util.TeleportManager;
import com.example.xteleport.util.XpScoreboardManager;
import com.example.xteleport.util.WarpManager;
import com.example.xteleport.commands.XSetWarpCommand;
import com.example.xteleport.commands.XWarpCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class XTeleportPlugin extends JavaPlugin implements Listener {
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
        getCommand("xmenu").setExecutor(new XMenuCommand());

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
        teleportManager.registerEvents();

        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new SkillsMenuListener(teleportManager), this);

        getCommand("xlink").setExecutor((sender, command, label, args) -> {
            if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
                // Przekieruj do /xtpa -help
                Bukkit.dispatchCommand(sender, "xtpa -help");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Use /xlink help for command list.");
            return true;
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendTitle(ChatColor.AQUA + "X-link active", "", 10, 40, 10);
        player.sendMessage(ChatColor.GREEN + "Hello " + player.getName() + "! Use " + ChatColor.YELLOW + "/xlink help" + ChatColor.GREEN + " for command list.");
    }
}