package org.github.clansmanager.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;

public class ClansManagerAPI extends PlaceholderExpansion {

    private final CManager clans;

    public ClansManagerAPI() {
        this.clans = new CManager();
    }

    @Override
    public String getIdentifier() {
        return "clan";
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

        if (this.clans.isMemberOrOwner(player.getPlayer())) {
            Clan clan = this.clans.getClanByMemberPlayer(player.getPlayer());

            if (clan == null)
                clan = this.clans.getClanByOwnerPlayer(player.getPlayer());

            if (clan == null)
                return "";

            return placeSend(params, clan);
        }
        return "";
    }

    private String placeSend(String params,Clan clan){
        switch (params) {
            case "name":
                return clan.getName();
            case "prefix":
                if(clan.getPrefix() !=null && !clan.getPrefix().isEmpty() || !clan.getPrefix().equalsIgnoreCase("null")) {
                    return createPrefix(clan.getPrefix());
                }
                return " ";
            case "prefix_clear":
                if(clan.getPrefix() !=null && !clan.getPrefix().isEmpty() || !clan.getPrefix().equalsIgnoreCase("null")) {
                    return clan.getPrefix();
                }
                return " ";
            case "kills":
                return String.valueOf(clan.getKills());
            case "death":
                return String.valueOf(clan.getDeath());
            case "balance":
                return Loader.eco.format(clan.getBalance());
            case "owner":
                return clan.getOwner();
            case "members_count":
                return String.valueOf(clan.getMembers().size());
            case "members":
                StringBuilder allMembers = new StringBuilder();
                for (String member : clan.getMembers()) {
                    allMembers.append(member).append(" ");
                }
                return allMembers.toString().trim();
            default:
                return "";
        }
    }

    private String createPrefix(String prefix) {
        String present = Loader.getPlugin(Loader.class).getConfig().getString("clan-prefix-present", "%prefix%");
        return present.replace("%prefix%", prefix);
    }

}
