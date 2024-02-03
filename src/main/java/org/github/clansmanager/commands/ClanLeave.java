package org.github.clansmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.DBManager;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClanLeave extends SubCommand {

    private CManager clans = null;

    public ClanLeave() {
        this.clans = new CManager();
    }
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + this.getName();
    }

    @Override
    public String getDescription() {
        return "Leave Your Clan!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.leave";
    }

    @Override
    public String getUsage() {
        return "/clan " + this.getName();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = this.clans.getClanByOwnerPlayer(((Player) sender));
        if(clan == null)
            clan = this.clans.getClanByMemberPlayer(((Player) sender));

        if(clan == null){
            sender.sendMessage(Messages.withPrefix("error-clan-not-found", "&cClan not found!"));
            return true;
        }

        if(clan.isOwner(((Player) sender))){
            sender.sendMessage(Messages.withPrefix("error-clan-leave-owner", "&cYou can't leave your clan instant your can remove it!"));
            return true;
        }

        if(clan.isMember(((Player) sender))){
            if(this.clans.leaveClan(((Player) sender), clan.getId())){
                sender.sendMessage(Messages.withPrefix("successful-clan-leave", "&aSuccessful leave clan %clan_name%!").replace("%clan_name%", clan.getName()));
                return true;
            }
            sender.sendMessage(Messages.withPrefix("error-clan-not-found", "&cClan not found!"));
            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
