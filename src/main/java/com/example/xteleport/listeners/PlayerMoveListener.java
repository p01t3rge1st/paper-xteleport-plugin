package com.example.xteleport.listeners;

import com.example.xteleport.util.TeleportManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final TeleportManager teleportManager;

    public PlayerMoveListener(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!teleportManager.isTeleporting(event.getPlayer())) return;
        // Anuluj TYLKO jeśli zmieniła się pozycja (X/Y/Z), a nie tylko obrót głowy
        if (event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getY() != event.getTo().getY()
                || event.getFrom().getZ() != event.getTo().getZ()) {
            teleportManager.cancelTeleport(event.getPlayer());
        }
    }
}