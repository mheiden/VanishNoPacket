package org.kitteh.vanish.hooks.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

public class DynmapHook extends Hook {

    private DynmapAPI dynmap;
    private boolean enabled = false;

    public DynmapHook(VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && this.plugin.getManager().isVanished(player)) {
                this.onUnvanish(player);
            }
        }
    }

    @Override
    public void onEnable() {
        this.enabled = true;
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("dynmap");
        if (grab != null) {
            this.dynmap = ((DynmapAPI) grab);
            this.plugin.getLogger().info("Now hooking into Dynmap");
        } else {
            this.plugin.getLogger().info("You wanted Dynmap support. I could not find Dynmap.");
            this.dynmap = null;
            this.enabled = false;
        }
    }

    @Override
    public void onJoin(Player player) {
        if (player.hasPermission("vanish.hooks.dynmap.alwayshidden")) {
            this.onVanish(player);
        }
    }

    @Override
    public void onUnvanish(Player player) {
        if (this.enabled && (this.dynmap != null) && !player.hasPermission("vanish.hooks.dynmap.alwayshidden")) {
            this.dynmap.setPlayerVisiblity(player, true);
        }
    }

    @Override
    public void onVanish(Player player) {
        if (this.enabled && (this.dynmap != null)) {
            this.dynmap.setPlayerVisiblity(player, false);
        }
    }

}
