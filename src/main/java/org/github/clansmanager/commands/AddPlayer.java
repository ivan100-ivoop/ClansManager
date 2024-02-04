package org.github.clansmanager.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.Collections;
import java.util.List;

public class AddPlayer extends SubCommand {
    private CManager clans = null;

    private int maxPlayers = -1;

    public AddPlayer() {
        this.clans = new CManager();
        this.maxPlayers = Loader.getPlugin(Loader.class).getConfig().getInt("max-clan-players", 3);
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return "Add new Player to your Clan!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.invite";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <ClanName> <Player>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;

        if(sender instanceof Player){
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage(Messages.withPrefix("errors.invite-player", "&cTo add new player your need to include him name!"));
                return true;
            }

            Player newMemberPlayer = Bukkit.getPlayer(args[0]);

            if (newMemberPlayer == null) {
                player.sendMessage(Messages.withPrefix("errors.player-offline", "&cThis player is offline!"));
                return true;
            }

           if (this.clans.isMemberOrOwner(newMemberPlayer)) {
                player.sendMessage(Messages.withPrefix("errors.clan-already-member", "&cThis player already is clan member!"));
                return true;
            }

            clan = this.clans.getClanByOwnerPlayer(player);
            if (clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if (clan == null) {
                player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if (clan.getMembers().size() >= this.maxPlayers) {
                player.sendMessage(Messages.withPrefix("errors.max-player", "&cYour have max count of players!"));
                return true;
            }

            TextComponent main = new TextComponent(Messages.withPrefix("clan.invite", "&bYou have been invited to the clan %clan_name% click to accept it").replace("%clan_name%", clan.getName()));
            main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.onlyMessage("clan.accept", "&aAccept clan invite", false)).create()));
            main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept " + clan.getName()));

            if(!this.clans.invitePlayer(newMemberPlayer, clan.getId())) {
                player.sendMessage(Messages.withPrefix("errors.clan-already-member", "&cThis player already is clan member!"));
                return true;
            }

            newMemberPlayer.spigot().sendMessage(main);
            player.sendMessage(Messages.withPrefix("success.invite", "&bYou have been invited %player_name% to clan %clan_name%!").replace("%clan_name%", clan.getName()).replace("%player_name%", newMemberPlayer.getName()));
            return true;
        }

        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
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
