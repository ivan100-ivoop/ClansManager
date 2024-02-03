package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.github.clansmanager.Admin.AdminCommands;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.game.Arena;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Admin extends SubCommand {

    private CManager clans = null;

    public Admin() {
        this.clans = new CManager();
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName();
    }

    @Override
    public String getDescription() {
        return "Clan Admin Commands!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.admin";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <chest> <clan>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(Messages.withPrefix("missing-clan-option", "&cAdmin Option required!"));
                return false;
            }
        }

        if(args[0].equalsIgnoreCase("spawn1")){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                Arena.setSpawn1(player.getLocation());
                player.sendMessage(Utils.fixColors("&aSpawn1 Saved!"));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("spawn2")){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                Arena.setSpawn2(player.getLocation());
                player.sendMessage(Utils.fixColors("&aSpawn2 Saved!"));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("inv")){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                Arena.setInv(player.getInventory());
                player.sendMessage(Utils.fixColors("&aInv Saved!"));
            }
            return true;
        }

        switch (args[0]){
            case "chest":
                if(sender instanceof Player){
                    return AdminCommands.openClanChest(args[1], ((Player) sender), this.clans);
                }
                sender.sendMessage(Messages.onlyMessage("player-only-command", "&cThis command can be run only from a player!", true));
                return true;
            case "balance":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("missing-clan-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.clanBalance(args[1], Arrays.copyOfRange(args, 1, args.length), ((Player) sender), this.clans);
            case "setOwner":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("missing-clan-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.setOwner(args[1], Arrays.copyOfRange(args, 1, args.length), ((Player) sender), this.clans);
            case "kick":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("missing-clan-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.kickMember(args[1], Arrays.copyOfRange(args, 1, args.length), ((Player) sender), this.clans);
            case "remove":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("missing-clan-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.removeClan(args[1], ((Player) sender), this.clans);
            case "lock":
                return AdminCommands.lockClan(args[1], ((Player) sender), this.clans);
            case "spychat":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("missing-clan-option", "&cAdmin Option required!"));
                    return false;
                }

                if(args[1].equalsIgnoreCase("enable")){
                    if(AdminCommands.enableSpyClanChat(((Player) sender))){
                        sender.sendMessage(Messages.withPrefix("admin-clan-spy-enabled", "&aSuccessful enabled clan spy chat!"));
                        return true;
                    }
                    sender.sendMessage(Messages.withPrefix("admin-clan-spy-error", "&cHmm error spy chat not executed!"));
                    return true;
                }
                if(args[1].equalsIgnoreCase("disable")){
                    if(AdminCommands.disableSpyClanChat(((Player) sender))){
                        sender.sendMessage(Messages.withPrefix("admin-clan-spy-disabled", "&aSuccessful disabled clan spy chat!"));
                        return true;
                    }
                    sender.sendMessage(Messages.withPrefix("admin-clan-spy-error", "&cHmm error spy chat not executed!"));
                    return true;
                }
            default:
                return false;
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 1 && sender.hasPermission(this.getPermission())) {
            return Arrays.asList("inv", "spawn1", "spawn2", "lock", "spychat", "chest", "balance", "remove", "kick", "setOwner");
        }

        if (args.length == 2 && sender.hasPermission(this.getPermission())) {
            if(args[0].equalsIgnoreCase("spychat")){
                return Arrays.asList("enable", "disable");
            } else {
                return this.clans.getClansTab();
            }
        }

        if (args.length == 3 && sender.hasPermission(this.getPermission())) {
            if (args[0].equalsIgnoreCase("balance"))
                return Arrays.asList("clear", "take","give", "set");
        }

        if (args.length == 3 && sender.hasPermission(this.getPermission())) {
            if(args[0].equalsIgnoreCase("kick")) {
                List<String> clanMembers = new ArrayList<>();
                Clan clan = this.clans.getClansByName(args[1]);
                clanMembers.add(clan.getOwner());
                for(String member : clan.getMembers()){
                    clanMembers.add(member);
                }
                return clanMembers;
            }
            if(args[0].equalsIgnoreCase("balance"))
                return this.clans.getClansTab();
            if(args[0].equalsIgnoreCase("setOwner"))
                return Utils.onlinePlayers();
        }

        return Collections.emptyList();
    }
}
