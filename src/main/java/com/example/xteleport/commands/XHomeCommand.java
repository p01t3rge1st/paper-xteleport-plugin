package com.example.xteleport.commands;

import com.example.xteleport.util.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class XHomeCommand implements CommandExecutor {
    private final TeleportManager teleportManager;

    public XHomeCommand(JavaPlugin plugin) {
        this.teleportManager = new TeleportManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Tylko gracze mogą używać tej komendy.");
            return true;
        }
        Player player = (Player) sender;
        Location home = player.getBedSpawnLocation();
        if (home == null) {
            player.sendMessage(ChatColor.RED + "Nie masz ustawionego punktu home (łóżka)!");
            return true;
        }
        teleportManager.teleportPlayerToLocation(player, home);
        return true;
    }
}