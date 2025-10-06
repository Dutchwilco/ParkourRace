package nl.dutchcoding.parkourrace.managers;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.models.Course;
import nl.dutchcoding.parkourrace.models.ParkourSession;
import nl.dutchcoding.parkourrace.models.SetupSession;
import nl.dutchcoding.parkourrace.utils.ItemBuilder;
import nl.dutchcoding.parkourrace.utils.TimeFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.*;

public class SessionManager {

    private final ParkourRace plugin;
    private final Map<UUID, ParkourSession> activeSessions;
    private final Map<UUID, SetupSession> setupSessions;
    private final Map<UUID, Integer> timerTasks;

    public SessionManager(ParkourRace plugin) {
        this.plugin = plugin;
        this.activeSessions = new HashMap<>();
        this.setupSessions = new HashMap<>();
        this.timerTasks = new HashMap<>();
    }

    public void startSession(Player player, Course course) {
        ParkourSession session = new ParkourSession(player.getUniqueId(), course);
        activeSessions.put(player.getUniqueId(), session);

        // Save inventory
        saveInventory(player, session);

        // Give items
        giveSessionItems(player);

        // Disable flight
        if (player.isFlying()) {
            player.setFlying(false);
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
        }

        // Start timer task
        startTimerTask(player);
    }

    public void endSession(Player player, boolean save) {
        ParkourSession session = activeSessions.remove(player.getUniqueId());
        if (session != null) {
            // Stop timer task
            stopTimerTask(player);

            // Clear action bar
            player.sendActionBar(Component.text(""));

            // Restore inventory
            restoreInventory(player, session);
        }
    }

    public ParkourSession getSession(Player player) {
        return activeSessions.get(player.getUniqueId());
    }

    public boolean hasSession(Player player) {
        return activeSessions.containsKey(player.getUniqueId());
    }

    public void startSetupSession(Player player, String courseName) {
        SetupSession session = new SetupSession(player.getUniqueId(), courseName);
        setupSessions.put(player.getUniqueId(), session);
    }

    public void endSetupSession(Player player) {
        setupSessions.remove(player.getUniqueId());
    }

    public SetupSession getSetupSession(Player player) {
        return setupSessions.get(player.getUniqueId());
    }

    public boolean hasSetupSession(Player player) {
        return setupSessions.containsKey(player.getUniqueId());
    }

    public void handleStart(Player player, ParkourSession session) {
        // Play effects
        player.playSound(player.getLocation(), plugin.getSound("sounds.start"), 1.0f, 1.0f);
        player.spawnParticle(Particle.valueOf(plugin.getConfig().getString("plates.start.particle")), player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);

        // Show title
        showTitle(player, "titles.start", "", "");

        // Send message
        player.sendMessage(plugin.getConfigManager().getMessage("timer-started", "course", session.getCourse().getName()));
    }

    public void handleCheckpoint(Player player, ParkourSession session, int checkpointIndex) {
        session.reachCheckpoint(checkpointIndex, session.getCourse().getCheckpoints().get(checkpointIndex));

        // Play effects
        player.playSound(player.getLocation(), plugin.getSound("sounds.checkpoint"), 1.0f, 1.0f);
        player.spawnParticle(Particle.valueOf(plugin.getConfig().getString("plates.checkpoint.particle")), player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);

        // Show title
        showTitle(player, "titles.checkpoint", "", "");

        // Send message
        player.sendMessage(plugin.getConfigManager().getMessage("checkpoint-reached", "number", String.valueOf(checkpointIndex + 1)));
    }

    public void handleFinish(Player player, ParkourSession session) {
        session.setFinished(true);
        long completionTime = session.getElapsedTime();
        String formattedTime = TimeFormatter.formatTime(completionTime);

        // Check for personal best
        Long previousBest = plugin.getDataManager().getPersonalBest(player.getUniqueId(), session.getCourse().getName());
        boolean isNewBest = plugin.getDataManager().setPersonalBest(player.getUniqueId(), session.getCourse().getName(), completionTime);

        // Play effects
        Sound finishSound = isNewBest ? plugin.getSound("sounds.personal-best") : plugin.getSound("sounds.finish");
        player.playSound(player.getLocation(), finishSound, 1.0f, 1.0f);
        player.spawnParticle(Particle.valueOf(plugin.getConfig().getString("plates.finish.particle")), player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);

        // Show titles
        if (isNewBest) {
            if (previousBest == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("first-completion"));
            } else {
                showTitle(player, "titles.personal-best", "old", TimeFormatter.formatTime(previousBest), "new", formattedTime);
                player.sendMessage(plugin.getConfigManager().getMessage("new-personal-best", "old", TimeFormatter.formatTime(previousBest)));
            }
        } else {
            showTitle(player, "titles.finish", "time", formattedTime);
        }

