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
            if (meta.getDisplayName().contains("Teleport")) {
                SkillsMenuManager.openTeleportMenu(player);
            }
            // Book - coming soon, nic nie rób
        } else if (title.equals("§aTeleport to Player")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            if (clicked.getType() == Material.PLAYER_HEAD) {
                String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                Player target = Bukkit.getPlayerExact(name);
                if (target != null && target.isOnline()) {
                    player.closeInventory();
                    teleportManager.teleportPlayerToPlayer(player, target);
                } else {
                    player.sendMessage(ChatColor.RED + "Player is not online.");
                }
            }
        }
    }
}