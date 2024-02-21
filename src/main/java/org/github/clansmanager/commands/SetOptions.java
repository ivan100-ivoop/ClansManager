package org.github.clansmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetOptions extends SubCommand {
    private CManager clans = null;
    private int maxPrefix = -1;
    public SetOptions() {
        this.maxPrefix = Loader.getPlugin(Loader.class).getConfig().getInt("clan-prefix-max", 8);
        this.clans = new CManager();
    }
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + this.getName() + " ";
    }

    @Override
    public String getDescription() {
        return Messages.onlyMessage("commands.options", "Set clan Options!");
    }

    @Override
    public String getPermission() {
        return "clansmanager.set";
    }

    @Override
    public String getUsage() {
        return "/clan " + this.getName() + " <tp, prefix, owner>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;

        if(args.length < 1) {
            sender.sendMessage(Messages.withPrefix("errors.clan-option", "&cClan option is required!"));
            return false;
        }

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            clan =  this.clans.getClanByOwnerPlayer(player);

            if(clan == null){
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if(clan.isLock() && clan.isMember(player)){
                sender.sendMessage(Messages.withPrefix("errors.clan-locked", "&cOps Clan is Locked!"));
                return true;
            }

            switch (args[0]){
                case "tp":
                    if(!this.clans.updateClanLocation(player)) {
                        player.sendMessage(Messages.withPrefix("errors.clan-location-set", "&cUnsuccessful set new clan teleport location!"));
                        return true;
                    }

                    player.sendMessage(Messages.withPrefix("success.clan-location-set", "&aSuccessful set new clan teleport location!"));
                    return true;
                case "owner":

                    if (args.length < 2) {
                        sender.sendMessage(Messages.withPrefix("errors.clan-new-owner", "&cClan new owner is required!"));
                        return true;
                    }

                    Player newOwnerPlayer = Bukkit.getPlayer(args[1]);

                    if (newOwnerPlayer == null){
                        sender.sendMessage(Messages.withPrefix("errors.player-offline", "&cThis player is offline!"));
                        return true;
                    }

                    if (clan.isOwner(newOwnerPlayer)) {
                        sender.sendMessage(Messages.withPrefix("errors.clan-owner", "&cYou are unable to give the clan to yourself!"));
                        return true;
                    }

                    if(!this.clans.updateClanOwner(player, newOwnerPlayer)){
                        player.sendMessage(Messages.withPrefix("errors.clan-owner-error", "&cUnsuccessful set new clan owner!"));
                        return true;
                    }

                    newOwnerPlayer.sendMessage(Messages.withPrefix("success.clan-new-owner", "&eYou have been given ownership of the clan %clan_name% from %old_owner%").replace("%clan_name%", clan.getName()).replace("%old_owner%", clan.getOwner()));
                    sender.sendMessage(Messages.withPrefix("success.clan-owner-notify", "&aSuccessfully set new clan owner!"));
                    return true;
                case "prefix":

                    String clanPrefix = Utils.getText(Arrays.copyOfRange(args, 1, args.length));

                    if(clanPrefix.length() > this.maxPrefix){
                        sender.sendMessage(Messages.withPrefix("errors.clan-prefix-count", "&cYour are unable to set to big prefix!"));
                        return true;
                    }

                    if(!this.clans.updateClanPrefix(player, clanPrefix)){
                        player.sendMessage(Messages.withPrefix("errors.clan-prefix", "&cUnsuccessful set new clan prefix!"));
                        return true;
                    }

                    player.sendMessage(Messages.withPrefix("success.clan-prefix", "&aSuccessful set new clan prefix!"));
                    return true;
                default:
                    player.sendMessage(Messages.withPrefix("errors.clan-set-command", "&cOps, you can use only tp,prefix,owner"));
                    break;
            }
            return true;
        }

        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if(args.length == 1 && sender.hasPermission(this.getPermission())){
            return Arrays.asList("tp", "prefix", "owner");
        }

        if(args.length == 2 && sender.hasPermission(this.getPermission())){
            if(args[0].equals("owner"))
                return Utils.onlinePlayers();
        }

        return Collections.emptyList();
    }
}
