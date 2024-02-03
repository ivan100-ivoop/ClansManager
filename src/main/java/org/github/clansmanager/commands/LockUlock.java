package org.github.clansmanager.commands;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LockUlock extends SubCommand {
    private CManager clans = null;

    public LockUlock(){
        this.clans = new CManager();
    }

    @Override
    public String getName() {
        return "lock";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName();
    }

    @Override
    public String getDescription() {
        return "Lock your clan commands for members!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.lock";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            clan = this.clans.getClanByOwnerPlayer(player);

            if (clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if (clan == null) {
                player.sendMessage(Messages.withPrefix("error-clan-not-found", "&cClan not found!"));
                return true;
            }

            if(clan.isMember(player)){
                player.sendMessage(Messages.withPrefix("clan-lock-perms", "&cThis command is only available for owner of this clan!!"));
                return true;
            }

            if(clan.isLock()){
                clan.setLock(false);
                player.sendMessage(Messages.withPrefix("clan-locked", "&aSuccessful Unlocked!"));
                return true;
            } else {
                clan.setLock(true);
                player.sendMessage(Messages.withPrefix("clan-unlock", "&aSuccessful Locked!"));
                return true;
            }


        }

        sender.sendMessage(Messages.onlyMessage("player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
