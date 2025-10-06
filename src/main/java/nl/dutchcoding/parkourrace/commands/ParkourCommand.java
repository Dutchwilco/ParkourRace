package nl.dutchcoding.parkourrace.commands;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ParkourCommand implements CommandExecutor, TabCompleter {

    private final ParkourRace plugin;
    private final Map<String, SubCommand> subCommands;

    public ParkourCommand(ParkourRace plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();

        // Register subcommands
        registerSubCommand(new CreateCommand(plugin));
        registerSubCommand(new DoneCommand(plugin));
        registerSubCommand(new CancelCommand(plugin));
        registerSubCommand(new ListCommand(plugin));
        registerSubCommand(new DeleteCommand(plugin));
        registerSubCommand(new JoinCommand(plugin));
        registerSubCommand(new LeaveCommand(plugin));
        registerSubCommand(new RetryCommand(plugin));
        registerSubCommand(new BestCommand(plugin));
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
        for (String alias : subCommand.getAliases()) {
            subCommands.put(alias.toLowerCase(), subCommand);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("prefix") + " §cUnknown subcommand. Use §e/" + label + " help§c for help.");
            return true;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        subCommand.execute(sender, subArgs);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (SubCommand subCommand : new HashSet<>(subCommands.values())) {
                if (sender.hasPermission(subCommand.getPermission()) && subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand.getName());
                }
            }
            return completions;
        }

        if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null && sender.hasPermission(subCommand.getPermission())) {
                return subCommand.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return new ArrayList<>();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8§m                §r §e§lParkourRace §8§m                ");
        sender.sendMessage("§e/pk create <name> §8- §7Start course setup");
        sender.sendMessage("§e/pk done §8- §7Finish and save course");
        sender.sendMessage("§e/pk cancel §8- §7Cancel course setup");
        sender.sendMessage("§e/pk list §8- §7List all courses");
        sender.sendMessage("§e/pk delete <name> §8- §7Delete a course");
        sender.sendMessage("§e/pk join <name> §8- §7Join a course");
        sender.sendMessage("§e/pk leave §8- §7Leave current course");
        sender.sendMessage("§e/pk retry §8- §7Teleport to checkpoint");
        sender.sendMessage("§e/pk best [name] §8- §7View personal bests");
        sender.sendMessage("§8§m                                      ");
    }
}
