package com.example.xteleport.commands;

import com.example.xteleport.util.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class WarpTabCompleter implements TabCompleter {
    private final WarpManager warpManager;

    public WarpTabCompleter(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (String warp : warpManager.getAllWarpNames()) {
                if (warp.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(warp);
                }
            }
        }
        return completions;
    }
}