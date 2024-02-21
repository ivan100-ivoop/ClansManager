package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.ClanChestManager;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Collections;
import java.util.List;

public class Remove extends SubCommand {
    private CManager clans = null;
    private ClanChestManager clanChest = null;
    public Remove() {
        this.clans = new CManager();
        this.clanChest = new ClanChestManager();
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + this.getName() + " ";
    }

    @Override
    public String getDescription() {
        return Messages.onlyMessage("commands.remove", "Remove You Clan!");
    }

    @Override
    public String getPermission() {
        return "clansmanager.remove";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }
            Clan clan = this.clans.removeClan(player);

            if(clan == null){
                player.sendMessage(Messages.withPrefix("errors.clan-remove", "&cClan is not removed!"));
                return true;
            }
            player.sendMessage(Messages.withPrefix("success.clan-remove", "&aClan Successful removed!"));
            return true;
        } else {
            if(args.length < 1) {
                sender.sendMessage(Messages.withPrefix("errors.clan-name", "&cClan name is required!"));
                return false;
            }
            Clan clan = this.clans.removeClan(args[0]);

            if(clan == null){
                sender.sendMessage(Messages.withPrefix("errors.clan-remove", "&cClan is not removed!"));
                return true;
            }

            this.clanChest.removeChest(clan);

            sender.sendMessage(Messages.withPrefix("success.clan-remove", "&aClan Successful removed!"));
            return true;
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0 && sender.hasPermission(this.getPermission())) {
            return Collections.singletonList(this.getName());
        }

        return Collections.emptyList();
    }
}
