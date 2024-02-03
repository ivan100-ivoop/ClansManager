package org.github.clansmanager.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;

import java.util.ArrayList;
import java.util.List;

public class Clan {
    private String name = "";
    private String prefix = "";
    private Location loc = null;
    private int id = -1;
    private String owner = "";
    private List<String> members = new ArrayList<>();

    private CManager manager = null;

    private int rank = -1;
    private double balance = -1;

    private int kills = 0;
    private int death = 0;

    private boolean isLock = false;

    public Clan setRank(int rank){
        this.rank = rank;
        return this;
    }

    public Clan setKills(int kill){
        this.kills = kill;
        this.manager.updateKills(this);
        return this;
    }

    public Clan setDeath(int death){
        this.death = death;
        this.manager.updateDeaths(this);
        return this;
    }

    public boolean isLock(){
        return this.isLock;
    }

    public int getKills(){
        return this.kills;
    }

    public int getDeath(){
        return this.death;
    }
    public Clan setManager(CManager manager){
        this.manager = manager;
        return this;
    }

    public CManager getManager(){
        return this.manager;
    }

    public Clan setBalance(double bal){
        this.balance = bal;
        return this;
    }

    public Clan setMembers(List<String> players){
        this.members = players;
        return this;
    }

    public Clan setName(String name){
        this.name = name;
        return this;
    }

    public Clan setId(int id){
        this.id = id;
        return this;
    }

    public Clan setOwner(String owner){
        this.owner = owner;
        return this;
    }

    public Clan setPrefix(String prefix){
        this.prefix = prefix;
        return this;
    }

    public boolean isMember(Player player){
        if(this.members.size() >= 0){
            return this.members.contains(player.getName());
        }
        return false;
    }

    public CManager getDB(){
        return this.manager;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }

        double newBalance = ((balance <= 0) ? amount : Double.sum(balance, amount));

        if (newBalance >= 0) {
            setBalance(newBalance);
            return this.manager.updateBalance(this);
        }
        return false;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }

        if (amount <= balance) {
            double newBalance = balance - amount;
            setBalance(newBalance);
            return this.manager.updateBalance(this);
        } else {
            return false;
        }
    }

    public Clan setLock(boolean lock) {
        this.isLock = lock;
        this.manager.updateLock(this);
        return this;
    }

    public boolean isOwner(Player player){
        if(!this.owner.isEmpty())
            return this.owner.equalsIgnoreCase(player.getName());
        return false;
    }

    public Clan setLocation(Location loc){
        this.loc = loc;
        return this;
    }

    public List<String> getMembers(){
        return this.members;
    }

    public String getOwner(){
        return this.owner;
    }

    public String getName(){
        return this.name;
    }

    public String getPrefix(){
        return this.prefix;
    }

    public int getId(){
        return this.id;
    }

    public Location getLocation(){
        return this.loc;
    }



    public int getRank(){
        return this.rank;
    }

    public double getBalance(){
        if(this.balance < 0){
            return 0.00;
        }
        return this.balance;
    }

    public boolean adminSetBalance(double amount) {
        this.balance = amount;
        setBalance(amount);
        return this.manager.updateBalance(this);
    }
}
