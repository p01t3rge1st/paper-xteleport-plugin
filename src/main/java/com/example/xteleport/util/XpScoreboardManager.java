package com.example.xteleport.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class XpScoreboardManager {
    private static final Set<UUID> hidden = new HashSet<>();
    private static final String OBJECTIVE_NAME = "xlinkxp";
    private static final String XP_LABEL = "§aXP: ";

    public static boolean isHidden(Player player) {
        return hidden.contains(player.getUniqueId());
    }

    public static void setHidden(Player player, boolean hide) {
        if (hide) hidden.add(player.getUniqueId());
        else hidden.remove(player.getUniqueId());
    }

    public static void showXp(Player player, int xp) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj = scoreboard.getObjective(OBJECTIVE_NAME);

        if (obj == null) {
            obj = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy", XP_LABEL);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // Format XP: 1.2k jeśli powyżej 1000
        String xpStr = xp >= 1000 ? String.format("%.1fk", xp / 1000.0) : String.valueOf(xp);

        // Usuń stare wpisy
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        // Dodaj XP na górze
        obj.getScore("§aXP: " + xpStr).setScore(2);

        // Dodaj Ping na dole
        int ping = getPing(player);
        obj.getScore("§bPing: " + ping + " ms").setScore(1);

        // Ustaw scoreboard tylko raz, jeśli nie jest już ustawiony
        if (player.getScoreboard() != scoreboard) {
            player.setScoreboard(scoreboard);
        }
    }

    // Pobierz ping gracza (Paper API)
    private static int getPing(Player player) {
        try {
            return player.getPing();
        } catch (NoSuchMethodError e) {
            return -1;
        }
    }

    public static void hideXp(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public static void unhideXp(Player player) {
        hidden.remove(player.getUniqueId());
    }
}