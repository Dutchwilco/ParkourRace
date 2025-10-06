package nl.dutchcoding.parkourrace.managers;

import nl.dutchcoding.parkourrace.ParkourRace;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final ParkourRace plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private final Map<String, String> messageCache;

    public ConfigManager(ParkourRace plugin) {
        this.plugin = plugin;
        this.messageCache = new HashMap<>();
    }

    public void loadConfigs() {
        // Save default config files
        plugin.saveDefaultConfig();
        saveResource("messages.yml");

        // Load configurations
        config = plugin.getConfig();
        messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        // Cache messages
        cacheMessages();
    }

    private void saveResource(String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
        }
    }

    private void cacheMessages() {
        messageCache.clear();
        for (String key : messages.getKeys(true)) {
            if (messages.isString(key)) {
                messageCache.put(key, colorize(messages.getString(key)));
            }
        }
    }

    public String getMessage(String path) {
        return messageCache.getOrDefault(path, "&cMessage not found: " + path);
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        
        // Replace prefix first
        String prefix = messageCache.getOrDefault("prefix", "&8[&eParkourRace&8]&r");
        message = message.replace("{prefix}", prefix);
        
        // Replace other placeholders
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return message;
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path).stream()
                .map(this::colorize)
                .toList();
    }

    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
        cacheMessages();
    }
}
