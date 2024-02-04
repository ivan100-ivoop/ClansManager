package org.github.clansmanager.hook;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.game.ClanBattleGame;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.ClanChestManager;
import org.github.clansmanager.utils.Messages;

import java.util.Iterator;

public class PlayerDeathListener implements Listener {
    private final CManager clans;
    private final ClanChestManager chest;

    public PlayerDeathListener() {
        this.chest = new ClanChestManager();
        this.clans = new CManager();
    }

    private void killDetect(Player victim, Player killer) {
        if (clans.isMemberOrOwner(killer)) {
            Clan killClan = clans.getClanByOwnerPlayer(killer);
            if (killClan == null)
                killClan = clans.getClanByMemberPlayer(killer);

            if (killClan != null) {
                int kills = killClan.getKills() + 1;
                killClan.setKills(kills);
                killClan.getDB().disconnect();
            }
        }

        if (clans.isMemberOrOwner(victim)) {
            Clan victimClan = clans.getClanByOwnerPlayer(victim);
            if (victimClan == null)
                victimClan = clans.getClanByMemberPlayer(victim);

            if (victimClan != null) {
                int death = victimClan.getDeath() + 1;
                victimClan.setDeath(death);
                victimClan.getDB().disconnect();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Loader.instance.restoreInv();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!Loader.games.isEmpty()) {
            Player player = event.getPlayer();
            Iterator<ClanBattleGame> iterator = Loader.games.iterator();
            while (iterator.hasNext()) {
                ClanBattleGame game = iterator.next();
                if (game.isBattlePlayer(player)) {
                    game.removePlayer(player);
                    player.sendMessage(Messages.withPrefix("clan.battle-remove", "&cYou are removed from the battle!"));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if(event.getEntity().getKiller() instanceof Player){
                Player killer = victim.getKiller();
                if (!Loader.games.isEmpty()) {
                    Iterator<ClanBattleGame> iterator = Loader.games.iterator();
                    while (iterator.hasNext()) {
                        ClanBattleGame game = iterator.next();
                        if (game.isBattlePlayer(victim)) {
                            game.playerDead(victim);
                            victim.sendMessage(Messages.withPrefix("clan.battle-remove", "&cYou are removed from the battle!"));
                            return;
                        } else {
                            if(!victim.hasPermission("clansmanager.bypass")) {
                                killDetect(victim, killer);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!Loader.games.isEmpty()) {
                Iterator<ClanBattleGame> iterator = Loader.games.iterator();
                while (iterator.hasNext()) {
                    ClanBattleGame game = iterator.next();
                    if (game.isBattlePlayer(player) && !game.isAllowPvP) {
                        if(!player.hasPermission("clansmanager.bypass")) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!Loader.games.isEmpty()) {
            Iterator<ClanBattleGame> iterator = Loader.games.iterator();
            while (iterator.hasNext()) {
                ClanBattleGame game = iterator.next();
                if (game.isBattlePlayer(player)) {
                    if(!player.hasPermission("clansmanager.bypass")) {
                        event.setCancelled(true);
                        player.sendMessage(Messages.withPrefix("errors.battle-command", "&cYou cannot use commands during a clan battle!"));
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!Loader.games.isEmpty()) {
            Iterator<ClanBattleGame> iterator = Loader.games.iterator();
            while (iterator.hasNext()) {
                ClanBattleGame game = iterator.next();
                if (game.isBattlePlayer(event.getPlayer()) && game.isDisableMove) {
                    if(!event.getPlayer().hasPermission("clansmanager.bypass")) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if(event != null && this.clans.isMemberOrOwner(((Player) event.getPlayer()))){
            Clan clan = this.clans.getClanByOwnerPlayer(((Player) event.getPlayer()));
            if(clan == null)
                clan = this.clans.getClanByMemberPlayer(((Player) event.getPlayer()));

            if(clan != null)
                if(event.getInventory().getViewers().get(0).getOpenInventory().getTitle().contains(clan.getName())){
                    chest.updateCheste(event.getInventory().getContents(), clan);
                }
            return;
        }
    }

}
