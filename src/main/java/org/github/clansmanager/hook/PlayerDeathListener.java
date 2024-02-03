package org.github.clansmanager.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;

import java.util.Locale;

public class PlayerDeathListener implements Listener {
    private CManager clans;

    public PlayerDeathListener(){
        this.clans = new CManager();
    }

    private void killDetect(PlayerDeathEvent event){
        if(event.getEntity() instanceof Player){
            Player victim = event.getEntity();
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                if (clans.isMemberOrOwner(killer)) {
                    Clan killClan = clans.getClanByOwnerPlayer(killer);
                    if (killClan == null)
                        killClan = clans.getClanByMemberPlayer(killer);

                    if (killClan != null) {
                        int kills = (killClan.getKills() + 1);
                        killClan.setKills(kills);
                        killClan.getDB().disconnect();
                    }
                }
                if (clans.isMemberOrOwner(victim)) {
                    Clan victimClan = clans.getClanByOwnerPlayer(victim);
                    if (victimClan == null)
                        victimClan = clans.getClanByMemberPlayer(victim);

                    if (victimClan != null) {
                        int death = (victimClan.getDeath() + 1);
                        victimClan.setDeath(death);
                        victimClan.getDB().disconnect();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(Loader.games != null) {
            Loader.games.forEach(gamesInstance -> {
                if (!gamesInstance.hasPlayer(event.getEntity())) {
                    killDetect(event);
                }
            });
        }
    }
}
