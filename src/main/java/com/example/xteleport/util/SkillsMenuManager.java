package com.example.xteleport.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class SkillsMenuManager {
    private static final String MENU_TITLE = "§bX-Link Menu";
    private static final Set<UUID> keybindDisabled = new HashSet<>();

    public static boolean isKeybindDisabled(Player player) {
        return keybindDisabled.contains(player.getUniqueId());
    }

    public static void setKeybindDisabled(Player player, boolean disabled) {
        if (disabled) keybindDisabled.add(player.getUniqueId());
        else keybindDisabled.remove(player.getUniqueId());
    }

    public static void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, MENU_TITLE);

        // 0: Scan
        ItemStack scan = new ItemStack(Material.SCULK);
        ItemMeta scanMeta = scan.getItemMeta();
        scanMeta.setDisplayName("§bScan §7(25 XP)");
        scanMeta.setLore(List.of(
            "§7Laser detects all mobs and sculks"
        ));
        scan.setItemMeta(scanMeta);
        inv.setItem(0, scan);

        // 1: Fullbright
        ItemStack fullbright = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta fullbrightMeta = fullbright.getItemMeta();
        fullbrightMeta.setDisplayName("§eFullbright §7(50 XP)");
        fullbrightMeta.setLore(List.of(
            "§7Gives you maximum brightness",
            "§7for a limited time"
        ));
        fullbright.setItemMeta(fullbrightMeta);
        inv.setItem(1, fullbright);

        // 2: Skulc Shock
        ItemStack witherSkull = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta witherMeta = witherSkull.getItemMeta();
        witherMeta.setDisplayName("§cSkulc Shock §7(200 XP)");
        witherMeta.setLore(List.of(
            "§7Disrupts all Deep Dark energy",
            "§7Affects wardens, sensors and shriekers"
        ));
        witherSkull.setItemMeta(witherMeta);
        inv.setItem(2, witherSkull);

        // 3: xhome
        ItemStack bed = new ItemStack(Material.RED_BED);
        ItemMeta bedMeta = bed.getItemMeta();
        bedMeta.setDisplayName("§6Teleport to Home");
        bed.setItemMeta(bedMeta);
        inv.setItem(3, bed);

        // 4: Teleport to Player
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta pearlMeta = pearl.getItemMeta();
        pearlMeta.setDisplayName("§aTeleport to Player");
        pearl.setItemMeta(pearlMeta);
        inv.setItem(4, pearl);

        // 5: Fireball
        ItemStack fireball = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta fireballMeta = fireball.getItemMeta();
        fireballMeta.setDisplayName("§6Fireball §7(100 XP)");
        fireballMeta.setLore(List.of(
            "§7Shoots a fireball like a ghast"
        ));
        fireball.setItemMeta(fireballMeta);
        inv.setItem(5, fireball);

        // 6: Decoy
        ItemStack decoy = new ItemStack(Material.TURTLE_EGG);
        ItemMeta decoyMeta = decoy.getItemMeta();
        decoyMeta.setDisplayName("§aDecoy §7(100 XP)");
        decoyMeta.setLore(List.of(
            "§7All mobs in range will focus you",
            "§7for a limited time"
        ));
        decoy.setItemMeta(decoyMeta);
        inv.setItem(6, decoy);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.2f);
    }

    public static void openTeleportMenu(Player player) {
        List<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
        Inventory inv = Bukkit.createInventory(null, 9 * ((online.size() + 8) / 9), "§aTeleport to Player");

        int slot = 0;
        for (Player target : online) {
            if (target.equals(player)) continue;
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName("§e" + target.getName());
            skull.setItemMeta(meta);
            inv.setItem(slot++, skull);
        }
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.5f);
    }
}