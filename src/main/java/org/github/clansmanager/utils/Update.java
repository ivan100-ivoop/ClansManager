package org.github.clansmanager.utils;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import jdk.jpackage.internal.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.clansmanager.Loader;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class Update {
    private BukkitRunnable updateCheckTask;
    private long updateInterval = Loader.instance.getConfig().getLong("update-checker", 0);

    public Update(){
        if(updateInterval > 0) {
            updateCheckTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Map<String, String> update = getUpdate();
                    if (update.get("update").equalsIgnoreCase("yes")) {
                        Iterator player = Bukkit.getOnlinePlayers().iterator();
                        while (player.hasNext()) {
                            Player p = (Player) player.next();
                            if (p.hasPermission("clansmanager.update")) {
                                p.sendMessage(Utils.fixColors(getUpdateMessage(update)));
                            }
                        }
                    }
                }
            };
        }
    }

    public void start(){
        if(updateInterval > 0) {
            updateCheckTask.runTaskTimer(Loader.instance, 0L, 1200L * updateInterval);
        }
    }

    public void stop() {
        if(updateCheckTask != null){
            if(!updateCheckTask.isCancelled()) {
                updateCheckTask.cancel();
            }
        }
    }

    private String getUpdateMessage(Map<String, String> update) {
        return String.format(
                "&7&m==============&r &b&lClansManager Update &r&7&m==============%n" +
                        "&a&lHey there! &r&7You are using ClansManager &7%s%n" +
                        "&c&lWhoa! &r&7A &e&lnew version &ris available: &7%s%n" +
                        "&a&lDon't miss out! &r&7Get the &d&lupdate &rat: &7%s%n" +
                        "&7&m==============&r &b&lClansManager Update &r&7&m==============",
                Loader.instance.getDescription().getVersion(),
                update.get("version"),
                update.get("url")
        );
    }

    public Map<String, String> getUpdate() {
        Map<String, String> update = new HashMap<>();

        try {
            URL url = new URL("https://freedown.store/ClansManager.php");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoInput(true);
            connection.connect();
            connection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                byte[] buffer = ByteStreams.toByteArray(connection.getInputStream());
                String jsonString = new String(buffer);

                UpdatePreview response = new Gson().fromJson(jsonString, UpdatePreview.class);

                if(response.isHaveError()){
                    Loader.logger.log(Level.WARNING, String.format("Error: %s", response.getMessage()));

                    update.put("update", "no");
                    update.put("version", Loader.instance.getDescription().getVersion());
                    update.put("current_version", Loader.instance.getDescription().getVersion());
                    update.put("url", "https://github.com/ivan100-ivoop/ClansManager/releases/latest");

                    return update;
                } else {
                    String currentVersion = String.format("v%s",Loader.instance.getDescription().getVersion());
                    String latestVersion = response.getData().getTagName(currentVersion);
                    String updateURL = response.getData().getHtmlUrl("https://github.com/ivan100-ivoop/ClansManager/releases/latest");

                    update.put("update", currentVersion.equalsIgnoreCase(latestVersion) ? "no" : "yes");
                    update.put("version", latestVersion);
                    update.put("current_version", currentVersion);
                    update.put("url", updateURL);

                    if(updateCheckTask != null){
                        if(!updateCheckTask.isCancelled()) {
                            updateCheckTask.cancel();
                        }
                    }

                    connection.disconnect();
                    return update;
                }
            } else {
                Loader.logger.log(Level.WARNING, "Checking for updates failed...");
                update.put("update", "no");
                update.put("version", Loader.instance.getDescription().getVersion());
                update.put("current_version", Loader.instance.getDescription().getVersion());
                update.put("url", "https://github.com/ivan100-ivoop/ClansManager/releases/latest");

                return update;
            }
        } catch (Exception e) {
            e.printStackTrace();

            update.put("update", "no");
            update.put("current_version", String.format("v%s",Loader.instance.getDescription().getVersion()));
            update.put("version", String.format("v%s",Loader.instance.getDescription().getVersion()));
            update.put("url", "https://github.com/ivan100-ivoop/ClansManager");
        }

        return update;
    }

    public void consoleCheck() {
        Map<String, String> update = getUpdate();
        Loader.logger.log(Level.WARNING, "============== ClansManager Update ==============");
        Loader.logger.log(Level.WARNING, "Checking for updates...");
        if (update.containsKey("update") && update.get("update").equalsIgnoreCase("yes")) {
            Loader.logger.log(Level.WARNING, String.format("Hey there! You are using ClansManager %s", update.get("current_version")));
            Loader.logger.log(Level.WARNING, String.format("Whoa! A new version &ris available: %s", update.get("version")));
            Loader.logger.log(Level.WARNING, String.format("Don't miss out! Get the &d&lupdate at: %s", update.get("url")));
        } else {
            Loader.logger.log(Level.WARNING, "You are up-to-date!");
        }
        Loader.logger.log(Level.WARNING, "============== ClansManager Update ==============");
    }

    public void AdminJoin(Player player){
        Map<String, String> update = getUpdate();
        if (update.get("update").equalsIgnoreCase("yes")) {
            if (player.hasPermission("clansmanager.update")) {
                player.sendMessage(Utils.fixColors(getUpdateMessage(update)));
            }
        }
    }
}
