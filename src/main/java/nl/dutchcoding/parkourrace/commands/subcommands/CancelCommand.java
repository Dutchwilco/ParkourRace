package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CancelCommand extends SubCommand {

    private final ParkourRace plugin;

    public CancelCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public String getDescription() {
        return "Cancel the current course setup";
    }

    @Override
    public String getUsage() {
        return "/pk cancel";
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

        if (!plugin.getSessionManager().hasSetupSession(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + " §cYou are not in setup mode!");
            return;
        }

        plugin.getSessionManager().endSetupSession(player);
        player.sendMessage(plugin.getConfigManager().getMessage("setup-cancelled"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
