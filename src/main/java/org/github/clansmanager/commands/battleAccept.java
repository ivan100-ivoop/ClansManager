package org.github.clansmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.game.ClanBattleGame;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Collections;
import java.util.List;

public class battleAccept extends SubCommand {
    private CManager clans = null;

    public battleAccept() {
        this.clans = new CManager();
    }
    @Override
    public String getName() {
        return "battleAccept";
    }

    @Override
    public String asUsageCommand() {
        return null;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getPermission() {
        return "clansmanager.baccept";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        Clan clan = null, clan2 = null;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(Messages.withPrefix("errors.clan-name", "&cClan name is required!"));
                return false;
            }

            clan = this.clans.getClanByOwnerPlayer(player);

            if (clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if (clan == null) {
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            List<Clan> _clans = this.clans.getAllClans();
            for (Clan c : _clans) {
                if (c.getName().equalsIgnoreCase(args[0])) {
                    clan2 = c;
                }
            }

            if (clan2 == null) {
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if (clan.getName().equalsIgnoreCase(clan2.getName())) {
                sender.sendMessage(Messages.withPrefix("errors.clan-battle", "&cHmm is not allow to fight yourself!"));
                return true;
            }

            Player clanOwner = Bukkit.getPlayer(clan2.getOwner());

            if (clanOwner == null) {
                player.sendMessage(Messages.withPrefix("errors.owner-offline", "&cThis clan owner is offline!"));
                return true;
            }

            if(Loader.instance.getConfig().getBoolean("battle-allow", false)){
                player.sendMessage(Messages.withPrefix("errors.battle-disabled", "&cClan battles are currently disabled."));
                return true;
            }

            player.sendMessage(Messages.withPrefix("success.accept-battle", "&aYou accepted the clan battle request."));
            ClanBattleGame game = new ClanBattleGame(clan2, clan);
            game.startGame();
            Loader.games.add(game);
            return false;
        }
        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
