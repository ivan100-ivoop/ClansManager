package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Collections;
import java.util.List;

public class Tp extends SubCommand {
    private CManager clans = null;

    public Tp() {
        this.clans = new CManager();
    }
    @Override
    public String getName() {
        return "tp";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + this.getName() + " ";
    }

    @Override
    public String getDescription() {
        return Messages.onlyMessage("commands.tp", "Teleport to Your ClanBase!");
    }

    @Override
    public String getPermission() {
        return "clansmanager.teleport";
    }

    @Override
    public String getUsage() {
        return "/clan " + this.getName() + " <ClanName>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            Clan clan =  this.clans.getClanByOwnerPlayer(player);

            if(clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if(clan == null){
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if(clan.isLock() && clan.isMember(player)){
                sender.sendMessage(Messages.withPrefix("errors.clan-locked", "&cOps Clan is Locked!"));
                return true;
            }

            if(clan.getLocation() == null){
                sender.sendMessage(Messages.withPrefix("errors.clan-location", "&cClan base location not found!"));
                return true;
            }

            player.teleport(clan.getLocation());
            player.sendMessage(Messages.withPrefix("success.clan-teleport", "&aYour have ben successful teleport to clan &b&l%clan_name%!").replace("%clan_name%", clan.getName()));
            return true;
        }

        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
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
