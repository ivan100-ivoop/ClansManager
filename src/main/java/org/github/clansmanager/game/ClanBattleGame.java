package org.github.clansmanager.game;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClanBattleGame {

    private static final int START_DELAY_SECONDS = Loader.instance.getConfig().getInt("start-timer", 5);
    private static final int STARTING_DELAY_SECONDS = Loader.instance.getConfig().getInt("timer-starting", 3);
    private static final int GAME_DURATION_SECONDS = Loader.instance.getConfig().getInt("match-time", 1) * 60;

    private List<Player> clan1Players, clan2Players;

    private Arena arena = null;

    private BukkitRunnable startTimer, progressTimer, gameTimer;
    private Location clan1Location, clan2Location;
    private Clan clan1, clan2;

    private boolean isRunning = false;
    public boolean isDisableMove = false;
    public boolean isAllowPvP = false;
    private boolean isCommand = false;

    public ClanBattleGame(Clan clan1, Clan clan2) {
        this.arena = getRandomArena();

        this.clan1 = clan1;
        this.clan2 = clan2;

        this.clan1Players = new ArrayList<>(this.getClanPlayers(clan1));
        this.clan2Players = new ArrayList<>(this.getClanPlayers(clan2));

        this.clan1Location = clan1.getLocation();
        this.clan2Location = clan2.getLocation();
    }

    private Arena getRandomArena(){
        Arena __arena = null;
        for (Map.Entry<String, Arena> _arena : Loader.arenas.entrySet()){
            if(!_arena.getValue().IN_USE){
                __arena = _arena.getValue();
            }
        }
        __arena.IN_USE = true;
        return __arena;
    }

    public List<Player> getClanPlayers(Clan clan) {
        List<Player> players = new ArrayList<>();

        Player owner_temp = Bukkit.getPlayer(clan.getOwner());
        if(owner_temp != null)
            players.add(owner_temp);

        for (String player : clan.getMembers()) {
            Player newMemberPlayer = Bukkit.getPlayer(player);
            if (newMemberPlayer != null) {
                players.add(newMemberPlayer);
            }
        }
        return players;
    }

    public void startGame() {
        if (!isRunning) {
            isRunning = true;

            startTimer = new BukkitRunnable() {
                int seconds = START_DELAY_SECONDS;

                @Override
                public void run() {
                    if (seconds <= 0) {
                        cancel();
                        startProgress();
                    } else {
                        broadcastTimer(seconds, Messages.onlyMessage("battle.timer.staring", "&eStarting at: %s"));
                        seconds--;
                    }
                }
            };
            startTimer.runTaskTimer(Loader.instance, 0L, 20L);
        }
    }

    public void startProgress() {
        if (isRunning) {

            teleportPlayers(clan1Players, this.arena.SPAWN.get(0));
            teleportPlayers(clan2Players, this.arena.SPAWN.get(1));

            saveInv(clan1Players);
            saveInv(clan2Players);

            giveItems();
            resetAll();


            progressTimer = new BukkitRunnable() {
                int seconds = STARTING_DELAY_SECONDS;

                @Override
                public void run() {
                    if (seconds <= -1) {
                        cancel();
                        startBattle();
                    } else {
                        if(seconds == 0){
                            broadcastTitle(Messages.onlyMessage("battle.timer.go", "&bGO"));
                            isDisableMove = false;
                            seconds--;
                        } else {
                            broadcastTitle(Messages.onlyMessage("battle.timer.countdown", "&b%time%")
                                    .replace("%time%", String.valueOf(seconds))
                            );
                            isDisableMove = true;
                            seconds--;
                        }
                    }
                }
            };
            progressTimer.runTaskTimer(Loader.instance, 0L, 20L);
        }
    }

    private void startBattle() {
        resetAll();

        gameTimer = new BukkitRunnable() {
            int seconds = GAME_DURATION_SECONDS;

            @Override
            public void run() {
                if (seconds <= 0) {
                    cancel();
                    endGame();
                } else {
                    broadcastTimer(seconds, Messages.onlyMessage("battle.timer.end", "&eEnd after: %s"));
                    isAllowPvP = true;
                    gameLogic();
                    seconds--;
                }
            }
        };
        gameTimer.runTaskTimer(Loader.instance, 0L, 20L);
    }

    public void forceEnd(){
        isRunning = false;
        isAllowPvP = false;
        arena.IN_USE = false;
        gameTimer.cancel();
        resetAll();
        broadcastMessage(Messages.withPrefix("battle.force-end-battle", "&cAdmin has forced an end to all clan battles!"));
        teleportPlayers(clan1Players, clan1Location);
        teleportPlayers(clan2Players, clan2Location);
        restoreInv(clan1Players);
        restoreInv(clan2Players);
    }

    private void endGame() {
        isRunning = false;
        isAllowPvP = false;
        arena.IN_USE = false;
        resetAll();
        calculateWinners();
        teleportPlayers(clan1Players, clan1Location);
        teleportPlayers(clan2Players, clan2Location);
        restoreInv(clan1Players);
        restoreInv(clan2Players);
        Loader.games.remove(Loader.games.indexOf(this));
    }

    private void calculateWinners() {
        int clan1left = 0;
        int clan2left = 0;

        if(!clan1Players.isEmpty()){
            clan1left = clan1Players.size();
        }

        if(!clan2Players.isEmpty()){
            clan2left = clan2Players.size();
        }

        if(clan1left > clan2left){
            win(clan1);
        } else if(clan2left > clan1left){
            win(clan2);
        } else if(clan2left == clan1left){
            broadcastTitle(Messages.onlyMessage("battle-raw", "&cDRAW"));
        }

    }

    private void gameLogic() {
        if(clan1Players.isEmpty()) {
            gameTimer.cancel();
            endGame();
        }
        if(clan2Players.isEmpty()) {
            gameTimer.cancel();
            endGame();
        }
    }

    private void win(Clan winningClan) {
        String winMessage = String.format(Messages.withPrefix("battle-win", "&aClan %s wins the battle!"), winningClan.getName());
        notifyAll(winMessage);
        isCommand = Loader.instance.getConfig().isString("reward-commands");
        if(isCommand){
            for (Player winners : this.getClanPlayers(winningClan)){
                if(winners != null){
                    String command = Loader.instance.getConfig().getString("reward-commands", "");
                    if(!command.isEmpty()){
                        Utils.executeCommand(command
                                .replace("%clan_name%", winningClan.getName())
                                .replace("%clan_prefix%", winningClan.getPrefix())
                                .replace("%clan_owner%", winningClan.getOwner())
                                .replace("%player%", winners.getName())
                        );
                    }
                }
            }
        } else {
            for (Player winners : this.getClanPlayers(winningClan)){
                if(winners != null){
                    List<String> commands = Loader.instance.getConfig().getStringList("reward-commands");
                    for (String command : commands){
                        if(!command.isEmpty()){
                            Utils.executeCommand(command
                                    .replace("%clan_name%", winningClan.getName())
                                    .replace("%clan_prefix%", winningClan.getPrefix())
                                    .replace("%clan_owner%", winningClan.getOwner())
                                    .replace("%player%", winners.getName())
                            );
                        }
                    }
                }
            }
        }
        winningClan.rankUP();

    }

    private void broadcastMessage(String message) {
        for (Player player : clan1Players) {
            player.sendMessage(message);
        }

        for (Player player : clan2Players) {
            player.sendMessage(message);
        }
    }
    private void broadcastTimer(int time, String message) {
        for (Player player : clan1Players) {
            String _msg = String.format(message, Utils.formatTimer(time));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(_msg));
        }
        for (Player player : clan2Players) {
            String _msg = String.format(message, Utils.formatTimer(time));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(_msg));
        }

    }

    private void broadcastTitle(String time) {
        for (Player player : clan1Players) {
            player.sendTitle(String.format("%s", time), "");
        }
        for (Player player : clan2Players) {
            player.sendTitle(String.format("%s", time), "");
        }
    }

    private void teleportPlayers(List<Player> players, Location location) {
        for (Player player : players) {
            player.teleport(location);
        }
    }

    private void teleportPlayers(Player player, Location location) {
        player.teleport(location);
    }

    private void resetAll(){
        for (Player player : clan1Players) {
            player.sendTitle(net.md_5.bungee.api.ChatColor.RESET + "", net.md_5.bungee.api.ChatColor.RESET + "");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(net.md_5.bungee.api.ChatColor.RESET + ""));
        }
        for (Player player : clan2Players) {
            player.sendTitle(net.md_5.bungee.api.ChatColor.RESET + "", net.md_5.bungee.api.ChatColor.RESET + "");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(net.md_5.bungee.api.ChatColor.RESET + ""));
        }
    }

    private void restoreInv(List<Player> players) {
        for (Player player : players) {
            Utils.restoreInv(player);
        }
    }

    private void restoreInv(Player player) {
        Utils.restoreInv(player);
    }

    private void saveInv(List<Player> players) {
        for (Player player : players) {
            Utils.saveInv(player);
        }
    }

    private void giveItems() {
        for (Player player : clan1Players) {
            player.getInventory().setContents(this.arena.INVENTORY.get(0));
        }
        for (Player player : clan2Players) {
            player.getInventory().setContents(this.arena.INVENTORY.get(1));
        }
    }

    public boolean isBattlePlayer(Player player) {
        return clan1Players.contains(player) || clan2Players.contains(player);
    }

    private void handleElimination(Player player, String eliminationMessage) {
        Clan eliminatedFromClan = null;
        Clan eliminatedToClan = null;

        if (clan1Players.contains(player)) {
            eliminatedFromClan = clan2;
            eliminatedToClan = clan1;
            clan1Players.remove(player);
        } else if (clan2Players.contains(player)) {
            eliminatedFromClan = clan1;
            eliminatedToClan = clan2;
            clan2Players.remove(player);
        }

        if (eliminatedFromClan != null) {
            teleportPlayers(player, eliminatedToClan.getLocation());
            restoreInv(player);
            broadcastMessage(String.format(eliminationMessage, player.getName(), eliminatedFromClan.getName()));
        }
    }

    public void removePlayer(Player player) {
        handleElimination(player, Messages.withPrefix("battle.eliminated.leave", "&c%s has been eliminated from Clan %s during a leave!"));
    }

    private void notifyAll(String msg){
        for(Player p : this.getClanPlayers(clan1)){
            if(p != null)
                p.sendMessage(msg);
        }
        for(Player p : this.getClanPlayers(clan2)){
            if(p != null)
                p.sendMessage(msg);

        }
    }

    public void playerDead(Player victim) {
        handleElimination(victim, Messages.withPrefix("battle.eliminated.battle", "&c%s has been eliminated from Clan %s during the battle!"));
    }

    public boolean containsClan(Clan clan) {
        if(clan1.getName().equalsIgnoreCase(clan.getName()))
            return true;
        if(clan2.getName().equalsIgnoreCase(clan.getName()))
            return true;
        return false;
    }
}
