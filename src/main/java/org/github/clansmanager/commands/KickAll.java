package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Collections;
import java.util.List;

public class KickAll extends SubCommand {

    private CManager clans;

    public KickAll(){
        this.clans = new CManager();
    }

    @Override
    public String getName() {
        return "kickAll";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return "KickAll members from your clan!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.kickall";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;

        if(sender instanceof Player) {
            Player player = (Player) sender;

            clan = this.clans.getClanByOwnerPlayer(player);

            if (clan == null) {
                player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if(!clan.isOwner(player)){
                player.sendMessage(Messages.withPrefix("clan.not-owner", "&cYour are not this clan owner!"));
                return true;
            }

            clan = this.clans.kickAllClanMember(player);

            if(clan == null){
                player.sendMessage(Messages.withPrefix("errors.kick-unsuccess", "&eYour unsuccessful kick all player members!"));
                return true;
            }

            return true;
        }

        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
