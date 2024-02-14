package org.github.clansmanager;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.github.clansmanager.game.Arena;
import org.github.clansmanager.game.ClanBattleGame;
import org.github.clansmanager.hook.InvMenuOpen;
import org.github.clansmanager.hook.PlayerDeathListener;
import org.github.clansmanager.placeholder.AllClansManager;
import org.github.clansmanager.placeholder.ClansManagerAPI;
import org.github.clansmanager.placeholder.ClansTop;
import org.github.clansmanager.utils.*;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Loader extends JavaPlugin {
    public static CommandsLoader cmd = null;
    public static Economy eco = null;
    public static DBManager db = null;
    public static Logger logger = Logger.getLogger("ClanManager");
    public static File rewardsChest = null, saveInv = null, dataDir = null;
    public static Loader instance;
    public static LuckPerms api = null;
    public static InvMenuOpen inv = null;
    public static Map<String, Clan> clan_battle = new HashMap<>();

    private ClansManagerAPI clansManagerAPI;
    private AllClansManager allClansManager;
    private ClansTop clansTop;
    private Update updater = null;
    public static String tempId = null;

    public static Map<String, Arena> arenas = new HashMap<>();
    public static List<ClanBattleGame> games = new ArrayList<>();
    @Override
    public void onEnable() {
        this.dataDir = new File(getDataFolder(), "utils");
        this.init();
        this.updater.start();

        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this.updater), this);
        logger.log(Level.INFO, "Successful enabled!");
    }

    @Override
    public void onDisable() {
        this.restoreInv();
        this.db.disconnectDB();
        logger.log(Level.INFO, "Successful disabled!");
    }

    public boolean reloadPlugin() {
        this.updater.stop();
        Iterator<ClanBattleGame> iterator = Loader.games.iterator();
        while (iterator.hasNext()) {
            ClanBattleGame game = iterator.next();
            game.forceEnd();
            iterator.remove();
        }

        this.restoreInv();

        clansManagerAPI.unregister();
        allClansManager.unregister();
        clansTop.unregister();
        reloadConfig();
        this.registerPlugins();
        this.cmd.reload();
        clansManagerAPI.register();
        allClansManager.register();
        clansTop.register();
        this.updater.start();
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

    private void registerPlugins(){
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
    }

    private void init(){

        this.registerPlugins();
        instance = this;

        saveDefaultConfig();
        this.rewardsChest = new File(this.dataDir, "ClansChest");
        this.saveInv = new File(this.dataDir, "saveInv");

        if (!this.dataDir.exists()) { this.dataDir.mkdir(); }
        if (!this.rewardsChest.exists()) { this.rewardsChest.mkdir(); }
        if (!this.saveInv.exists()) { this.saveInv.mkdir(); }


        this.db = new DBManager(this);
        this.cmd = new CommandsLoader();

        getCommand("clan").setExecutor(this.cmd);
        getCommand("clan").setTabCompleter(this.cmd);

        clansManagerAPI = new ClansManagerAPI();
        allClansManager = new AllClansManager();
        clansTop = new ClansTop();
        this.inv = new InvMenuOpen();

        clansManagerAPI.register();
        allClansManager.register();
        clansTop.register();

        updater = new Update();
        updater.consoleCheck();

        Arena.LoadArenas();

        this.restoreInv();
    }


    public void restoreInv() {
        File[] list = this.saveInv.listFiles();
        for (File file : list) {
            if (file.getAbsolutePath().endsWith(".yml")) {
                String playerName = file.getName().substring(0, file.getName().indexOf('.'));

                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    Utils.restoreInv(player);
                    Clan clan = new CManager().getClanByOwnerPlayer(player);
                    if (clan == null) {
                        clan = new CManager().getClanByMemberPlayer(player);
                    }

                    if (clan != null) {
                        player.teleport(clan.getLocation());
                    }
                }
            }
        }
    }


}
