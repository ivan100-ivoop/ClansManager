package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.game.container.GamesInstance;
import org.github.clansmanager.utils.*;

import java.util.Collections;
import java.util.List;

public class Test extends SubCommand {
    private CManager clans = null;

    public Test() {
        this.clans = new CManager();
    }
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName();
    }

    @Override
    public String getDescription() {
        return "Test Game";
    }

    @Override
    public String getPermission() {
        return "clansmanager.game";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null, clan2 = null;

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            if(args.length < 1) {
                sender.sendMessage(Messages.withPrefix("missing-clan-name", "&cClan name is required!"));
                return false;
            }

            clan = this.clans.getClanByOwnerPlayer(player);

            if(clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if(clan == null){
                sender.sendMessage(Messages.withPrefix("error-clan-not-found", "&cClan not found!"));
                return true;
            }

            List<Clan> _clans = this.clans.getAllClans();
            for (Clan c : _clans){
                if(c.getName().equalsIgnoreCase(args[0])){
                    clan2 = c;
                }
            }

            if(clan2 == null){
                sender.sendMessage(Messages.withPrefix("error-clan-not-found", "&cClan not found!"));
                return true;
            }

            Loader.games.add(new GamesInstance(clan, clan2));

            return true;
        }
        sender.sendMessage(Messages.onlyMessage("player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return this.clans.getClansTab();
    }
}
