package org.github.clansmanager.game;

import org.bukkit.scheduler.BukkitRunnable;
import org.github.clansmanager.Loader;

public class BattleUpdate extends BukkitRunnable {
    @Override
    public void run() {
        if(Loader.games != null) {
            Loader.games.forEach(gamesInstance -> {
                if (gamesInstance != null)
                    gamesInstance.onUpdate();
            });
        }
    }
}

