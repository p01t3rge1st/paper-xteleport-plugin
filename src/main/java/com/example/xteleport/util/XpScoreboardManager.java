package com.example.xteleport.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class XpScoreboardManager {
    private static final String OBJECTIVE_NAME = "rawxp";
    private static final Set<UUID> hidden = new HashSet<>();

    public static void showXp(Player player, int xp) {
        if (hidden.contains(player.getUniqueId())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            return;
        }
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, ChatColor.GOLD + "Raw XP");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        String formatted = xp >= 1000 ? String.format("%.1fk", xp / 1000.0) : String.valueOf(xp);
        Score score = obj.getScore(ChatColor.YELLOW + formatted + ChatColor.AQUA + " XP");
        score.setScore(2);

        // Ping (od 1.19+ można użyć getPing())
        int ping = 0;
        try {
            ping = player.getPing();
        } catch (NoSuchMethodError | Exception ignored) {}

        Score pingScore = obj.getScore(ChatColor.GREEN + "Ping: " + ChatColor.YELLOW + ping + " ms");
        pingScore.setScore(1);

        player.setScoreboard(board);
    }

    public static void hideXp(Player player) {
        hidden.add(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public static void unhideXp(Player player) {
        hidden.remove(player.getUniqueId());
    }

    public static boolean isHidden(Player player) {
        return hidden.contains(player.getUniqueId());
    }
}