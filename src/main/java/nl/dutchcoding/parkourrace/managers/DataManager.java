package nl.dutchcoding.parkourrace.managers;

import nl.dutchcoding.parkourrace.ParkourRace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {

    private final ParkourRace plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Map<String, Long>> playerBestTimes; // UUID -> CourseName -> Time

    public DataManager(ParkourRace plugin) {
        this.plugin = plugin;
        this.playerBestTimes = new HashMap<>();
    }

    public void loadData() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml!");
                e.printStackTrace();
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // Load player best times
        if (dataConfig.contains("player-times")) {
            for (String uuidString : dataConfig.getConfigurationSection("player-times").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                Map<String, Long> times = new HashMap<>();

                for (String courseName : dataConfig.getConfigurationSection("player-times." + uuidString).getKeys(false)) {
                    long time = dataConfig.getLong("player-times." + uuidString + "." + courseName);
                    times.put(courseName, time);
                }

                playerBestTimes.put(uuid, times);
            }
        }
    }

    public void saveData() {
        // Save player best times
        dataConfig.set("player-times", null);
        for (Map.Entry<UUID, Map<String, Long>> entry : playerBestTimes.entrySet()) {
            String uuidString = entry.getKey().toString();
            for (Map.Entry<String, Long> courseEntry : entry.getValue().entrySet()) {
                dataConfig.set("player-times." + uuidString + "." + courseEntry.getKey(), courseEntry.getValue());
            }
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml!");
            e.printStackTrace();
        }
    }

    public Long getPersonalBest(UUID playerId, String courseName) {
        return playerBestTimes.getOrDefault(playerId, new HashMap<>()).get(courseName);
    }

    public boolean setPersonalBest(UUID playerId, String courseName, long time) {
        Long currentBest = getPersonalBest(playerId, courseName);
        if (currentBest == null || time < currentBest) {
            playerBestTimes.computeIfAbsent(playerId, k -> new HashMap<>()).put(courseName, time);
            return true;
        }
        return false;
    }

    public Map<String, Long> getPlayerBestTimes(UUID playerId) {
        return new HashMap<>(playerBestTimes.getOrDefault(playerId, new HashMap<>()));
    }

    public List<Map.Entry<UUID, Long>> getTopTimes(String courseName, int limit) {
        List<Map.Entry<UUID, Long>> topTimes = new ArrayList<>();

        for (Map.Entry<UUID, Map<String, Long>> entry : playerBestTimes.entrySet()) {
            Long time = entry.getValue().get(courseName);
            if (time != null) {
                topTimes.add(new AbstractMap.SimpleEntry<>(entry.getKey(), time));
            }
        }

        topTimes.sort(Map.Entry.comparingByValue());
        return topTimes.subList(0, Math.min(limit, topTimes.size()));
    }
}
