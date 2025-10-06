package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import nl.dutchcoding.parkourrace.utils.TimeFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BestCommand extends SubCommand {

    private final ParkourRace plugin;

    public BestCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "best";
    }

    @Override
    public String getDescription() {
        return "View your personal best times";
    }

    @Override
    public String getUsage() {
        return "/pk best [name]";
    }

    @Override
    public String getPermission() {
        return "parkourrace.use";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            // Show all personal bests
            Map<String, Long> bestTimes = plugin.getDataManager().getPlayerBestTimes(player.getUniqueId());

            if (bestTimes.isEmpty()) {
                player.sendMessage(plugin.getConfigManager().getMessage("best-none"));
                return;
            }

            player.sendMessage(plugin.getConfigManager().getMessage("best-header"));
            for (Map.Entry<String, Long> entry : bestTimes.entrySet()) {
                player.sendMessage(plugin.getConfigManager().getMessage("best-entry", 
                        "course", entry.getKey(), 
                        "time", TimeFormatter.formatTime(entry.getValue())));
            }
        } else {
            // Show specific course best
            String courseName = args[0];
            Long bestTime = plugin.getDataManager().getPersonalBest(player.getUniqueId(), courseName);

            if (bestTime == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("best-no-record", "course", courseName));
                return;
            }

            player.sendMessage(plugin.getConfigManager().getMessage("best-single", 
                    "course", courseName, 
                    "time", TimeFormatter.formatTime(bestTime)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getCourseManager().getAllCourses().stream()
                    .map(course -> course.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());
        }
        return List.of();
    }
}
