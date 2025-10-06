package nl.dutchcoding.parkourrace;

import nl.dutchcoding.parkourrace.commands.ParkourCommand;
import nl.dutchcoding.parkourrace.listeners.PlateListener;
import nl.dutchcoding.parkourrace.listeners.PlayerListener;
import nl.dutchcoding.parkourrace.managers.ConfigManager;
import nl.dutchcoding.parkourrace.managers.CourseManager;
import nl.dutchcoding.parkourrace.managers.DataManager;
import nl.dutchcoding.parkourrace.managers.SessionManager;
import nl.dutchcoding.parkourrace.placeholders.ParkourPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourRace extends JavaPlugin {

    private ConfigManager configManager;
    private DataManager dataManager;
    private CourseManager courseManager;
    private SessionManager sessionManager;

    @Override
    public void onEnable() {
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.courseManager = new CourseManager(this);
        this.sessionManager = new SessionManager(this);

        // Load data
        configManager.loadConfigs();
        dataManager.loadData();
        courseManager.loadCourses();

        // Register commands
        getCommand("parkour").setExecutor(new ParkourCommand(this));

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new PlateListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register PlaceholderAPI expansion if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ParkourPlaceholders(this).register();
            getLogger().info("PlaceholderAPI hook enabled!");
        }

        getLogger().info("ParkourRace has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all data
        if (dataManager != null) {
            dataManager.saveData();
        }
        if (courseManager != null) {
            courseManager.saveCourses();
        }

        // Clear active sessions
        if (sessionManager != null) {
            sessionManager.clearAllSessions();
        }

        getLogger().info("ParkourRace has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public Sound getSound(String configKey) {
        String soundName = getConfig().getString(configKey);
        String key = "minecraft:" + soundName.toLowerCase().replace("_", ".");
        Sound sound = Registry.SOUNDS.get(NamespacedKey.fromString(key));
        if (sound == null) {
            getLogger().warning("Unknown sound: " + soundName + ", using default.");
            return Sound.UI_BUTTON_CLICK; // Fallback to a known sound
        }
        return sound;
    }
}
