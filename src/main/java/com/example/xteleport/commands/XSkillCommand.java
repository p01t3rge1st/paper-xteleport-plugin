package com.example.xteleport.commands;

import com.example.xteleport.util.SkillEffects;
import com.example.xteleport.util.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XSkillCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /xskill <skulcshock|scan|fireball>");
            return true;
        }
        String skill = args[0].toLowerCase();
        switch (skill) {
            case "skulcshock" -> SkillEffects.skulcShock(player);
            case "scan" -> SkillEffects.scan(player);
            case "fireball" -> SkillEffects.fireball(player);
            default -> player.sendMessage(ChatColor.RED + "Unknown skill!");
        }
        return true;
    }
}