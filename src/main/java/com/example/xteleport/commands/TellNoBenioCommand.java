package com.example.xteleport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TellNoBenioCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /tellnobenio <player> <message>");
            return true;
        }
        String excludedName = args[0];
        Player excluded = Bukkit.getPlayerExact(excludedName);
        if (excluded == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + excludedName + "' is not online.");
            return true;
        }
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String senderName = (sender instanceof Player p) ? p.getName() : "Console";
        String formatted = ChatColor.LIGHT_PURPLE + "[TellNoBenio] " + ChatColor.YELLOW + senderName + ": " + ChatColor.WHITE + message;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(excluded)) {
                player.sendMessage(formatted);
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Message sent to everyone except " + excludedName + ".");
        return true;
    }
}