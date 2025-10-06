package nl.dutchcoding.parkourrace.commands.subcommands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.SubCommand;
import nl.dutchcoding.parkourrace.models.ParkourSession;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RetryCommand extends SubCommand {

    private final ParkourRace plugin;

    public RetryCommand(ParkourRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "retry";
    }

    @Override
    public String getDescription() {
        return "Teleport to your last checkpoint";
    }

    @Override
    public String getUsage() {
        return "/pk retry";
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

        ParkourSession session = plugin.getSessionManager().getSession(player);
        if (session == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("not-in-course"));
            return;
        }

        player.teleport(session.getLastCheckpointLocation());
        player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.retry")), 1.0f, 1.0f);

        if (session.getCurrentCheckpoint() == -1) {
            player.sendMessage(plugin.getConfigManager().getMessage("retry-no-checkpoint"));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("retry-teleport"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
