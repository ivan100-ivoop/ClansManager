package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.Collections;
import java.util.List;

public class Create extends SubCommand {
    private CManager clans = null;
    public Create() {
        this.clans = new CManager();
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return "Create Your Clan!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.create";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <ClanName>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }


            if(args.length < 1) {
                sender.sendMessage(Messages.withPrefix("errors.clan-name", "&cClan name is required!"));
                return false;
            }

            if(this.clans.isMemberOrOwner(player)){
                sender.sendMessage(Messages.withPrefix("errors.already-clan-member", "&cYour already a clan member!"));
                return true;
            }

            if(!Utils.isValidClanName(args[0])){
                sender.sendMessage(Messages.withPrefix("errors.invalid-clan-name", "&cThis clanName is not valid!"));
                return true;
            }

            if(!this.clans.createClan(args[0], player)){
                player.sendMessage(Messages.withPrefix("errors.clan-save", "&cClan not is saved!"));
                return true;
            }

            player.sendMessage(Messages.withPrefix("success.clan-save", "&aClan Successful created!"));
            return true;

        }

        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 0 && sender.hasPermission(this.getPermission())){
            return Collections.singletonList(this.getName());
        }
        return Collections.emptyList();
    }
}
