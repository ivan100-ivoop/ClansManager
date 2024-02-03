package org.github.clansmanager.placeholder;

import jdk.javadoc.internal.doclets.formats.html.HelpWriter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Utils;

import java.util.List;

public class ClansTop extends PlaceholderExpansion {

    private final CManager clans;

    public ClansTop() {
        this.clans = new CManager();
    }

    @Override
    public String getIdentifier() {
        return "clantop";
    }

    @Override
    public String getAuthor() {
        return "Ivan";
    }

    @Override
    public String getVersion() {
        return Loader.instance.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (player == null || !player.isOnline())
            return "";

        if(params.contains("kill")){
            return topKills(params);
        }

        if(params.contains("death")){
            return topDeaths(params);
        }

        if(params.contains("balance")){
            return topBalance(params);
        }

        return "";
    }

    private String topKills(String params) {
        List<Clan> _clans = this.clans.topKills();
        int id = this.getID(params);

        if (id < 0 || id >= _clans.size()) {
            return "";
        }

        return fixInfo(_clans.get(id), "kill");
    }

    private String topDeaths(String params) {
        List<Clan> _clans = this.clans.topDeath();
        int id = this.getID(params);

        if (id < 0 || id >= _clans.size()) {
            return "";
        }

        return fixInfo(_clans.get(id), "death");
    }

    private String topBalance(String params) {
        List<Clan> _clans = this.clans.topBalance();
        int id = this.getID(params);

        if (id < 0 || id >= _clans.size()) {
            return "";
        }

        return fixInfo(_clans.get(id), "balance");
    }


    public String fixInfo(Clan clan, String type){
        ConfigurationSection config = Loader.instance.getConfig();

        if(type.equalsIgnoreCase("kill")){
            return Utils.fixColors(config.getString("clan-top-kills-preset","{clan} &6&l{amount} &aKills")
                    .replace("{clan}", clan.getName())
                    .replace("{amount}", String.valueOf(clan.getKills()))
            );
        }

        if(type.equalsIgnoreCase("death")){
            return Utils.fixColors(config.getString("clan-top-death-preset","{clan} &6&l{amount} &cDeaths")
                    .replace("{clan}", clan.getName())
                    .replace("{amount}", String.valueOf(clan.getKills()))
            );
        }

        return Utils.fixColors(config.getString("clan-top-balance-preset","{clan} &6&l{amount}")
                .replace("{clan}", clan.getName())
                .replace("{amount}", Loader.eco.format(clan.getBalance()))
        );
    }

    private int getID(String params) {
        int lastIndex = params.lastIndexOf('_');

        if (lastIndex != -1 && lastIndex < params.length() - 1) {
            try {
                return (Integer.parseInt(params.substring(lastIndex + 1)) - 1);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

}
