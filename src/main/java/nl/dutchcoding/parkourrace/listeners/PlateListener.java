package nl.dutchcoding.parkourrace.listeners;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.models.Course;
import nl.dutchcoding.parkourrace.models.ParkourSession;
import nl.dutchcoding.parkourrace.models.SetupSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlateListener implements Listener {

    private final ParkourRace plugin;

    public PlateListener(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlatePress(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Material material = event.getClickedBlock().getType();
        Location location = event.getClickedBlock().getLocation();

        // Check if player is in setup mode
        SetupSession setupSession = plugin.getSessionManager().getSetupSession(player);
        if (setupSession != null) {
            handleSetupMode(player, setupSession, material, location);
            return;
        }

        // Check if player is in a parkour session
        ParkourSession session = plugin.getSessionManager().getSession(player);
        if (session != null) {
            handleParkourMode(player, session, material, location);
            return;
        }

        // Check if this is a start plate
        if (material == Material.valueOf(plugin.getConfig().getString("plates.start.material"))) {
            String courseName = plugin.getCourseManager().getCourseByPlateLocation(location);
            if (courseName != null) {
                Course course = plugin.getCourseManager().getCourse(courseName);
                if (course != null && course.getStartLocation().equals(location)) {
                    // Start new session
                    plugin.getSessionManager().startSession(player, course);
                    plugin.getSessionManager().handleStart(player, plugin.getSessionManager().getSession(player));
                }
            }
        }
    }

    private void handleSetupMode(Player player, SetupSession session, Material material, Location location) {
        String startMaterial = plugin.getConfig().getString("plates.start.material");
        String checkpointMaterial = plugin.getConfig().getString("plates.checkpoint.material");
        String finishMaterial = plugin.getConfig().getString("plates.finish.material");

        if (material.name().equals(startMaterial)) {
            if (session.getStartLocation() == null) {
                session.setStartLocation(location);
                player.sendMessage(plugin.getConfigManager().getMessage("setup-start-added"));
            }
        } else if (material.name().equals(checkpointMaterial)) {
            if (session.getStartLocation() == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("setup-need-start"));
                return;
            }
            session.addCheckpoint(location);
            player.sendMessage(plugin.getConfigManager().getMessage("setup-checkpoint-added", 
                    "number", String.valueOf(session.getCheckpoints().size())));
        } else if (material.name().equals(finishMaterial)) {
            if (session.getStartLocation() == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("setup-need-start"));
                return;
            }
            session.setFinishLocation(location);
            player.sendMessage(plugin.getConfigManager().getMessage("setup-finish-added"));
        }
    }

    private void handleParkourMode(Player player, ParkourSession session, Material material, Location location) {
        Course course = session.getCourse();

        String startMaterial = plugin.getConfig().getString("plates.start.material");
        String checkpointMaterial = plugin.getConfig().getString("plates.checkpoint.material");
        String finishMaterial = plugin.getConfig().getString("plates.finish.material");

        // Check start plate (restart)
        if (material.name().equals(startMaterial) && course.getStartLocation().equals(location)) {
            plugin.getSessionManager().handleStart(player, session);
            return;
        }

        // Check checkpoints
        if (material.name().equals(checkpointMaterial)) {
            for (int i = 0; i < course.getCheckpoints().size(); i++) {
                if (course.getCheckpoints().get(i).equals(location)) {
                    if (i == session.getCurrentCheckpoint() + 1) {
                        plugin.getSessionManager().handleCheckpoint(player, session, i);
                    }
                    break;
                }
            }
            return;
        }

        // Check finish plate
        if (material.name().equals(finishMaterial) && course.getFinishLocation().equals(location)) {
            // Make sure all checkpoints were reached
            if (session.getCurrentCheckpoint() >= course.getCheckpoints().size() - 1 || course.getCheckpoints().isEmpty()) {
                plugin.getSessionManager().handleFinish(player, session);
            }
        }
    }
}
