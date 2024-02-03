package org.github.clansmanager.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;

import java.util.List;

public class AllClansManager extends PlaceholderExpansion {

    private final CManager clans;

    public AllClansManager() {
        this.clans = new CManager();
    }

    @Override
    public String getIdentifier() {
        return "clans";
    }

    @Override
    public String getAuthor() {
        return "Ivan";
    }

    @Override
    public String getVersion() {
        return Loader.getPlugin(Loader.class).getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (player == null || !player.isOnline())
            return "";

        return decodeClans(params, this.clans.getAllClans());
    }

    private int getID(String params) {
        System.out.println(params);
        int lastIndex = params.lastIndexOf('_');

        if (lastIndex != -1 && lastIndex < params.length() - 1) {
            try {
                return Integer.parseInt(params.substring(lastIndex + 1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }


    private String decodeClans(String params, List<Clan> clans) {

        if(clans == null)
            return "";

        int id = getID(params);

        if(id == -1)
            return "";

        if (id < 0 || id >= clans.size()) {
            return "";
        }

            Clan clan = clans.get(id);

            if (params.contains("name")) {
                return clan.getName();
            }

            if (params.contains("owner")) {
                return clan.getOwner();
            }

            if (params.contains("prefix")) {
                return clan.getPrefix();
            }

            if (params.contains("members")) {
                StringBuilder allMembers = new StringBuilder();
                for (String member : clan.getMembers()) {
                    allMembers.append(member).append(" ");
                }
                return allMembers.toString();
            }


            if (params.contains("members_count")) {
                return String.valueOf(clan.getMembers().size());
            }

        return "";
    }
}
