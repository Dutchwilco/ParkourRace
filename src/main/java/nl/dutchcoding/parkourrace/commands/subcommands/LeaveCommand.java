package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand extends SubCommand {

    private final ParkourRace plugin;

    public LeaveCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave your current parkour course";
    }

    @Override
    public String getUsage() {
        return "/pk leave";
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

        if (!plugin.getSessionManager().hasSession(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("not-in-course"));
            return;
        }

        plugin.getSessionManager().endSession(player, false);
        player.sendMessage(plugin.getConfigManager().getMessage("left-course"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
