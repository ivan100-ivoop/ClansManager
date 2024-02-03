package org.github.clansmanager.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.hook.InvMenuOpen;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.SubCommand;
import org.github.clansmanager.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClanList extends SubCommand {
    private CManager clans = null;
    private ConfigurationSection shem = null;
    private Loader plugin = null;
    public ClanList(){
        this.plugin = Loader.getPlugin(Loader.class);
        this.shem = this.plugin.getConfig().getConfigurationSection("clan-view");
        this.clans = new CManager();
    }
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String asUsageCommand() {
        return "/clan " + getName();
    }

    @Override
    public String getDescription() {
        return "Get list of all Clans!";
    }

    @Override
    public String getPermission() {
        return "clansmanager.list";
    }

    @Override
    public String getUsage() {
        return "/clan " + getName();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        int index = 0;
        int row = 0;

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(this.getPermission()) && !player.isOp()) {
                player.sendMessage(Messages.withPrefix("not-permission", "&4Your do not have permission to run this command!"));
                return true;
            }

            InvMenuOpen inv = this.plugin.inv.setName(Utils.fixColors(this.shem.getString("menu-title", "&7&lClans")));
            inv.setSize(6);
            inv.setPlayer(player);

            List<Clan> _clans = this.clans.getAllClans();

            for (Clan clan : _clans){
                ItemStack item = new ItemStack(Material.valueOf(this.shem.getString("type",  "STONE")), (clan.getRank() < 0 ? 1 : clan.getRank()));
                inv.addButton(inv.getRow(row), index, item, this.getTitle(index), String.valueOf(clan.getId()), this.getLores(index));

                if(index == 8) {
                    row++;
                    index = 0;
                } else {
                    index++;
                }
            }

            inv.onClick(new InvMenuOpen.onClick() {
                @Override
                public boolean click(Player p, InvMenuOpen menu, InvMenuOpen.Row row, int slot, ItemStack item) {
                    if(item != null && !item.getType().isAir()) {
                        List<Clan> _clans = clans.getClanById(item.getItemMeta().getCustomModelData());
                        if (_clans != null && _clans.size() >= 0) {
                            for (Clan _clan : _clans) {
                                if(_clan.isOwner(p)){
                                    p.teleport(_clan.getLocation());
                                    p.sendMessage(Messages.withPrefix("successful-clan-teleport", "&aYour have ben successful teleport to clan &b&l%clan_name%!").replace("%clan_name%", _clan.getName()));
                                }
                            }
                        }
                    }
                    return false;
                }
            });

            inv.open();

            return true;

        }
        sender.sendMessage(Messages.onlyMessage("player-only-command", "&cThis command can be run only from a player!", true));
        return true;
    }

    private String getTitle(int index) {
        String title = this.shem.getString("title", "%clans_prefix%");

        String[] placeholders = {"%clans_prefix%", "%clans_name%", "%clans_owner%", "%clans_members%", "%clans_members_count%"};

        for (String placeholder : placeholders) {
            if (title.contains(placeholder)) {
                title = this.fixPlaceHolder(title, placeholder, index);
            }
        }

        return Utils.fixColors(title);
    }

    private String[] getLores(int index) {
        List<String> lores = this.shem.getStringList("lore");
        List<String> output = new ArrayList<>();

        String[] placeholders = {"%clans_prefix%", "%clans_name%", "%clans_owner%", "%clans_members%", "%clans_members_count%"};

        for (String lore : lores) {
            for (String placeholder : placeholders) {
                if (lore.contains(placeholder)) {
                    lore = this.fixPlaceHolder(lore, placeholder, index);
                }
            }
            output.add(Utils.fixColors(lore));
        }

        return output.toArray(new String[0]);
    }

    public String fixPlaceHolder(String content, String placeholder, int index){
        return content.replace(placeholder, placeholder.substring(0, placeholder.length() - 1) + "_" + index + "%");
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
