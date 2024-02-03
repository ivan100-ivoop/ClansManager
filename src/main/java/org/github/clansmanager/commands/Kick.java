package org.github.clansmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.Collections;
import java.util.List;

public class Kick extends SubCommand {

    private CManager clans;

    public Kick(){
        this.clans = new CManager();
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return "Kick Member from your clan!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.kick";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <ClanMember>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;

        if (args.length < 1) {
            sender.sendMessage(Messages.withPrefix("missing-clan-remove-player", "&cTo remove player your need to include him name!"));
            return true;
        }

        if(sender instanceof Player) {
            Player player = (Player) sender;

            clan = this.clans.getClanByOwnerPlayer(player);

            if (clan == null) {
                player.sendMessage(Messages.withPrefix("error-clan-not-found", "&cClan not found!"));
                return true;
            }

            if(!clan.isOwner(player)){
                player.sendMessage(Messages.withPrefix("error-clan-not-owner", "&cYour are not this clan owner!"));
                return true;
            }

            Player newMemberPlayer = Bukkit.getPlayer(args[0]);

            if (newMemberPlayer == null) {
                player.sendMessage(Messages.withPrefix("player-offline", "&cThis player is offline!"));
                return true;
            }

            if (!clan.isMember(newMemberPlayer)) {
                player.sendMessage(Messages.withPrefix("player-not-fount", "&cThis player is not clan member!"));
                return true;
            }

            clan = this.clans.kickClanMember(player, newMemberPlayer);

            if(clan == null){
                player.sendMessage(Messages.withPrefix("clan-kick-unsuccess", "&eYour unsuccessful kick all player members!"));
                return true;
            }

            player.sendMessage(Messages.withPrefix("clan-kick-success", "&eYour successful kick player %player% from clan %clan_name%!").replace("%player%", newMemberPlayer.getName()).replace("%clan_name%", clan.getName()));
            newMemberPlayer.sendMessage(Messages.withPrefix("clan-kick-notify", "&eYour are kick from clan %clan_name%").replace("%clan_name%", clan.getName()));
            return true;
        }

        sender.sendMessage(Messages.onlyMessage("player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission(this.getPermission())) {
            return Utils.onlinePlayers();
        }
        return Collections.emptyList();
    }
}
