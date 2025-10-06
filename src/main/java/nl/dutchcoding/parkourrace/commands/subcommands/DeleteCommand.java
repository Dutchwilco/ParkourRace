package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends SubCommand {

    private final ParkourRace plugin;

    public DeleteCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete a parkour course";
    }

    @Override
    public String getUsage() {
        return "/pk delete <name>";
    }

    @Override
    public String getPermission() {
        return "parkourrace.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("prefix") + " §cUsage: " + getUsage());
            return;
        }

        String courseName = args[0];

        if (!plugin.getCourseManager().courseExists(courseName)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("course-not-found", "course", courseName));
            return;
        }

        plugin.getCourseManager().deleteCourse(courseName);
        sender.sendMessage(plugin.getConfigManager().getMessage("course-deleted", "course", courseName));
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
