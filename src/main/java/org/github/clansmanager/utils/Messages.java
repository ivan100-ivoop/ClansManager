package org.github.clansmanager.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.github.clansmanager.Loader;

import java.io.File;

public class Messages {

    public static FileConfiguration getMessages(Loader plugin) {
        File langDir = new File(plugin.getDataFolder(), "lang/");
        File langFile = new File(langDir, plugin.getConfig().getString("lang", "en") + ".yml");

        if (!langDir.exists()) {
            langDir.mkdir();
        }

        if (!langFile.exists()) {
            plugin.saveResource("lang/en.yml", false);
        }

        return YamlConfiguration.loadConfiguration(langFile);
    }

    public static String withPrefix(String path, String def) {
        return Utils.fixColors(getMessages(Loader.instance).getString("prefix", "&6&lClanManager &7»&r") + " " +
                getMessages(Loader.instance).getString(path, def));
    }

    public static String onlyMessage(String path, String def, boolean withoutColors) {
        if (!withoutColors) {
            return Utils.removeColors(getMessages(Loader.instance).getString(path, def));
        }
        return Utils.fixColors(getMessages(Loader.instance).getString(path, def));
    }
    public static String onlyMessage(String path, String def) {
        return Utils.fixColors(getMessages(Loader.instance).getString(path, def));
    }
}
