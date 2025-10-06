package nl.dutchcoding.parkourrace.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.models.Course;
import nl.dutchcoding.parkourrace.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParkourPlaceholders extends PlaceholderExpansion {

    private final ParkourRace plugin;

    public ParkourPlaceholders(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "parkourrace";
    }

    @Override
    public @NotNull String getAuthor() {
        return "wilcodwg";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        // %parkourrace_best_<course>% - Player's best time for a course
        if (params.startsWith("best_")) {
            String courseName = params.substring(5);
            if (player == null) return "N/A";

            Long bestTime = plugin.getDataManager().getPersonalBest(player.getUniqueId(), courseName);
            if (bestTime == null) {
                return "No Record";
            }
            return TimeFormatter.formatTime(bestTime);
        }

        // %parkourrace_top_<position>_<course>_player% - Top player name
        if (params.startsWith("top_") && params.endsWith("_player")) {
            String[] parts = params.split("_");
            if (parts.length >= 4) {
                try {
                    int position = Integer.parseInt(parts[1]);
                    String courseName = params.substring(params.indexOf(parts[2]), params.lastIndexOf("_player")).replace("_player", "");

                    List<Map.Entry<UUID, Long>> topTimes = plugin.getDataManager().getTopTimes(courseName, 10);
                    if (position > 0 && position <= topTimes.size()) {
                        UUID uuid = topTimes.get(position - 1).getKey();
                        Player topPlayer = Bukkit.getOfflinePlayer(uuid).getPlayer();
                        return topPlayer != null ? topPlayer.getName() : Bukkit.getOfflinePlayer(uuid).getName();
                    }
                    return "N/A";
                } catch (NumberFormatException e) {
                    return "Invalid";
                }
            }
        }

        // %parkourrace_top_<position>_<course>_time% - Top time
        if (params.startsWith("top_") && params.endsWith("_time")) {
            String[] parts = params.split("_");
            if (parts.length >= 4) {
                try {
                    int position = Integer.parseInt(parts[1]);
                    String courseName = params.substring(params.indexOf(parts[2]), params.lastIndexOf("_time")).replace("_time", "");

                    List<Map.Entry<UUID, Long>> topTimes = plugin.getDataManager().getTopTimes(courseName, 10);
                    if (position > 0 && position <= topTimes.size()) {
                        return TimeFormatter.formatTime(topTimes.get(position - 1).getValue());
                    }
                    return "N/A";
                } catch (NumberFormatException e) {
                    return "Invalid";
                }
            }
        }

        // %parkourrace_courses% - Total number of courses
        if (params.equals("courses")) {
            return String.valueOf(plugin.getCourseManager().getAllCourses().size());
        }

        // %parkourrace_completions% - Total completions by player
        if (params.equals("completions")) {
            if (player == null) return "0";
            return String.valueOf(plugin.getDataManager().getPlayerBestTimes(player.getUniqueId()).size());
        }

        // %parkourrace_in_course% - Whether player is in a course (true/false)
        if (params.equals("in_course")) {
            if (player == null) return "false";
            return String.valueOf(plugin.getSessionManager().hasSession(player));
        }

        // %parkourrace_current_course% - Current course name
        if (params.equals("current_course")) {
            if (player == null) return "None";
            if (!plugin.getSessionManager().hasSession(player)) return "None";
            return plugin.getSessionManager().getSession(player).getCourse().getName();
        }

        // %parkourrace_course_checkpoints_<course>% - Number of checkpoints in a course
        if (params.startsWith("course_checkpoints_")) {
            String courseName = params.substring(19);
            Course course = plugin.getCourseManager().getCourse(courseName);
            if (course == null) return "N/A";
            return String.valueOf(course.getCheckpointCount());
        }

        return null;
    }
}
