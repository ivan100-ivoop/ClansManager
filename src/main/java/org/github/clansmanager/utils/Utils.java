package org.github.clansmanager.utils;

import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");


    public static final String fixColors(String message) {
        return Utils.colorize(Utils.translateHexColorCodes(Utils.hexToMinecraftColor(message)));
    }

    private static final String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static final String translateHexColorCodes(String message) {
        Matcher matcher = Utils.HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x§" + group.charAt(0) + '§' + group.charAt(1) + '§' + group.charAt(2) + '§' + group.charAt(3) + '§' + group.charAt(4) + '§' + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

    public static String hexToMinecraftColor(String hexColor) {
        if (hexColor.startsWith("#") && hexColor.length() == 7) {
            StringBuilder converted = new StringBuilder("§x");
            for (char c : hexColor.substring(1).toCharArray()) {
                converted.append("§").append(c);
            }
            return converted.toString();
        }
        return hexColor;
    }

    public static void runCommand(CommandSender sender, String cmd) {
        Bukkit.getServer().dispatchCommand(sender, cmd);
    }

    public static String generateUniqueID() {
        String id = "";
        for (int i = 0; i < 10; i++) {
            id += (int) (Math.random() * 10);
        }
        return id;
    }

    public static String removeColors(String message) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static List<String> onlinePlayers(){
        List<String> player = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()){
            player.add(p.getName());
        }
        return player;
    }
    public static CachedDataManager getPlayer(String player) {
        UserManager userManager = Loader.getPlugin(Loader.class).api.getUserManager();
        User user = userManager.getUser(player);
        return user.getCachedData();
    }
    public static String getText(String ...args){
        StringBuilder output = new StringBuilder();
        for (String arg : args){
            output.append(arg).append(" ");
        }
        return output.toString();
    }

    public static String formatTimer(int seconds) {
        int minutes = seconds/60;
        seconds = seconds%60;

        String sMinutes = minutes + "";
        String sSeconds = seconds + "";

        if(minutes < 10) sMinutes = "0" + minutes;
        if(seconds < 10) sSeconds = "0" + seconds;

        return sMinutes + ":" + sSeconds;
    }

}
