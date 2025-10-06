package nl.dutchcoding.parkourrace.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getUsage();

    public abstract String getPermission();

    public abstract boolean isPlayerOnly();

    public abstract void execute(CommandSender sender, String[] args);

    public List<String> getAliases() {
        return new ArrayList<>();
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
