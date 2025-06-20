package com.example.xteleport;

import com.example.xteleport.commands.XBackCommand;
import com.example.xteleport.commands.XDeleteWarpCommand;
import com.example.xteleport.commands.XHomeCommand;
import com.example.xteleport.commands.XLinkReloadCommand;
import com.example.xteleport.commands.XMenuCommand;
import com.example.xteleport.commands.XSkillCommand;
import com.example.xteleport.commands.XTpaCommand;
import com.example.xteleport.commands.XTpaConfCommand;
import com.example.xteleport.commands.XdCommand;
import com.example.xteleport.commands.WarpTabCompleter;
import com.example.xteleport.listeners.SkillsMenuListener;
import com.example.xteleport.listeners.PlayerMoveListener;
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
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import com.example.xteleport.commands.TellNoBenioCommand;
import com.example.xteleport.util.ConfigManager;

public class XTeleportPlugin extends JavaPlugin implements Listener {
    private WarpManager warpManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigManager.init(this);
        TeleportManager.loadXpCostFromConfig();
        warpManager = new WarpManager(this);
        teleportManager = new TeleportManager(this);

        getCommand("xhome").setExecutor(new XHomeCommand(this));
        getCommand("xtpa").setExecutor(new XTpaCommand(teleportManager));
        getCommand("xtpaconf").setExecutor(new XTpaConfCommand());
        getCommand("xd").setExecutor(new XdCommand());
        getCommand("xback").setExecutor(new XBackCommand(this));

        getCommand("xsetwarp").setExecutor(new XSetWarpCommand(warpManager));
        getCommand("xwarp").setExecutor(new XWarpCommand(warpManager, teleportManager));
        getCommand("xdeletewarp").setExecutor(new XDeleteWarpCommand(warpManager));
        getCommand("xmenu").setExecutor(new XMenuCommand());
        getCommand("xskill").setExecutor(new XSkillCommand());
        getCommand("tellnobenio").setExecutor(new TellNoBenioCommand());
        getCommand("xlinkreload").setExecutor(new XLinkReloadCommand(this));

        // Set tab completers
        getCommand("xwarp").setTabCompleter(new WarpTabCompleter(warpManager));
        getCommand("xdeletewarp").setTabCompleter(new WarpTabCompleter(warpManager));

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
        }.runTaskTimer(this, 20, 100); // co 5 sekund zamiast co sekundę

        // Zarejestruj event śmierci
        teleportManager.registerEvents();

        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new SkillsMenuListener(teleportManager), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(teleportManager), this);

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

    @Override
    public void onDisable() {
        // Title dla wszystkich graczy
        for (Player player : getServer().getOnlinePlayers()) {
            player.sendTitle("§cServer is reloading", "", 10, 40, 10);
        }
        // Anuluj teleporty
        if (teleportManager != null) teleportManager.cancelAllTeleports();
        org.bukkit.event.HandlerList.unregisterAll((org.bukkit.plugin.Plugin) this);
    }

    // (opcjonalnie, jeśli chcesz mieć listener)
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == this) {
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendTitle("§cServer is reloading", "", 10, 40, 10);
            }
        }
    }
}