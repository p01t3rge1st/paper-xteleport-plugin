package com.example.xteleport.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class SkillsMenuManager {
    private static final String MENU_TITLE = "§bX-Link Skills";
    private static final String TELEPORT_TITLE = "§aTeleport to Player";
    private static final String SKILLS_TITLE = "§dSkills (Coming Soon)";
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

        // 1. slot: Ender Pearl (Teleport)
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta pearlMeta = pearl.getItemMeta();
        pearlMeta.setDisplayName("§aTeleport to Player");
        pearl.setItemMeta(pearlMeta);
        inv.setItem(0, pearl);

        // 2. slot: Book (Skills - Coming soon)
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName("§dSkills §7(Coming soon)");
        book.setItemMeta(bookMeta);
        inv.setItem(1, book);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.2f);
    }

    public static void openTeleportMenu(Player player) {
        List<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
        Inventory inv = Bukkit.createInventory(null, 9 * ((online.size() + 8) / 9), TELEPORT_TITLE);

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