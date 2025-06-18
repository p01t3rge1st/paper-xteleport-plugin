package com.example.xteleport.commands;

import com.example.xteleport.util.TeleportManager;
import com.example.xteleport.util.XpScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XdCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (XpScoreboardManager.isHidden(player)) {
            XpScoreboardManager.unhideXp(player);
            player.sendMessage(ChatColor.GREEN + "Raw XP HUD enabled.");
        } else {
            XpScoreboardManager.hideXp(player);
            player.sendMessage(ChatColor.RED + "Raw XP HUD disabled.");
        }
        return true;
    }
}