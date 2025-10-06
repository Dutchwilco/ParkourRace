package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommand extends SubCommand {

    private final ParkourRace plugin;

    public CreateCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Start creating a new parkour course";
    }

    @Override
    public String getUsage() {
        return "/pk create <name>";
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

        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + " §cUsage: " + getUsage());
            return;
        }

        String courseName = args[0];

        if (plugin.getCourseManager().courseExists(courseName)) {
            player.sendMessage(plugin.getConfigManager().getMessage("setup-already-exists"));
            return;
        }

        if (plugin.getSessionManager().hasSetupSession(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + " §cYou are already in setup mode!");
            return;
        }

        plugin.getSessionManager().startSetupSession(player, courseName);
        player.sendMessage(plugin.getConfigManager().getMessage("setup-started", "course", courseName));
        player.sendMessage(plugin.getConfigManager().getMessage("setup-info"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of("<name>");
    }
}
