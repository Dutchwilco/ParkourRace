package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import nl.dutchcoding.parkourrace.models.SetupSession;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DoneCommand extends SubCommand {

    private final ParkourRace plugin;

    public DoneCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "done";
    }

    @Override
    public String getDescription() {
        return "Finish and save the current course setup";
    }

    @Override
    public String getUsage() {
        return "/pk done";
    }

    @Override
    public String getPermission() {
        return "parkourrace.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        SetupSession session = plugin.getSessionManager().getSetupSession(player);
        if (session == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + " §cYou are not in setup mode!");
            return;
        }

        if (!session.isValid()) {
            player.sendMessage(plugin.getConfigManager().getMessage("setup-need-finish"));
            return;
        }

        // Save the course
        plugin.getCourseManager().addCourse(session.toCourse());
        plugin.getSessionManager().endSetupSession(player);

        player.sendMessage(plugin.getConfigManager().getMessage("setup-saved", 
                "course", session.getCourseName(), 
                "checkpoints", String.valueOf(session.getCheckpoints().size())));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
