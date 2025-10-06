package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import nl.dutchcoding.parkourrace.models.Course;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JoinCommand extends SubCommand {

    private final ParkourRace plugin;

    public JoinCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Join a parkour course";
    }

    @Override
    public String getUsage() {
        return "/pk join <name>";
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

        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + " §cUsage: " + getUsage());
            return;
        }

        String courseName = args[0];
        Course course = plugin.getCourseManager().getCourse(courseName);

        if (course == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("course-not-found", "course", courseName));
            return;
        }

        if (plugin.getSessionManager().hasSession(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("already-in-course"));
            return;
        }

        // Teleport to start
        player.teleport(course.getStartLocation());

        // Start session
        plugin.getSessionManager().startSession(player, course);

        player.sendMessage(plugin.getConfigManager().getMessage("joined-course", "course", courseName));
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
