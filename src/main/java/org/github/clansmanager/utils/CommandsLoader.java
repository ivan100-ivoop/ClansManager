package org.github.clansmanager.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.github.clansmanager.commands.*;

import java.util.*;

public class CommandsLoader implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public CommandsLoader() {
        this.loadInternalCommands();
    }

    private void loadInternalCommands(){
        registerSubCommand(new Reload());
        registerSubCommand(new Create());
        registerSubCommand(new Remove());
        registerSubCommand(new Tp());
        registerSubCommand(new SetOptions());
        registerSubCommand(new AddPlayer());
        registerSubCommand(new ClanAccept());
        registerSubCommand(new ClanLeave());
        registerSubCommand(new Kick());
        registerSubCommand(new KickAll());
        registerSubCommand(new Chest());
        registerSubCommand(new ClanList());
        registerSubCommand(new Chat());
        registerSubCommand(new Bank());
        registerSubCommand(new Admin());
        registerSubCommand(new LockUlock());
        registerSubCommand(new Battle());
        registerSubCommand(new battleAccept());
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("clansmanager.clan") && !sender.isOp()){
            sender.sendMessage(Messages.withPrefix("errors.no-permission", "&4You do not have the necessary permissions to execute this command."));
            return true;
        }

        if (args.length > 0 && subCommands.containsKey(args[0].toLowerCase())) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());

            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                command.setDescription(subCommand.getDescription());
                if (!subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length))) {

                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        TextComponent main = new TextComponent(Messages.withPrefix("errors.usage", "Usage: %command%").replace("%command%", subCommand.getUsage()));
                        main.setColor(ChatColor.YELLOW);
                        main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(subCommand.getUsage()).create()));
                        main.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, subCommand.asUsageCommand()));

                        player.spigot().sendMessage(main);
                    }
                }
            } else {
                sender.sendMessage(Messages.withPrefix("errors.no-permission", "&4You do not have the necessary permissions to execute this command."));
            }
        } else {
            sender.sendMessage(Messages.withPrefix("errors.invalid-command", "&4Invalid command."));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if(!sender.hasPermission("clansmanager.clan") && !sender.isOp()){
            return  Collections.emptyList();
        }

        if (args.length == 1) {
            for( Map.Entry<String, SubCommand> command1 : subCommands.entrySet()){
                if(command1.getValue().getPermission() != null && sender.hasPermission(command1.getValue().getPermission())){
                    if (command1.getKey().toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(command1.getKey());
                    }
                }
            }
        } else if (args.length > 1 && subCommands.containsKey(args[0].toLowerCase())) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand.getPermission() != null && sender.hasPermission(subCommand.getPermission())) {
                completions.addAll(subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length)));
            }
        }

        return completions;
    }

    public void reload() {
        subCommands.clear();
        this.loadInternalCommands();
    }
}