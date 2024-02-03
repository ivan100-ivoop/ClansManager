package org.github.clansmanager.game.container;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.PlayerInventory;
import org.github.clansmanager.Loader;
import org.github.clansmanager.game.Arena;
import org.github.clansmanager.game.GameState;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.Utils;
import sun.nio.ch.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamesInstance {

    private boolean isTeleported = false;

    private GameState state = GameState.STARTING;

    private final int DEFAULT_START_TIME = 5;
    private final int END_TIME_GAME = 10;

    private int startMessage = 3;

    private Location clan1 = Arena.getSpawn1();
    private Location clan2 = Arena.getSpawn2();

    private List<Player> clanRequred = new ArrayList<>();
    private List<Player> clanAccept = new ArrayList<>();
    Clan accept, requred;

    private int startTimer = DEFAULT_START_TIME;
    private int endTimer = 0;
    private Player owner_temp;

    public GamesInstance(Clan request, Clan accept) {
        this.requred = request;
        this.accept = accept;

        owner_temp = getPlayer(request.getOwner());
        if (owner_temp != null)
            clanRequred.add(owner_temp);

        for (String player : request.getMembers()) {
            Player newMemberPlayer = Bukkit.getPlayer(player);
            if (newMemberPlayer != null) {
                clanRequred.add(newMemberPlayer);
            }
        }

        owner_temp = getPlayer(accept.getOwner());
        if (owner_temp != null)
            clanAccept.add(owner_temp);

        for (String player : accept.getMembers()) {
            Player newMemberPlayer = Bukkit.getPlayer(player);
            if (newMemberPlayer != null) {
                clanAccept.add(newMemberPlayer);
            }
        }

    }
    private Player getPlayer(String player){
        Player p = Bukkit.getPlayer(player);
        if(p != null)
            return p;
        return null;
    }

    private void setTimer(Player player, int timer, String msg){
        if (player != null) {
            String _msg = String.format(msg, Utils.formatTimer(timer));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Utils.fixColors(_msg)));
        }
    }

    private void resetActionBar(Player player){
        if (player != null)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RESET + ""));
    }

    private void resetTitle(Player player){
        if (player != null)
            player.sendTitle(ChatColor.RESET + "", ChatColor.RESET + "");
    }

    private void sendMessage(Player player, String msg) {
        if(player != null)
            player.sendMessage(Utils.fixColors(msg));
    }

    private void sendTitle(Player player, String msg) {
        if (player != null)
            player.sendTitle(Utils.fixColors(msg), "");
    }

    private void notifyAllClans() {

        for (Player p : clanAccept) {
            this.sendMessage(p, String.format("&eYour clan accept clan battle with %s!", this.requred.getName()));
        }

        for (Player p : clanRequred) {
            this.sendMessage(p, String.format("&eClan %s accept you require for battle!", this.accept.getName()));
        }
        this.state = GameState.PREPARE;
    }

    private void notifyAllClansTimeLeft(){
        for (Player p : clanAccept) {
            this.setTimer(p, this.startTimer, "&eStarting at: %s");
        }

        for (Player p : clanRequred) {
            this.setTimer(p, this.startTimer, "&eStarting at: %s");
        }

        this.startTimer--;
    }

    private void teleportClear(){
        if(!this.isTeleported){
            this.resetAll();

            for (Player p : clanAccept) {
                teleportPlayer(p, clan1);
                setInvertory(p);
            }

            for (Player p : clanRequred) {
                teleportPlayer(p, clan2);
                setInvertory(p);
            }
            this.isTeleported = true;
        }
    }

    private void showPlayerStart(){
        this.teleportClear();
        if(this.startMessage == 0){

            for (Player p : clanAccept) {
                this.sendTitle(p, String.format("&b%s", "GO"));
            }

            for (Player p : clanRequred) {
                this.sendTitle(p, String.format("&b%s", "GO"));
            }

            this.state = GameState.START;
        } else {

            for (Player p : clanAccept) {
                this.sendTitle(p, String.format("&b%s", String.valueOf(this.startMessage)));
            }

            for (Player p : clanRequred) {
                this.sendTitle(p, String.format("&b%s", String.valueOf(this.startMessage)));

            }
            this.startMessage = (startMessage - 1);
        }
    }
    private void resetAll() {
        for (Player p : clanAccept) {
            this.resetTitle(p);
            this.resetActionBar(p);
        }

        for (Player p : clanRequred) {
            this.resetTitle(p);
            this.resetActionBar(p);
        }
    }

    private void startGame(){
        this.startTimer = DEFAULT_START_TIME;
        this.state = GameState.PROGRESS;
        this.resetAll();
    }

    private void notifyAllInClans(Player p, Clan dead) {

        for (Player member : clanRequred){
            if(member != null){
                this.sendMessage(member, String.format("&c%s &edead from clan %s", p.getName(), dead.getName()));
            }
        }

        for (Player member : clanAccept){
            if(member != null){
                this.sendMessage(member, String.format("&c%s &edead from clan %s", p.getName(), dead.getName()));
            }
        }
    }

    public void onUpdate() {
            // update every 1 tick
            if (this.state == GameState.STARTING) {
                this.notifyAllClans();
            }

            if (this.state == GameState.START) {
                if (this.startTimer <= 0) {
                    this.startGame();
                }
            }

        }

        private void teleportPlayer(Player player, Location location) {
           if (player != null && location != null) {
               player.teleport(location);
               player.setFallDistance(0);
           }

        }


    public void update(){
        // update ever 20 tick is 1 sec

        if(this.state == GameState.PREPARE){
            if(this.startTimer <= 0){
                this.showPlayerStart();
            } else {
                this.notifyAllClansTimeLeft();
            }
        }

        if(this.state == GameState.PROGRESS){
            if(this.endTimer >= END_TIME_GAME){
                this.endGame();
            } else {
                this.updateGameTimer();
            }
        }

        if(this.state == GameState.END){
            Iterator<GamesInstance> games = Loader.games.iterator();
            while (games.hasNext()) {
                GamesInstance game = games.next();
                if(game.state == GameState.END){
                    games.remove();
                }
            }

        }

    }

    private void updateGameTimer() {
        this.endTimer++;
        int leftTime = (END_TIME_GAME - endTimer);
        for (Player p : clanRequred){
            this.setTimer(p, leftTime, "&eEnd after: %s");
        }

        for (Player p : clanAccept){
            this.setTimer(p, leftTime, "&eEnd after: %s");
        }
    }

    private void endGame() {
        this.endTimer = 0;
        this.calculateWinners();
        this.resetAll();

        Iterator<Player> iteratorAccept = clanAccept.iterator();
        while (iteratorAccept.hasNext()) {
            Player p = iteratorAccept.next();
            teleportPlayer(p, accept.getLocation());
        }

        Iterator<Player> iteratorRequred = clanRequred.iterator();
        while (iteratorRequred.hasNext()) {
            Player p = iteratorRequred.next();
            teleportPlayer(p, requred.getLocation());
        }
        this.state = GameState.END;
    }

    private void removePlayer(Player player, Clan clan){
        Iterator<Player> iteratorAccept = clanAccept.iterator();
        while (iteratorAccept.hasNext()) {
            Player p = iteratorAccept.next();
            if(p.equals(player)) {
                notifyAllInClans(player, clan);
                iteratorAccept.remove();
            }
        }

        Iterator<Player> iteratorRequred = clanRequred.iterator();
        while (iteratorRequred.hasNext()) {
            Player p = iteratorRequred.next();
            if(p.equals(player)) {
                notifyAllInClans(player, clan);
                iteratorRequred.remove();
            }
        }
    }

    private void setInvertory(Player player){
        player.getInventory().clear();
        player.getInventory().setContents(Arena.getInv());
    }

    private void calculateWinners() {
        int clan1left = 0;
        int clan2left = 0;

        if(clanRequred.size() > 0){
            clan1left = clanRequred.size();
        }

        if(clanAccept.size() > 0){
            clan2left = clanAccept.size();
        }

        if(clan1left > clan2left){
            win(requred);
        } else if(clan2left > clan1left){
            win(accept);
        } else if(clan2left == clan1left){
            Iterator<Player> iteratorAccept = clanAccept.iterator();
            while (iteratorAccept.hasNext()) {
                Player p = iteratorAccept.next();
                sendTitle(p, "&cRAW");
            }

            Iterator<Player> iteratorRequred = clanRequred.iterator();
            while (iteratorRequred.hasNext()) {
                Player p = iteratorRequred.next();
                sendTitle(p, "&cRAW");
            }
        }

    }

    private void win(Clan clan){
        String msg = String.format("&aClan %s win!", clan.getName());

        Iterator<Player> iteratorAccept = clanAccept.iterator();
        while (iteratorAccept.hasNext()) {
            Player p = iteratorAccept.next();
            sendMessage(p, msg);
        }

        Iterator<Player> iteratorRequred = clanRequred.iterator();
        while (iteratorRequred.hasNext()) {
            Player p = iteratorRequred.next();
            sendMessage(p, msg);
        }

        giveRewards(clan);

    }
    public boolean hasPlayer(Player entity) {
        if(clanAccept.contains(entity)){
            removePlayer(entity, accept);
            return true;
        }

        if(clanRequred.contains(entity)){
            removePlayer(entity, accept);
            return true;
        }

        return false;
    }

    private void giveRewards(Clan clan) {


    }
}
