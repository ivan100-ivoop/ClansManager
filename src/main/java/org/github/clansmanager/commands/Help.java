package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Help extends SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName();
    }

    @Override
    public String getDescription() {
        return "Display help information about ClansManager commands.";
    }

    @Override
    public String getPermission() {
        return "clansmanager.clan";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(Messages.withPrefix("help.title", "Available commands:"));
        for (Map.Entry<String, SubCommand> command : Loader.cmd.getCommands()){
            String commandName = command.getKey();
            SubCommand _command = command.getValue();

            if(sender.hasPermission(_command.getPermission())) {
                String present = Messages.onlyMessage("help.present", "/clan %command% %usage% - %description%");
                present = present.replace("%command%", _command.getName());
                present = present.replace("%description%", _command.getDescription());
                present = present.replace("%usage%", _command.getUsage());
                sender.sendMessage(present);
            }
        }

        sender.sendMessage("/clan set <prefix, owner, tp> - Set various properties of your clan");
        // Add more help information as needed
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
