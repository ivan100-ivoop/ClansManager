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

import java.util.List;

public class Battle extends SubCommand {
    private CManager clans = null;

    public Battle() {
        this.clans = new CManager();
    }
    @Override
    public String getName() {
        return "battle";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return "Clan battle game!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.battle";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <clanName>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null, clan2 = null;

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            if(args.length < 1) {
                sender.sendMessage(Messages.withPrefix("errors.clan-name", "&cClan name is required!"));
                return false;
            }

            clan = this.clans.getClanByOwnerPlayer(player);

            if(clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if(clan == null){
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            List<Clan> _clans = this.clans.getAllClans();
            for (Clan c : _clans){
                if(c.getName().equalsIgnoreCase(args[0])){
                    clan2 = c;
                }
            }

            if(clan2 == null){
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if( clan.getName().equalsIgnoreCase(clan2.getName()) ){
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

            TextComponent main = new TextComponent(Messages.withPrefix("clan.battle-invite", "&bYou have been invited to the clan battle click here to accept").replace("%clan_name%", clan.getName()));
            main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.onlyMessage("clan.battle-accept", "&aAccept clan battle", false)).create()));
            main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/clan battleAccept %s %s", clan.getName(), clan2.getName()) ));
            clanOwner.spigot().sendMessage(main);
            player.sendMessage(Messages.withPrefix("success.battle-invite", "&aYou send invite for clan battle."));
            return true;
        }
        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return this.clans.getClansTab();
    }
}
