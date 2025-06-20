package com.example.xteleport.listeners;

import com.example.xteleport.util.SkillsMenuManager;
import com.example.xteleport.util.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkillsMenuListener implements Listener {
    private final TeleportManager teleportManager;

    public SkillsMenuListener(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() && !SkillsMenuManager.isKeybindDisabled(player)) {
            event.setCancelled(true);
            SkillsMenuManager.openMainMenu(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();

        if (title.equals("§bX-Link Skills")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            ItemMeta meta = clicked.getItemMeta();

            if (meta.getDisplayName().contains("Teleport to Player")) {
                player.closeInventory();
                SkillsMenuManager.openTeleportMenu(player);
                return;
            }
            if (meta.getDisplayName().contains("Teleport to Home")) {
                player.closeInventory();
                player.performCommand("xhome");
                return;
            }
            if (meta.getDisplayName().contains("Skills")) {
                player.closeInventory();
                SkillsMenuManager.openSkillsMenu(player);
                return;
            }
        } else if (title.equals("§aTeleport to Player")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            if (clicked.getType() == Material.PLAYER_HEAD) {
                String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                player.closeInventory();
                player.performCommand("xtpa " + name);
            }
        } else if (title.equals("§dSkills (Coming Soon)") || title.equals("§dSkills §7(Kliknij)")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            player.closeInventory();
            if (type == Material.WITHER_SKELETON_SKULL) {
                player.performCommand("xskill skulcshock");
            } else if (type == Material.SCULK) {
                player.performCommand("xskill scan");
            } else if (type == Material.FIRE_CHARGE) {
                player.performCommand("xskill fireball");
            } else if (type == Material.GLOWSTONE_DUST) {
                player.performCommand("xskill fullbright");
            }
        }
    }
}