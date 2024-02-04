package org.github.clansmanager.Admin;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.*;

import java.util.Arrays;
import java.util.List;

public class AdminCommands {

    public static void spyClanChat(String chat, Player player){
        createTable();
        DBManager database = Loader.db;
        if(!database.isConnected())
            database.connect();

        List<Object[]> rows = database.executeQuery("SELECT * FROM " + database.fixName("staff"));
        if(rows != null && rows.size() >= 0) {
            for (Object[] row : rows) {
                Player newMemberPlayer = Bukkit.getPlayer(((String) row[1]));
                if (newMemberPlayer != null)
                    if(!player.getName().equalsIgnoreCase(newMemberPlayer.getName()))
                        newMemberPlayer.sendMessage(chat);
            }
        }
        database.disconnect();
    }

    public static boolean enableSpyClanChat(Player player){
        createTable();
        DBManager database = Loader.db;
        if(!database.isConnected())
            database.connect();

        return database.execute("INSERT INTO " + database.fixName("staff") + " (staff_name) VALUES (?)", player.getName());
    }

    public static boolean disableSpyClanChat(Player player){
        createTable();
        DBManager database = Loader.db;
        if(!database.isConnected())
            database.connect();

        return database.execute("DELETE FROM " + database.fixName("staff") + " WHERE staff_name=?", player.getName());
    }

    private static void createTable(){
        DBManager database = Loader.db;
        database.connect();
        if (!database.tableExists(database.fixName("staff"))) {
            if(!database.isMysql()) {
                database.execute("CREATE TABLE IF NOT EXISTS " + database.fixName("staff") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "staff_name VARCHAR (255)," +
                        "UNIQUE (id)" +
                        ");");
            } else {
                database.execute("CREATE TABLE IF NOT EXISTS " + database.fixName("staff") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `staff_name` varchar(255) NOT NULL," +
                        ");");
                database.execute("ALTER TABLE " + database.fixName("staff") + " ADD PRIMARY KEY (`id`);");
                database.execute("ALTER TABLE " + database.fixName("staff") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;");
            }
        }
        database.disconnect();
    }

    public static boolean openClanChest(String clanName, Player player, CManager manager) {
        ClanChestManager clanChest = new ClanChestManager();
        Clan clan = manager.getClansByName(clanName);

        if (clan == null) {
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            manager.disconnect();
            return true;
        }

        if (clanChest.hasChest(clan)) {
            clanChest.createEmptyChest(clan, clanChest.calculateSlots(Utils.getPlayer(clan.getOwner())));
        }

        clanChest.openChest(clan, player);
        manager.disconnect();
        return true;
    }

    public static boolean clanBalance(String clanName, String[] args, Player player, CManager manager) {
        Clan clan = manager.getClansByName(clanName);

        if (clan == null) {
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            manager.disconnect();
            return true;
        }

        if(args.length == 1){
            player.sendMessage(Messages.withPrefix("success.clan-admin-bank-successful", "&eClan &c&l%clan%&e Balance: &a&l%balance%&e!")
                    .replace("%balance%", Loader.eco.format(clan.getBalance()))
                    .replace("%clan%", clan.getName()));
            manager.disconnect();
            return true;
        }

        switch (args[1]){
            case "take":
                return takeClanBalance(clan, Arrays.copyOfRange(args, 2, args.length), player, manager);
            case "set":
                return setClanBalance(clan, Arrays.copyOfRange(args, 2, args.length), player, manager);
            case "clear":
                return setClanBalance(clan, 0, player, manager);
            case "give":
                return giveClanBalance(clan, Arrays.copyOfRange(args, 2, args.length), player, manager);
            default:
                manager.disconnect();
                return false;
        }
    }

    private static boolean giveClanBalance(Clan clan, String[] args, Player player, CManager manager) {
        try {
            double amount = Double.parseDouble(Utils.getText(args));
            if(!clan.deposit(amount)){
                player.sendMessage(Messages.withPrefix("errors.clan-bank-admin-error-give", "&cUnable to give Money!"));
                manager.disconnect();
                return true;
            }

            player.sendMessage(Messages.withPrefix("success.clan-bank-successful-deposit", "&eYou deposit &a&l%amount%$&e and now clan have &a&l%balance%&e!")
                    .replace("%balance%", Loader.eco.format(clan.getBalance()))
                    .replace("%amount%", Loader.eco.format(amount)));
            manager.disconnect();
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.withPrefix("errors.clan-bank-invalid", "&cInvaild amount!"));
            manager.disconnect();
            return true;
        }
    }

