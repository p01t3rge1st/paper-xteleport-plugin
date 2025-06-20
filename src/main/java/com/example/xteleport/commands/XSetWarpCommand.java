package com.example.xteleport.commands;

import com.example.xteleport.util.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class XSetWarpCommand implements CommandExecutor {
    private final WarpManager warpManager;

    public XSetWarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("xlink.setwarp")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set warps!");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /xsetwarp <name>");
            return true;
        }
        String name = args[0];
        Location loc = player.getLocation();
        warpManager.setWarp(name, loc);
        player.sendMessage(ChatColor.GREEN + "Warp '" + name + "' set at your current location!");
        return true;
    }
}