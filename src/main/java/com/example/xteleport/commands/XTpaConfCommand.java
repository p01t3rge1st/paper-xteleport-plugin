package com.example.xteleport.commands;

import com.example.xteleport.util.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class XTpaConfCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Użycie: /xtpaconf <constant|by_distance> <xp_per_block>");
            return true;
        }
        String mode = args[0];
        try {
            float value = Float.parseFloat(args[1]);
            TeleportManager.setXpCostMode(mode, value);
            sender.sendMessage(ChatColor.GREEN + "Ustawiono koszt XP: " + mode + " (" + value + ")");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Podaj poprawną liczbę (może być zmiennoprzecinkowa).");
        }
        return true;
    }
}