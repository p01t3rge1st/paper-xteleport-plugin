package com.example.xteleport.commands;

import com.example.xteleport.util.ConfigManager;
import com.example.xteleport.util.TeleportManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class XLinkReloadCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public XLinkReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("xlink.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the plugin.");
            return true;
        }
        plugin.reloadConfig();
        ConfigManager.init(plugin);
        TeleportManager.loadXpCostFromConfig();
        sender.sendMessage(ChatColor.GREEN + "XLink config reloaded!");
        return true;
    }
}