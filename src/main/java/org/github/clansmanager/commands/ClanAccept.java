package org.github.clansmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Collections;
import java.util.List;

public class ClanAccept extends SubCommand {
    private CManager clans = null;
    public ClanAccept() {
        this.clans = new CManager();

    }
    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return Messages.onlyMessage("commands.accept", "Accept clan invite!");
    }

    @Override
    public String getPermission() {
        return "clansmanager.invite.accept";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <ClanName>";
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;

        if(args.length < 1) {
            sender.sendMessage(Messages.withPrefix("errors.clan-name", "&cClan name is required!"));
            return false;
        }

        if(sender instanceof Player){
            Player player = (Player) sender;
            String clanName = args[0];

            if(this.clans.isMemberOrOwner(player)){
                sender.sendMessage(Messages.withPrefix("errors.already-clan-member", "&cYour already is clan member!"));
                return true;
            }

            if(this.clans.getInvites(player).size() >= 0){
                clan = this.clans.acceptClan(player, clanName);

                if(clan == null){
                    sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                    return true;
                }

                if(clan.isLock()){
                    sender.sendMessage(Messages.withPrefix("errors.clan-locked", "&cOps Clan is Locked!"));
                    return true;
                }

                player.sendMessage(Messages.withPrefix("success.accept-invite", "&aYour successful accept clan invite!"));
                Player newMemberPlayer = Bukkit.getPlayer(clan.getOwner());

                if (newMemberPlayer != null)
                    newMemberPlayer.sendMessage(Messages.withPrefix("clan.add-player", "&aYour successful add %player_name% to clan %clan_name%!").replace("%player_name%", player.getName()).replace("%clan_name%", clanName));

                return true;
            }

            sender.sendMessage(Messages.withPrefix("errors.empty-invites", "&cYour not have pending clan invites!"));
            return true;
        }

        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1 && sender.hasPermission(this.getPermission())) {
            return this.clans.getInvites(((Player) sender));
        }

        return Collections.emptyList();
    }

}
