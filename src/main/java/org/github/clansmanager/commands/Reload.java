package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Collections;
import java.util.List;

public class Reload extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload Plugin Configuration!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.reload";
    }

    @Override
    public String getUsage() {
        return "/clan " + this.getName();
    }
    @Override
    public String asUsageCommand() { return "/clan " + getName() + " ";}

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if((sender instanceof Player) && !sender.hasPermission(this.getPermission())){
            sender.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
            return true;
        }

        if(Loader.getPlugin(Loader.class).reloadPlugin()){
            if(sender instanceof Player){
                sender.sendMessage(Messages.withPrefix("success.reload", "&aSuccessful reload!"));
            } else {
                sender.sendMessage(Messages.onlyMessage("success.reload", "&aSuccessful reload!", true));
            }
        } else {
            if(sender instanceof Player){
                sender.sendMessage(Messages.withPrefix("error.reload", "&cNot successful reload!"));
            } else {
                sender.sendMessage(Messages.onlyMessage("error.reload", "&cNot successful reload!", true));
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0 && sender.hasPermission(this.getPermission())) {
            return Collections.singletonList(this.getName());
        }

        return Collections.emptyList();
    }
}
