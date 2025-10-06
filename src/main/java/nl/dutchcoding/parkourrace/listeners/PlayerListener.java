package nl.dutchcoding.parkourrace.listeners;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.models.ParkourSession;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final ParkourRace plugin;

    public PlayerListener(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // End any active sessions
        if (plugin.getSessionManager().hasSession(player)) {
            plugin.getSessionManager().endSession(player, false);
        }

        // End any setup sessions
        if (plugin.getSessionManager().hasSetupSession(player)) {
            plugin.getSessionManager().endSetupSession(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !plugin.getSessionManager().hasSession(player)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ParkourSession session = plugin.getSessionManager().getSession(player);

        // Check for retry item
        Material retryMaterial = Material.valueOf(plugin.getConfig().getString("items.retry.material"));
        if (item.getType() == retryMaterial) {
            event.setCancelled(true);
            player.teleport(session.getLastCheckpointLocation());
            player.playSound(player.getLocation(), plugin.getSound("sounds.retry"), 1.0f, 1.0f);

            if (session.getCurrentCheckpoint() == -1) {
                player.sendMessage(plugin.getConfigManager().getMessage("retry-no-checkpoint"));
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("retry-teleport"));
            }
        }

        // Check for leave item
        Material leaveMaterial = Material.valueOf(plugin.getConfig().getString("items.leave.material"));
        if (item.getType() == leaveMaterial) {
            event.setCancelled(true);
            String courseName = session.getCourse().getName();
            plugin.getSessionManager().endSession(player, false);
            player.sendMessage(plugin.getConfigManager().getMessage("left-course", "course", courseName));
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        // Prevent dropping items during parkour
        if (plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Prevent fall damage during parkour
        if (plugin.getSessionManager().hasSession(player) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // If player starts flying during parkour, cancel the session
        if (plugin.getSessionManager().hasSession(player) && event.isFlying()) {
            event.setCancelled(true);
            ParkourSession session = plugin.getSessionManager().getSession(player);
            String courseName = session.getCourse().getName();
            plugin.getSessionManager().endSession(player, false);
            player.sendMessage(plugin.getConfigManager().getMessage("left-course", "course", courseName));
        }
    }
}
