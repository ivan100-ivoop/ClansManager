package org.github.clansmanager.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.Admin.AdminCommands;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.game.Arena;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.io.IOException;
import java.util.*;

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
        return "/clan " + getName() + " <setInv1, setInv2, setSpawn1, setSpawn2, lock, spychat, chest, balance, remove, kick, setOwner> <clan> <value>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(Messages.withPrefix("errors.admin-option", "&cAdmin Option required!"));
                return false;
            }
        }

        if(args[0].equalsIgnoreCase("arena")){
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(args[1].equalsIgnoreCase("select")) {
                    Loader.tempId = args[2];
                    Arena arena = Loader.arenas.get(Loader.tempId);
                    player.sendMessage(Messages.withPrefix("success.arena.select", "&aSelected Arena %arena%!").replace("%arena%", arena.ARENA_NAME));
                    return true;
                }

                if(args[1].equalsIgnoreCase("create")) {
                    Loader.tempId = UUID.randomUUID().toString();
                    Loader.arenas.put(Loader.tempId, new Arena().setID(Loader.tempId)
                            .setLocation(player.getLocation()));
                    player.sendMessage(Messages.withPrefix("success.arena.create", "&aArena Created!"));
                    return true;
                }

                if(args[1].equalsIgnoreCase("name")) {

                    if(Loader.tempId == null){
                        player.sendMessage(Messages.withPrefix("errors.arena.select", "&cSelect Arena!"));
                        return true;
                    }

                    Loader.arenas.get(Loader.tempId).setName(getName(Arrays.copyOfRange(args, 1, args.length)));
                    player.sendMessage(Messages.withPrefix("success.arena.name", "&aArena Name Updated!"));
                    return true;
                }

                if(args[1].equalsIgnoreCase("size")) {

                    if(Loader.tempId == null){
                        player.sendMessage(Messages.withPrefix("errors.arena.select", "&cSelect Arena!"));
                        return true;
                    }

                    Loader.arenas.get(Loader.tempId).setMaxClans(Integer.parseInt(args[2]));
                    player.sendMessage(Messages.withPrefix("success.arena.size", "&aArena Clan Max Updated!"));
                    return true;
                }

                if(args[1].equalsIgnoreCase("spawn")) {

                    if(Loader.tempId == null){
                        player.sendMessage(Messages.withPrefix("errors.arena.select", "&cSelect Arena!"));
                        return true;
                    }

                    Loader.arenas.get(Loader.tempId).addSpawn(player.getLocation());
                    player.sendMessage(Messages.withPrefix("success.arena.spawn", "&aArena Spawn Created!"));
                    return true;
                }


                if(args[1].equalsIgnoreCase("tp")) {

                    if(Loader.tempId == null){
                        player.sendMessage(Messages.withPrefix("errors.arena.select", "&cSelect Arena!"));
                        return true;
                    }
                    player.teleport(Loader.arenas.get(Loader.tempId).ARENA_LOCATION);
                    return true;
                }


                if(args[1].equalsIgnoreCase("inventory")) {

                    if(Loader.tempId == null){
                        player.sendMessage(Messages.withPrefix("errors.arena.select", "&cSelect Arena!"));
                        return true;
                    }

                    Loader.arenas.get(Loader.tempId).addInventory(player.getInventory().getContents());
                    player.sendMessage(Messages.withPrefix("success.arena.inventory", "&aArena Inventory Created!"));
                    return true;
                }

                if(args[1].equalsIgnoreCase("save")) {

                    if(Loader.tempId == null){
                        player.sendMessage(Messages.withPrefix("errors.arena.select", "&cSelect Arena!"));
                        return true;
                    }

                    try {
                        Loader.arenas.get(Loader.tempId).saveArena();
                        Loader.tempId = null;
                        player.sendMessage(Messages.withPrefix("success.arena.save", "&aArena Saved!"));
                    } catch (IOException e) {
                        player.sendMessage(Messages.withPrefix("errors.arena.save", "&cArena not saved!"));
                    }
                    return true;
                }
            }
            return true;
        }

        switch (args[0]){
            case "chest":
                if(sender instanceof Player){
                    return AdminCommands.openClanChest(args[1], ((Player) sender), this.clans);
                }
                sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
                return true;
            case "balance":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("errors.admin-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.clanBalance(args[1], Arrays.copyOfRange(args, 1, args.length), ((Player) sender), this.clans);
            case "setOwner":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("errors.admin-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.setOwner(args[1], Arrays.copyOfRange(args, 1, args.length), ((Player) sender), this.clans);
            case "kick":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("errors.admin-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.kickMember(args[1], Arrays.copyOfRange(args, 1, args.length), ((Player) sender), this.clans);
            case "remove":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("errors.admin-option", "&cAdmin Option required!"));
                    return false;
                }
                return AdminCommands.removeClan(args[1], ((Player) sender), this.clans);
            case "lock":
                return AdminCommands.lockClan(args[1], ((Player) sender), this.clans);
            case "spychat":
                if (args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("errors.admin-option", "&cAdmin Option required!"));
                    return false;
                }

                if(args[1].equalsIgnoreCase("enable")){
                    if(AdminCommands.enableSpyClanChat(((Player) sender))){
                        sender.sendMessage(Messages.withPrefix("success.admin-spy-enabled", "&aSuccessful enabled clan spy chat!"));
                        return true;
                    }
                    sender.sendMessage(Messages.withPrefix("errors.admin-spy-chat", "&cHmm error spy chat not executed!"));
                    return true;
                }
                if(args[1].equalsIgnoreCase("disable")){
                    if(AdminCommands.disableSpyClanChat(((Player) sender))){
                        sender.sendMessage(Messages.withPrefix("success.admin-spy-disabled", "&aSuccessful disabled clan spy chat!"));
                        return true;
                    }
                    sender.sendMessage(Messages.withPrefix("errors.admin-spy-chat", "&cHmm error spy chat not executed!"));
                    return true;
                }
            default:
                return false;
        }

    }

    private String getName(String ...args){
        StringBuilder out = new StringBuilder();
        for (int i=1; i<args.length; i++){
            out.append(args[i]).append(" ");
        }
        return out.toString();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 1 && sender.hasPermission(this.getPermission())) {
            return Arrays.asList("arena", "lock", "spychat", "chest", "balance", "remove", "kick", "setOwner");
        }

        if (args.length >= 2 && sender.hasPermission(this.getPermission())) {
            if (args[0].equalsIgnoreCase("arena")) {
                if (args[1].equalsIgnoreCase("select")){
                    List<String> tab = new ArrayList<>();
                    for (Map.Entry<String, Arena> arena : Loader.arenas.entrySet()){
                        tab.add(arena.getValue().ARENA_ID);
                    }
                    return tab;
                }

                return Arrays.asList("select", "create", "save", "name", "spawn", "inventory", "size", "tp");
            }

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
