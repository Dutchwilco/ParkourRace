package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import nl.dutchcoding.parkourrace.models.Course;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListCommand extends SubCommand {

    private final ParkourRace plugin;

    public ListCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List all parkour courses";
    }

    @Override
    public String getUsage() {
        return "/pk list";
    }

    @Override
    public String getPermission() {
        return "parkourrace.use";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (plugin.getCourseManager().getAllCourses().isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getMessage("course-list-empty"));
            return;
        }

        sender.sendMessage(plugin.getConfigManager().getMessage("course-list-header"));
        for (Course course : plugin.getCourseManager().getAllCourses()) {
            sender.sendMessage(plugin.getConfigManager().getMessage("course-list-entry", 
                    "course", course.getName(), 
                    "checkpoints", String.valueOf(course.getCheckpointCount())));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
