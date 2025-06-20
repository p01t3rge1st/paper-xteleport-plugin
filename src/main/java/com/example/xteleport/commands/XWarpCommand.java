package com.example.xteleport.commands;

import com.example.xteleport.util.WarpManager;
import com.example.xteleport.util.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XWarpCommand implements CommandExecutor {
    private final WarpManager warpManager;
    private final TeleportManager teleportManager;

    public XWarpCommand(WarpManager warpManager, TeleportManager teleportManager) {
        this.warpManager = warpManager;
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /xwarp <name>");
            return true;
        }
        String name = args[0];
        if (!warpManager.warpExists(name)) {
            player.sendMessage(ChatColor.RED + "Warp '" + name + "' does not exist!");
            return true;
        }
        Location loc = warpManager.getWarp(name);
        if (loc == null) {
            player.sendMessage(ChatColor.RED + "Warp location is invalid or missing!");
            return true;
        }
        World playerWorld = player.getWorld();
        World warpWorld = loc.getWorld();
        if (warpWorld == null) {
            player.sendMessage(ChatColor.RED + "Warp world does not exist!");
            return true;
        }
        // Blokada teleportacji do innego wymiaru
        if (!playerWorld.getUID().equals(warpWorld.getUID())) {
            player.sendMessage(ChatColor.RED + "You cannot warp to another dimension!");
            return true;
        }
        teleportManager.teleportWithDelay(player, loc);
        return true;
    }
}