    private static boolean setClanBalance(Clan clan, String[] args, Player player, CManager manager) {
        try {
            double amount = Double.parseDouble(Utils.getText(args));
            if(!clan.adminSetBalance(amount)){
                player.sendMessage(Messages.withPrefix("errors.clan-bank-admin-error", "&cUnable to set Clan Balance!"));
                manager.disconnect();
                return true;
            }

            player.sendMessage(Messages.withPrefix("success.clan-bank-admin-set", "&eYou set clan %clan% balance to &a&l%balance%&e!")
                    .replace("%balance%", Loader.eco.format(clan.getBalance()))
                    .replace("%clan%", clan.getName()));
            manager.disconnect();
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.withPrefix("errors.clan-bank-invalid", "&cInvaild amount!"));
            manager.disconnect();
            return true;
        }
    }
    private static boolean setClanBalance(Clan clan, double amount, Player player, CManager manager) {
        if(!clan.adminSetBalance(amount)){
            player.sendMessage(Messages.withPrefix("errors.clan-bank-admin-error", "&cUnable to set Clan Balance!"));
            manager.disconnect();
            return true;
        }

        player.sendMessage(Messages.withPrefix("success.clan-bank-admin-set", "&eYou set clan %clan% balance to &a&l%balance%&e!")
                .replace("%balance%", Loader.eco.format(clan.getBalance()))
                .replace("%clan%", clan.getName()));
        manager.disconnect();
        return true;
    }

    public static boolean takeClanBalance(Clan clan, String[] args, Player player, CManager manager){
        try {
            double amount = Double.parseDouble(Utils.getText(args));
            return withdrawClan(clan, amount, player, manager);
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.withPrefix("errors.clan-bank-invalid", "&cInvaild amount!"));
            manager.disconnect();
            return true;
        }

    }

    private static boolean withdrawClan(Clan clan, double amount, Player player, CManager manager) {
        if (amount > clan.getBalance()) {
            player.sendMessage(Messages.withPrefix("errors.clan-bank-no-amount-withdraw", "&cYour clan not have %amount%").replace("%amount%", Loader.eco.format(amount)));
            manager.disconnect();
            return true;
        }

        if(!clan.withdraw(amount)){
            player.sendMessage(Messages.withPrefix("errors.clan-bank-withdraw-error", "&cWithdrawal failed. Insufficient funds."));
            manager.disconnect();
            return true;
        }

        EconomyResponse r = Loader.eco.depositPlayer(player.getName(), amount);

        if(!r.transactionSuccess()) {
            player.sendMessage(Messages.withPrefix("errors.clan-bank-error", "&cAn error occured: %error%").replace("%error%", r.errorMessage));
            clan.deposit(amount);
            manager.disconnect();
            return true;
        }

        player.sendMessage(Messages.withPrefix("success.clan-bank-successful-withdraw", "&eYou withdraw &a&l%amount%$&e and now clan have &a&l%balance%&e!")
                .replace("%balance%", Loader.eco.format(clan.getBalance()))
                .replace("%amount%", Loader.eco.format(amount)));
        manager.disconnect();
        return true;

    }

    public static boolean setOwner(String clanName, String[] args, Player player, CManager manager){
        Clan clan = manager.getClansByName(clanName);

        if (clan == null) {
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            manager.disconnect();
            return true;
        }

        Player newOwnerPlayer = Bukkit.getPlayer(args[1]);

        if (newOwnerPlayer == null){
            player.sendMessage(Messages.withPrefix("errors.player-offline", "&cThis player is offline!"));
            manager.disconnect();
            return true;
        }

        if (clan.isOwner(newOwnerPlayer)) {
            player.sendMessage(Messages.withPrefix("errors.clan-owner", "&cYou are unable to give the clan to yourself!"));
            manager.disconnect();
            return true;
        }

        if(!manager.updateClanOwner(player, newOwnerPlayer)){
            player.sendMessage(Messages.withPrefix("errors.clan-owner-error", "&cUnsuccessful set new clan owner!"));
            manager.disconnect();
            return true;
        }

        newOwnerPlayer.sendMessage(Messages.withPrefix("success.clan-new-owner", "&eYou have been given ownership of the clan %clan_name% from %old_owner%").replace("%clan_name%", clan.getName()).replace("%old_owner%", clan.getOwner()));
        player.sendMessage(Messages.withPrefix("success.clan-owner-updated", "&aSuccessfully set new clan owner!"));
        manager.disconnect();
        return true;
    }
    public static boolean kickMember(String clanName, String[] args, Player player, CManager manager) {
        Clan clan = manager.getClansByName(clanName);

        if (clan == null) {
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            manager.disconnect();
            return true;
        }

        Player newMemberPlayer = Bukkit.getPlayer(args[1]);

        if (newMemberPlayer == null) {
            player.sendMessage(Messages.withPrefix("errors.player-offline", "&cThis player is offline!"));
            manager.disconnect();
            return true;
        }

        if (!clan.isMember(newMemberPlayer)) {
            player.sendMessage(Messages.withPrefix("errors.player-not-fount", "&cThis player is not clan member!"));
            manager.disconnect();
            return true;
        }

        clan = manager.kickClanMember(player, newMemberPlayer);

        if(clan == null){
            player.sendMessage(Messages.withPrefix("errors.clan-kick-unsuccess", "&eYour unsuccessful kick all player members!"));
            manager.disconnect();
            return true;
        }

        player.sendMessage(Messages.withPrefix("success.clan-kick-success", "&eYour successful kick player %player% from clan %clan_name%!").replace("%player%", newMemberPlayer.getName()).replace("%clan_name%", clan.getName()));
        newMemberPlayer.sendMessage(Messages.withPrefix("clan.kick-notify", "&eYour are kick from clan %clan_name%").replace("%clan_name%", clan.getName()));
        manager.disconnect();
        return true;

    }

    public static boolean removeClan(String clanName, Player player, CManager manager) {
        ClanChestManager clanChest = new ClanChestManager();
        Clan clan = manager.getClansByName(clanName);

        if (clan == null) {
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            manager.disconnect();
            return true;
        }

        clan = manager.removeClan(clanName);

        if(!clanChest.removeChest(clan) && clan == null){
            player.sendMessage(Messages.withPrefix("errors.clan-remove", "&cClan is not removed!"));
            manager.disconnect();
            return true;
        }

        player.sendMessage(Messages.withPrefix("success.clan-remove", "&aClan Successful removed!"));
        manager.disconnect();
        return true;
    }

    public static boolean lockClan(String clanName, Player player, CManager manager) {
        Clan clan = manager.getClansByName(clanName);

        if (clan == null) {
            player.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
            manager.disconnect();
            return true;
        }

        if(clan.isLock()){
            clan.setLock(false);
            player.sendMessage(Messages.withPrefix("clan.locked", "&aSuccessful Unlocked!"));
            return true;
        } else {
            clan.setLock(true);
            player.sendMessage(Messages.withPrefix("clan.unlock", "&aSuccessful Locked!"));
            return true;
        }
    }
}
