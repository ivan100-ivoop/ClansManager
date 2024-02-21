package org.github.clansmanager.commands;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bank extends SubCommand {
    private CManager clans = null;
    private Economy economy;
    private EconomyResponse r;

    public Bank(){
        this.clans = new CManager();
        this.economy = Loader.eco;
    }
    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName() + " ";
    }

    @Override
    public String getDescription() {
        return Messages.onlyMessage("commands.bank", "Your Clan bank");
    }

    @Override
    public String getPermission() {
        return "clansmanager.bank";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName() + " <deposit, balance, withdraw> <amount> ";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Clan clan = null;


        if(sender instanceof Player) {
            Player player = (Player) sender;


            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("errors.not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            if(args.length < 1) {
                sender.sendMessage(Messages.withPrefix("errors.bank-method", "&cMethod are requerd!"));
                return false;
            }

            clan = this.clans.getClanByOwnerPlayer(player);

            if (clan == null)
                clan = this.clans.getClanByMemberPlayer(player);

            if(clan.isLock() && clan.isMember(player)){
                sender.sendMessage(Messages.withPrefix("errors.clan-locked", "&cOps Clan is Locked!"));
                return true;
            }

            if (clan == null) {
                sender.sendMessage(Messages.withPrefix("errors.clan-not-found", "&cClan not found!"));
                return true;
            }

            if(args[0].equalsIgnoreCase("deposit")) {

                if(args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("errors.bank-option-amount", "&cAmount are requerd!"));
                    return false;
                }

                try {
                    double amount = Double.parseDouble(Utils.getText(Arrays.copyOfRange(args, 1, args.length)));
                    return depositClan(clan, amount, player);
                } catch (NumberFormatException e) {
                    player.sendMessage(Messages.withPrefix("errors.bank-invalid-amount", "&cInvaild amount!"));
                    return true;
                }
            }

            if(args[0].equalsIgnoreCase("withdraw")) {

                if(args.length < 2) {
                    sender.sendMessage(Messages.withPrefix("errors.bank-option-amount", "&cAmount are requerd!"));
                    return false;
                }

                try {
                    double amount = Double.parseDouble(Utils.getText(Arrays.copyOfRange(args, 1, args.length)));
                    return withdrawClan(clan, amount, player);
                } catch (NumberFormatException e) {
                    player.sendMessage(Messages.withPrefix("errors.bank-invalid-amount", "&cInvaild amount!"));
                    return true;
                }
            }

            if(args[0].equalsIgnoreCase("balance")) {
                player.sendMessage(Messages.withPrefix("success.bank-balance", "&eYou clan Balance: &a&l%balance%&e!")
                        .replace("%balance%", economy.format(clan.getBalance())));
                return true;
            }

        }
        sender.sendMessage(Messages.onlyMessage("errors.player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    private boolean withdrawClan(Clan clan, double amount, Player player) {
        if (amount > clan.getBalance()) {
            player.sendMessage(Messages.withPrefix("errors.bank-withdraw-amount", "&cYour clan not have %amount%").replace("%amount%", economy.format(amount)));
            return true;
        }

        if(!clan.withdraw(amount)){
            player.sendMessage(Messages.withPrefix("errors.bank-withdraw-error", "&cWithdrawal failed. Insufficient funds."));
            return true;
        }

        r = economy.depositPlayer(player.getName(), amount);

        if(!r.transactionSuccess()) {
            player.sendMessage(Messages.withPrefix("errors.bank-error", "&cAn error occured: %error%").replace("%error%", r.errorMessage));
            clan.deposit(amount);
            return true;
        }

        player.sendMessage(Messages.withPrefix("success.bank-withdraw", "&eYou withdraw &a&l%amount%$&e and now clan have &a&l%balance%&e!")
                .replace("%balance%", economy.format(clan.getBalance()))
                .replace("%amount%", economy.format(amount)));
        return true;

    }

    private boolean depositClan(Clan clan, double amount, Player player) {

            if (amount > economy.getBalance(player.getName())) {
                player.sendMessage(Messages.withPrefix("errors.bank-deposit-amount", "&cYou not have %amount%").replace("%amount%", economy.format(amount)));
                return true;
            }

            if(!clan.deposit(amount)){
                player.sendMessage(Messages.withPrefix("errors.bank-deposit-error", "&cDeposit failed. Insufficient funds."));
                return true;
            }

            r = economy.withdrawPlayer(player.getName(), amount);

            if(!r.transactionSuccess()) {
                player.sendMessage(Messages.withPrefix("errors.bank-error", "&cAn error occured: %error%").replace("%error%", r.errorMessage));
                clan.withdraw(amount);
                return true;
            }

        player.sendMessage(Messages.withPrefix("success.bank-deposit", "&eYou deposit &a&l%amount%$&e and now clan have &a&l%balance%&e!")
                    .replace("%balance%", economy.format(clan.getBalance()))
                    .replace("%amount%", economy.format(amount)));
            return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1 && sender.hasPermission(getPermission())){
            return Arrays.asList("deposit", "balance", "withdraw");
        }

        return Collections.emptyList();
    }
}
