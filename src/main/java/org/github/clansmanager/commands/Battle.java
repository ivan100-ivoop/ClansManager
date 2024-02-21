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
import org.github.clansmanager.game.ClanBattleGame;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.Arrays;
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
        return Messages.onlyMessage("commands.battle", "Clan battle game!");
    }

    @Override
    public String getPermission() {
        return "clansmanager.battle";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <ClanName>";
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

            if(args[0].equalsIgnoreCase("accept")){
                return acceptBattle(player);
            }

            if(args[0].equalsIgnoreCase("deny")){
                return denyBattle(player);
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

            if (!this.isHaveOnline(clan2)) {
                player.sendMessage(Messages.withPrefix("errors.owner-offline", "&cThis clan member's is offline!"));
                return true;
            }

            if(!Loader.instance.getConfig().getBoolean("battle-allow", false)){
                player.sendMessage(Messages.withPrefix("errors.battle-disabled", "&cClan battles are currently disabled."));
                return true;
            }

            if(Loader.clan_battle.containsKey(clan2.getName())){
                player.sendMessage(Messages.withPrefix("errors.currently-battle", "&cThis Clan currently are in battles."));
                return true;
            }

            if(Utils.isInBattle(clan2)){
                player.sendMessage(Messages.withPrefix("errors.currently-battle", "&cThis Clan currently are in battles."));
                return true;
            }

            TextComponent main = new TextComponent(Messages.withPrefix("clan.battle-invite", "&bYou have been invited to the clan battle click here to accept").replace("%clan_name%", clan.getName()));
            main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.onlyMessage("clan.battle-accept", "&aAccept clan battle", false)).create()));
            main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/clan %s accept", getName()) ));

            if(!this.getOnlineMember(player, clan2, main)){
                player.sendMessage(Messages.withPrefix("errors.owner-offline", "&cThis clan member's is offline!"));
                return true;
            }

            Loader.clan_battle.put(clan2.getName(), clan);

            player.sendMessage(Messages.withPrefix("success.battle-invite", "&aYou send invite for clan battle."));
            return true;
        }
        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    private boolean denyBattle(Player player) {
        Clan clan = this.clans.getClanByOwnerPlayer(player);

        if(clan == null)
            clan = this.clans.getClanByMemberPlayer(player);

        if(clan == null){
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            return true;
        }

        if(Loader.clan_battle.containsKey(clan.getName())){
            Clan oponent = Loader.clan_battle.get(clan.getName());

            if (this.isHaveOnline(oponent)) {
                if (!this.getOnlineMember(player, oponent, Messages.withPrefix("errors.deny-battle", "&cClan %clan_name% deny the clan battle request.").replace("%clan_name%", clan.getName()))) {
                    player.sendMessage(Messages.withPrefix("errors.owner-offline", "&cThis clan member's is offline!"));
                    return true;
                }
            }

            player.sendMessage(Messages.withPrefix("success.deny-battle", "&eYou deny the clan battle request."));
            Loader.clan_battle.remove(clan.getName());

            return true;
        }
        player.sendMessage(Messages.withPrefix("errors.battle-not-found", "&cHmm battle request not found!"));
        return true;
    }

    private boolean acceptBattle(Player player) {

        System.out.println(Loader.clan_battle.entrySet().toArray());
        Clan clan = this.clans.getClanByOwnerPlayer(player);

        if(clan == null)
            clan = this.clans.getClanByMemberPlayer(player);

        if(clan == null){
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            return true;
        }

        if(Loader.clan_battle.containsKey(clan.getName())){
            Clan oponent =Loader.clan_battle.get(clan.getName());

            if (!this.isHaveOnline(oponent)) {
                player.sendMessage(Messages.withPrefix("errors.owner-offline", "&cThis clan member's is offline!"));
                return true;
            }

            if(!this.getOnlineMember(player, oponent, Messages.withPrefix("clan.accept-battle", "&aClan %clan_name% accepted the battle request.").replace("%clan_name%", clan.getName()))){
                player.sendMessage(Messages.withPrefix("errors.owner-offline", "&cThis clan member's is offline!"));
                return true;
            }

            player.sendMessage(Messages.withPrefix("success.accept-battle", "&aYou accepted the clan battle request."));
            ClanBattleGame game = new ClanBattleGame(clan, oponent);
            game.startGame();
            Loader.games.add(game);
            Loader.clan_battle.remove(clan.getName());

            return true;
        }
        player.sendMessage(Messages.withPrefix("errors.battle-not-found", "&cHmm battle request not found!"));
        return true;
    }

    private boolean isHaveOnline(Clan clan){
        Player clanMember = Bukkit.getPlayer(clan.getOwner());
        if (clanMember == null) {
            for (String member : clan.getMembers()){
                clanMember = Bukkit.getPlayer(member);
                if(clanMember != null)
                    return true;
            }
        }

        if(clanMember != null)
            return true;

        return false;
    }


    private boolean getOnlineMember(Player player, Clan clan, TextComponent message){
        Player clanMember = Bukkit.getPlayer(clan.getOwner());
        if (clanMember != null) {
            clanMember.spigot().sendMessage(message);
            return true;
        }

        for (String member : clan.getMembers()){
            clanMember = Bukkit.getPlayer(member);
            if(clanMember != null) {
                clanMember.spigot().sendMessage(message);
                return true;
            }
        }
        return false;
    }

    public boolean getOnlineMember(Player player, Clan clan, String message){
        Player clanMember = Bukkit.getPlayer(clan.getOwner());
        if (clanMember != null) {
            clanMember.sendMessage(message);
            return true;
        }

        for (String member : clan.getMembers()){
            clanMember = Bukkit.getPlayer(member);
            if(clanMember != null) {
                clanMember.sendMessage(message);
                return true;
            }
        }
        return false;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1 && sender.hasPermission(this.getPermission())) {
            Player player = (Player) sender;
            Clan clan = this.clans.getClanByOwnerPlayer(player);

            if(clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if(clan != null && Loader.clan_battle.containsKey(clan.getName())){
                return Arrays.asList("accept", "deny");
            }
        }

        return this.clans.getClansTab();
    }
}