        // Send message
        player.sendMessage(plugin.getConfigManager().getMessage("course-completed", "time", formattedTime));

        // End session
        endSession(player, true);

        // Save data
        plugin.getDataManager().saveData();
    }

    public void clearAllSessions() {
        // End all active parkour sessions
        new HashSet<>(activeSessions.keySet()).forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                endSession(player, false);
            }
        });

        // Clear all setup sessions
        setupSessions.clear();

        // Cancel all timer tasks
        timerTasks.values().forEach(Bukkit.getScheduler()::cancelTask);
        timerTasks.clear();
    }

    private void startTimerTask(Player player) {
        int interval = plugin.getConfig().getInt("timer.update-interval", 10);

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                ParkourSession session = getSession(player);
                if (session == null || session.isFinished()) {
                    cancel();
                    timerTasks.remove(player.getUniqueId());
                    return;
                }

                String formattedTime = TimeFormatter.formatTime(session.getElapsedTime());
                String format = plugin.getConfigManager().colorize(plugin.getConfig().getString("timer.format"));
                String actionBar = format.replace("{time}", formattedTime);

                player.sendActionBar(Component.text(actionBar));
            }
        }.runTaskTimer(plugin, 0L, interval).getTaskId();

        timerTasks.put(player.getUniqueId(), taskId);
    }

    private void stopTimerTask(Player player) {
        Integer taskId = timerTasks.remove(player.getUniqueId());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private void giveSessionItems(Player player) {
        player.getInventory().clear();

        ConfigManager config = plugin.getConfigManager();

        // Retry item
        if (plugin.getConfig().getBoolean("items.retry.enabled")) {
            ItemStack retryItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("items.retry.material")))
                    .name(config.colorize(plugin.getConfig().getString("items.retry.name")))
                    .lore(config.getStringList("items.retry.lore"))
                    .build();
            player.getInventory().setItem(plugin.getConfig().getInt("items.retry.slot"), retryItem);
        }

        // Leave item
        if (plugin.getConfig().getBoolean("items.leave.enabled")) {
            ItemStack leaveItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("items.leave.material")))
                    .name(config.colorize(plugin.getConfig().getString("items.leave.name")))
                    .lore(config.getStringList("items.leave.lore"))
                    .build();
            player.getInventory().setItem(plugin.getConfig().getInt("items.leave.slot"), leaveItem);
        }
    }

    private void saveInventory(Player player, ParkourSession session) {
        session.setSavedInventory(player.getInventory().getContents().clone());
        session.setSavedArmor(player.getInventory().getArmorContents().clone());
    }

    private void restoreInventory(Player player, ParkourSession session) {
        player.getInventory().clear();
        if (session.getSavedInventory() != null) {
            player.getInventory().setContents(session.getSavedInventory());
        }
        if (session.getSavedArmor() != null) {
            player.getInventory().setArmorContents(session.getSavedArmor());
        }
    }

    private void showTitle(Player player, String path, String... replacements) {
        // Check if titles are globally enabled
        if (!plugin.getConfig().getBoolean("titles.enabled", true)) {
            return;
        }

        // Check if this specific title is enabled
        if (!plugin.getConfig().getBoolean(path + ".enabled", true)) {
            return;
        }

        String titleText = plugin.getConfig().getString(path + ".title", "");
        String subtitleText = plugin.getConfig().getString(path + ".subtitle", "");

        // Replace placeholders
        for (int i = 0; i < replacements.length - 1; i += 2) {
            titleText = titleText.replace("{" + replacements[i] + "}", replacements[i + 1]);
            subtitleText = subtitleText.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }

        int fadeIn = plugin.getConfig().getInt(path + ".fade-in", 10);
        int stay = plugin.getConfig().getInt(path + ".stay", 30);
        int fadeOut = plugin.getConfig().getInt(path + ".fade-out", 10);

        Title title = Title.title(
                Component.text(plugin.getConfigManager().colorize(titleText)),
                Component.text(plugin.getConfigManager().colorize(subtitleText)),
                Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L))
        );

        player.showTitle(title);
    }
}
