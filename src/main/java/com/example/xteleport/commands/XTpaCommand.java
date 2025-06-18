package com.example.xteleport.commands;

import com.example.xteleport.util.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class XTpaCommand implements CommandExecutor {

    private final TeleportManager teleportManager;

    public XTpaCommand(JavaPlugin plugin) {
        this.teleportManager = new TeleportManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // /xtpa -help
        if (args.length == 1 && args[0].equalsIgnoreCase("-help")) {
            sender.sendMessage(ChatColor.GOLD + "---- XTeleport Help ----");
            sender.sendMessage(ChatColor.YELLOW + "/xhome" + ChatColor.WHITE + " - Teleport to your bed/home.");
            sender.sendMessage(ChatColor.YELLOW + "/xtpa <player>" + ChatColor.WHITE + " - Teleport to another player.");
            sender.sendMessage(ChatColor.YELLOW + "/xtpaconf <constant|by_distance> <xp_per_block>" + ChatColor.WHITE + " - Set XP cost for teleportation.");
            sender.sendMessage(ChatColor.YELLOW + "/xtpa xpcost <constant|by_distance> <xp_per_block>" + ChatColor.WHITE + " - Alternative way to set XP cost.");
            sender.sendMessage(ChatColor.YELLOW + "/xsetwarp <name>" + ChatColor.WHITE + " - Set a global warp at your location.");
            sender.sendMessage(ChatColor.YELLOW + "/xwarp <name>" + ChatColor.WHITE + " - Teleport to a global warp.");
            sender.sendMessage(ChatColor.YELLOW + "/xdeletewarp <name>" + ChatColor.WHITE + " - Delete a global warp.");
            sender.sendMessage(ChatColor.YELLOW + "/xback" + ChatColor.WHITE + " - Teleport to your last death location.");
            sender.sendMessage(ChatColor.YELLOW + "/xd" + ChatColor.WHITE + " - Toggle Raw XP HUD.");
            sender.sendMessage(ChatColor.GRAY + "Teleportation costs raw XP points. You must stand still for 5 seconds.");
            sender.sendMessage(ChatColor.GRAY + "Teleportation is cancelled if you move.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /xtpa <player>");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("xpcost")) {
            if (args.length == 3) {
                String mode = args[1];
                try {
                    float value = Float.parseFloat(args[2]);
                    TeleportManager.setXpCostMode(mode, value);
                    sender.sendMessage(ChatColor.GREEN + "XP cost set: " + mode + " (" + value + ")");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Please enter a valid number.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /xtpa xpcost <constant|by_distance> <xp_per_block>");
            }
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player is not online.");
            return true;
        }

        if (target.isDead()) {
            player.sendMessage(ChatColor.RED + "You cannot teleport to a dead player.");
            return true;
        }

        if (!target.getWorld().equals(player.getWorld())) {
            player.sendMessage(ChatColor.RED + "Player is in another dimension.");
            return true;
        }

        // Calculate cost and distance
        double distance = player.getLocation().distance(target.getLocation());
        int cost = TeleportManager.getXpCost(player.getLocation(), target.getLocation());
        int playerXp = TeleportManager.getTotalExperience(player);

        if (playerXp < cost) {
            player.sendMessage(ChatColor.RED + "You don't have enough experience points to teleport!");
            player.sendMessage(ChatColor.YELLOW + "Teleportation would cost: " + cost + " XP (" +
                    String.format("%.1f", distance) + " blocks).");
            return true;
        }

        teleportManager.teleportPlayerToPlayer(player, target);
        return true;
    }
}