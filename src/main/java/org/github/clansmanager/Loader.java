package org.github.clansmanager;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.github.clansmanager.game.BattleUpdate;
import org.github.clansmanager.game.GameUpdate;
import org.github.clansmanager.game.container.GamesInstance;
import org.github.clansmanager.hook.InvMenuOpen;
import org.github.clansmanager.hook.PlayerDeathListener;
import org.github.clansmanager.placeholder.AllClansManager;
import org.github.clansmanager.placeholder.ClansManagerAPI;
import org.github.clansmanager.placeholder.ClansTop;
import org.github.clansmanager.utils.CommandsLoader;
import org.github.clansmanager.utils.DBManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Loader extends JavaPlugin {
    public static CommandsLoader cmd = null;
    public static Economy eco = null;
    public static DBManager db = null;
    public static Logger logger = Logger.getLogger("ClanManager");
    public static File rewardsChest = null;
    public static Loader instance;
    public static LuckPerms api = null;
    public static InvMenuOpen inv = null;

    public static List<GamesInstance> games = new ArrayList<>();
    @Override
    public void onEnable() {

        if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            logger.log(Level.SEVERE, "Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            logger.log(Level.SEVERE, "Could not find LuckPerms! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (!setupEconomy() ) {
            logger.log(Level.SEVERE, "Could not find Vault! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        api = provider.getProvider();
        instance = this;

        saveDefaultConfig();
        this.rewardsChest = new File(this.getDataFolder(), "ClansChest");

        if (!this.rewardsChest.exists())
            this.rewardsChest.mkdir();

        this.db = new DBManager(this);
        this.cmd = new CommandsLoader();

        getCommand("clan").setExecutor(this.cmd);
        getCommand("clan").setTabCompleter(this.cmd);

        new ClansManagerAPI().register();
        new AllClansManager().register();
        new ClansTop().register();
        this.inv = new InvMenuOpen(this.rewardsChest);

        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        new BattleUpdate().runTaskTimer(this, 1, 20);
        new GameUpdate().runTaskTimer(this, 20, 20);
        logger.log(Level.INFO, "Successful enabled!");
    }

    @Override
    public void onDisable() {
        this.db.disconnect();
        logger.log(Level.INFO, "Successful disabled!");
    }

    public boolean reloadPlugin() {
        reloadConfig();
        return true;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }
}
