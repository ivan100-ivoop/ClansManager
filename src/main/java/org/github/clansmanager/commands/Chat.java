package org.github.clansmanager.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.Admin.AdminCommands;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.Collections;
import java.util.List;

public class Chat extends SubCommand {
    private CManager clans = null;
    private String chat = null;
    public Chat(){
        this.clans = new CManager();
        this.chat = Loader.getPlugin(Loader.class).getConfig().getString("clan-chat-present", "&8「%clan_prefix_clear%&8」%player_name%: %message%");
    }
    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return "Send messages to your clan members!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.chat";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <Message>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;
        String ChatPlayer = "";

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            clan = this.clans.getClanByOwnerPlayer(player);

            if (clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if (clan == null) {
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if(clan.isLock() && clan.isMember(player)){
                sender.sendMessage(Messages.withPrefix("errors.clan-locked", "&cOps Clan is Locked!"));
                return true;
            }

            ChatPlayer = String.format("%s%s",
                    (clan.isOwner(player) ? Loader.instance.getConfig().getString("clan-chat-owner-color", "&c&l") : Loader.instance.getConfig().getString("clan-chat-member-color", "&a&l")),
                    player.getName()
            );

            String message = Utils.getText(args);
            String chat = Utils.fixColors(PlaceholderAPI.setPlaceholders(player, this.chat.replace("%player_name%", ChatPlayer).replace("%message%", message)));
            AdminCommands.spyClanChat(chat, player);
            this.sendAllPlayer(clan, chat);
            return true;
        }
        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    private void sendAllPlayer(Clan clan, String chat) {
        Player owner = Bukkit.getPlayer(clan.getOwner());
        if (owner != null)
            owner.sendMessage(chat);

        if(clan.getMembers().size() >= 0){
            for( String member : clan.getMembers()){
                Player newMemberPlayer = Bukkit.getPlayer(member);
                if (newMemberPlayer != null)
                    newMemberPlayer.sendMessage(chat);
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
