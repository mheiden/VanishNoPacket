package org.kitteh.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.kitteh.vanish.VanishCheck;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.metrics.MetricsOverlord;

public class ListenPlayerJoin implements Listener {

    private final VanishPlugin plugin;

    public ListenPlayerJoin(VanishPlugin instance) {
        this.plugin = instance;
    }

    /**
     * Handle setting who the player can see and setting invisibility if needed
     * here.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEarly(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("vanished", new LazyMetadataValue(this.plugin, CacheStrategy.NEVER_CACHE, new VanishCheck(event.getPlayer().getName())));
        this.plugin.getManager().resetSeeing(event.getPlayer());
        if (VanishPerms.joinVanished(event.getPlayer())) {
            MetricsOverlord.getJoinInvisTracker().increment();
            this.plugin.getManager().toggleVanishQuiet(event.getPlayer(), false);
            this.plugin.hooksVanish(event.getPlayer());
        }
        this.plugin.hooksJoin(event.getPlayer());
    }

    /**
     * Handle any messages to the player here
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinLate(PlayerJoinEvent event) {
        final StringBuilder statusUpdate = new StringBuilder();
        if (VanishPerms.joinVanished(event.getPlayer())) {
            String message = ChatColor.DARK_AQUA + "You have joined vanished.";
            if (VanishPerms.canVanish(event.getPlayer())) {
                message += " To appear: /vanish";
            }
            event.getPlayer().sendMessage(message);
            statusUpdate.append("vanished");
        }
        if (VanishPerms.joinWithoutAnnounce(event.getPlayer())) {
            this.plugin.getManager().getAnnounceManipulator().addToDelayedAnnounce(event.getPlayer().getName());
            event.setJoinMessage(null);
            if (statusUpdate.length() != 0) {
                statusUpdate.append(" and ");
            }
            statusUpdate.append("silently");
        }
        if (statusUpdate.length() != 0) {
            this.plugin.messageStatusUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has joined " + statusUpdate.toString());
        }
        if (VanishPerms.canReceiveAdminAlerts(event.getPlayer()) && this.plugin.versionDifference()) {
            event.getPlayer().sendMessage(ChatColor.AQUA + "[Vanish] This is version " + ChatColor.DARK_AQUA + this.plugin.getCurrentVersion() + ChatColor.AQUA + ", latest is " + ChatColor.DARK_AQUA + this.plugin.getLatestKnownVersion());
            event.getPlayer().sendMessage(ChatColor.AQUA + "[Vanish] Check " + ChatColor.DARK_AQUA + "http://dev.bukkit.org/server-mods/vanish/");
        }
    }

}
