package com.example.xteleport.commands;

import com.example.xteleport.util.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class XDeleteWarpCommand implements CommandExecutor {
    private final WarpManager warpManager;

    public XDeleteWarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /xdeletewarp <name>");
            return true;
        }
        String name = args[0];
        if (!warpManager.warpExists(name)) {
            sender.sendMessage(ChatColor.RED + "Warp '" + name + "' does not exist!");
            return true;
        }
        warpManager.deleteWarp(name);
        sender.sendMessage(ChatColor.GREEN + "Warp '" + name + "' has been deleted!");
        return true;
    }
}