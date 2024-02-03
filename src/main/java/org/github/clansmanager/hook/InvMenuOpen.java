package org.github.clansmanager.hook;

import com.sun.imageio.plugins.tiff.TIFFExifJPEGCompressor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.github.clansmanager.CManager;
import org.github.clansmanager.Loader;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.ClanChestManager;
import org.github.clansmanager.utils.Messages;
import org.github.clansmanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvMenuOpen  implements Listener {

    private String name;
    private CManager clans = null;
    private int size;
    private onClick click;
    List<String> viewing = new ArrayList<String>();

    private Player view_player = null;

    private ItemStack[] items;

    private ClanChestManager ches;
    private File rewardsChest;

    public InvMenuOpen(String name, int size, onClick click) {
        this.name = name;
        this.size = size * 9;
        this.items = new ItemStack[this.size];
        this.click = click;
        this.ches = new ClanChestManager();
        this.clans = new CManager();
        Bukkit.getPluginManager().registerEvents(this, Loader.instance);
    }

    public InvMenuOpen(){
        this.ches = new ClanChestManager();
        this.clans = new CManager();
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
    }

    public InvMenuOpen(File rewardsChest){
        this.ches = new ClanChestManager();
        this.rewardsChest = rewardsChest;
        this.clans = new CManager();
        Bukkit.getPluginManager().registerEvents(this, Loader.instance);
    }

    public InvMenuOpen setName(String name){
        this.name = name;
        return this;
    }

    public InvMenuOpen setPlayer(Player player){
        this.view_player = player;
        return this;
    }

    public InvMenuOpen setSize(int size){
        this.size = size * 9;
        this.items = new ItemStack[this.size];
        return this;
    }

    public InvMenuOpen onClick(onClick click){
        this.click = click;
        return this;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player p : this.getViewers())
            close(p);
    }

    public InvMenuOpen open(Player p) {
        p.openInventory(getInventory(p));
        viewing.add(p.getName());
        return this;
    }

    public InvMenuOpen open() {
        if(this.view_player != null) {
            this.view_player.openInventory(getInventory(this.view_player));
            viewing.add(this.view_player.getName());
        }
        return this;
    }

    private Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, size, name);
        inv.setContents(this.items);
        return inv;
    }

    public InvMenuOpen close(Player p) {
        if (p.getOpenInventory().getTitle().equals(name))
            p.closeInventory();
        return this;
    }

    public List<Player> getViewers() {
        List<Player> viewers = new ArrayList<Player>();
        for (String s : viewing)
            viewers.add(Bukkit.getPlayer(s));
        return viewers;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (viewing.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            if(event.getSlot() >= 0) {
                Row row = getRowFromSlot(event.getSlot());
                if (!click.click(p, this, row, event.getSlot() - row.getRow() * 9, event.getCurrentItem()))
                    close(p);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (viewing.contains(event.getPlayer().getName()))
            viewing.remove(event.getPlayer().getName());

        if(event != null && this.clans.isMemberOrOwner(((Player) event.getPlayer()))){
            Clan clan = this.clans.getClanByOwnerPlayer(((Player) event.getPlayer()));
            if(clan == null)
                clan = this.clans.getClanByMemberPlayer(((Player) event.getPlayer()));

            if(clan != null)
                if(event.getInventory().getViewers().get(0).getOpenInventory().getTitle().contains(clan.getName())){
                    ches.setDir(rewardsChest);
                    ches.updateCheste(event.getInventory().getContents(), clan);
                }
        }
    }


    public InvMenuOpen addButton(Row row, int position, ItemStack item, String name, String... lore) {
        items[row.getRow() * 9 + position] = getItem(item, name, lore);
        return this;
    }

    public InvMenuOpen addButton(Row row, int position, ItemStack item, String name, String id, String... lore) {
        items[row.getRow() * 9 + position] = getItem(item, name, id, lore);
        return this;
    }

    public Row getRowFromSlot(int slot) {
        return new Row(slot / 9, items);
    }

    public Row getRow(int row) {
        return new Row(row, items);
    }

    public interface onClick {
        public abstract boolean click(Player clicker, InvMenuOpen menu, Row row, int slot, ItemStack item);
    }

    public class Row {
        private ItemStack[] rowItems = new ItemStack[9];
        int row;

        public Row(int row, ItemStack[] items) {
            this.row = row;
            int j = 0;
            for (int i = (row * 9); i < (row * 9) + 9; i++) {
                rowItems[j] = items[i];
                j++;
            }
        }

        public ItemStack[] getRowItems() {
            return rowItems;
        }

        public ItemStack getRowItem(int item) {
            return rowItems[item] == null ? new ItemStack(Material.AIR) : rowItems[item];
        }

        public int getRow() {
            return row;
        }
    }

    private ItemStack getItem(ItemStack item, String name, String... lores) {
        List<String> _lore = new ArrayList<>();
        ItemMeta im = item.getItemMeta();

        if(this.view_player != null) {
            im.setDisplayName(Utils.fixColors(PlaceholderAPI.setPlaceholders(this.view_player, name)));
        } else {
            im.setDisplayName(Utils.fixColors(name));
        }

        for (String lore: lores){
            if(this.view_player != null){
                _lore.add(PlaceholderAPI.setPlaceholders(this.view_player, lore));
            } else {
                _lore.add(lore);
            }
        }

        im.setLore(_lore);
        item.setItemMeta(im);

        return item;
    }

    private ItemStack getItem(ItemStack item, String name, String id, String... lores) {
        List<String> _lore = new ArrayList<>();
        ItemMeta im = item.getItemMeta();

        if(this.view_player != null) {
            im.setDisplayName(Utils.fixColors(PlaceholderAPI.setPlaceholders(this.view_player, name)));
        } else {
            im.setDisplayName(Utils.fixColors(name));
        }

        for (String lore: lores){
            if(this.view_player != null){
                _lore.add(PlaceholderAPI.setPlaceholders(this.view_player, lore));
            } else {
                _lore.add(lore);
            }
        }

        im.setCustomModelData(Integer.parseInt(id));

        im.setLore(_lore);
        item.setItemMeta(im);
        return item;
    }

}