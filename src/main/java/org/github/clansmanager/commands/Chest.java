package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.utils.*;

import java.util.Collections;
import java.util.List;

public class Chest extends SubCommand {
    private CManager clans = null;
    private ClanChestManager clanChest = null;

    public Chest(){
        this.clans = new CManager();
        this.clanChest = new ClanChestManager();
    }
    @Override
    public String getName() {
        return "chest";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName();
    }

    @Override
    public String getDescription() {
        return Messages.onlyMessage("commands.chest", "Clan Personal Chest!");
    }

    @Override
    public String getPermission() {
        return "clansmanager.chest";
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

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            clan = this.clans.getClanByOwnerPlayer(player);

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

            if(this.clanChest.hasChest(clan)) {

                this.clanChest.createEmptyChest(clan, this.clanChest.calculateSlots(Utils.getPlayer(clan.getOwner())));
            }

            this.clanChest.openChest(clan, player);
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
