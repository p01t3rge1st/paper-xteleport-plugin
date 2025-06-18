package com.example.xteleport.commands;

import com.example.xteleport.util.SkillsMenuManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XMenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("off")) {
            SkillsMenuManager.setKeybindDisabled(player, true);
            player.sendMessage(ChatColor.RED + "Keybind for menu disabled.");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("on")) {
            SkillsMenuManager.setKeybindDisabled(player, false);
            player.sendMessage(ChatColor.GREEN + "Keybind for menu enabled.");
            return true;
        }
        // Otwórz menu
        SkillsMenuManager.openMainMenu(player);
        return true;
    }
}