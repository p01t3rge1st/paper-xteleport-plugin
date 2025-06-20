package com.example.xteleport.commands;

import com.example.xteleport.util.ConfigManager;
import com.example.xteleport.util.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class XBackCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public XBackCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        Location deathLoc = TeleportManager.getLastDeathLocation(player);
        if (deathLoc == null || deathLoc.getWorld() == null) {
            player.sendMessage(ChatColor.RED + "No valid death location found!");
            return true;
        }
        int cost = ConfigManager.getXBackXpCost();
        int xp = TeleportManager.getTotalExperience(player);
        if (xp < cost) {
            player.sendMessage(ChatColor.RED + "You need at least " + cost + " XP to use /xback!");
            return true;
        }
        TeleportManager.takeRawXp(player, cost);
        player.teleport(deathLoc);
        player.sendMessage(ChatColor.GREEN + "Teleported to your last death location! (-" + cost + " XP)");
        return true;
    }
}