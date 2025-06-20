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

        if (title.equals("§bX-Link Menu")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            player.closeInventory();
            switch (type) {
                case SCULK -> player.performCommand("xskill scan");
                case GLOWSTONE_DUST -> player.performCommand("xskill fullbright");
                case WITHER_SKELETON_SKULL -> player.performCommand("xskill skulcshock");
                case RED_BED -> player.performCommand("xhome");
                case ENDER_PEARL -> SkillsMenuManager.openTeleportMenu(player);
                case FIRE_CHARGE -> player.performCommand("xskill fireball");
                case TURTLE_EGG -> player.performCommand("xskill decoy");
            }
        } else if (title.equals("§aTeleport to Player")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            if (clicked.getType() == Material.PLAYER_HEAD) {
                String name = org.bukkit.ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                player.closeInventory();
                player.performCommand("xtpa " + name);
            }
        }
    }
}