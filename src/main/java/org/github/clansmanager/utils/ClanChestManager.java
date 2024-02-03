package org.github.clansmanager.utils;

import net.luckperms.api.cacheddata.CachedDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.github.clansmanager.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClanChestManager {

    private static final int DEFAULT_MAX_SLOT = 9;
    private File dir;

    public ClanChestManager(){
        this.dir = Loader.rewardsChest;
    }
    public int calculateSlots(CachedDataManager userInfo) {
        int slot = DEFAULT_MAX_SLOT;
        String[] permissions = {"1", "2", "3", "4", "5", "6"};

        if(userInfo.getPermissionData().checkPermission("clansmanager.slots." + permissions[0]).asBoolean()){
            slot = Integer.parseInt(permissions[0]) * 9;
        }

        if(userInfo.getPermissionData().checkPermission("clansmanager.slots." + permissions[1]).asBoolean()){
            slot = Integer.parseInt(permissions[1]) * 9;
        }

        if(userInfo.getPermissionData().checkPermission("clansmanager.slots." + permissions[2]).asBoolean()){
            slot = Integer.parseInt(permissions[2]) * 9;
        }

        if(userInfo.getPermissionData().checkPermission("clansmanager.slots." + permissions[3]).asBoolean()){
            slot = Integer.parseInt(permissions[3]) * 9;
        }

        if(userInfo.getPermissionData().checkPermission("clansmanager.slots." + permissions[4]).asBoolean()){
            slot = Integer.parseInt(permissions[4]) * 9;
        }

        if(userInfo.getPermissionData().checkPermission("clansmanager.slots." + permissions[5]).asBoolean()){
            slot = Integer.parseInt(permissions[5]) * 9;
        }

        return slot;
    }

    private String getChestTitle(Clan clan) {
        return Messages.onlyMessage("clan-chest-title", "%clan_name% &7Chest", false)
                .replace("%clan_name%", clan.getName());
    }

    public boolean hasChest(Clan clan) {
        File chestFile = new File(this.dir, clan.getId() + ".yml");
        return chestFile.exists();
    }
    public boolean removeChest(Clan clan){
        File chestFile = new File(this.dir, clan.getId() + ".yml");
        if(chestFile.exists()){
            return chestFile.delete();
        }
        return true;
    }

    public boolean isFull(Clan clan){
        int maxSlot = calculateSlots(Utils.getPlayer(clan.getOwner()));

        File chestFile = new File(this.dir, clan.getId() + ".yml");
        if (!hasChest(clan)) {
            FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
            return chest.getList("content") != null && chest.getList("content").size() >= maxSlot;
        }

        return true;
    }

    public void createEmptyChest(Clan clan, int Slots) {
        if (!hasChest(clan)) {
            File chestFile = new File(this.dir, clan.getId() + ".yml");
            try {
                chestFile.createNewFile();
                FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
                chest.set("content", new ArrayList<ItemStack>());
                chest.save(chestFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean clearChest(Clan clan){
        if (!hasChest(clan)) {
            try {
                File chestFile = new File(this.dir, clan.getId() + ".yml");
                FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
                chest.set("content", new ArrayList<ItemStack>());
                chest.save(chestFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void openChest(Clan clan, Player player) {
        int slots = calculateSlots(Utils.getPlayer(clan.getOwner()));
        if (hasChest(clan)) {
            FileConfiguration chest = YamlConfiguration.loadConfiguration(new File(this.dir, clan.getId() + ".yml"));
            Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, getChestTitle(clan));
            if (chest.isList("content")) {
                ItemStack[] chestContents = chest.getList("content").toArray(new ItemStack[0]);
                inv.setContents(chestContents);
            }
            player.openInventory(inv);
        } else {
            createEmptyChest(clan, slots);
            openChest(clan, player);
        }
    }


    public boolean addItem(ItemStack item, Clan clan) {
        int slots = calculateSlots(Utils.getPlayer(clan.getOwner()));

        if (hasChest(clan)) {
            createEmptyChest(clan, slots);
            return putItem(item, clan, slots);
        }
        return putItem(item, clan, slots);
    }

    private boolean putItem(ItemStack item, Clan clan, int maxSlots) {
        try {
            File chestFile = new File(this.dir, clan.getId() + ".yml");
            FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
            List<ItemStack> chestContents = (List<ItemStack>) chest.getList("content");
            if (chestContents.size() < maxSlots) {
                chestContents.add(item);
                chest.set("content", chestContents);
                chest.save(chestFile);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCheste(ItemStack[] contents, Clan clan) {
        int slots = calculateSlots(Utils.getPlayer(clan.getOwner()));
        if (!hasChest(clan)) {
            createEmptyChest(clan, slots);
            return updateCheste(contents, clan);
        }

        if(!isFull(clan)) {
            try {
                File chestFile = new File(this.dir, clan.getId() + ".yml");
                FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
                List<ItemStack> chestContents = new ArrayList<ItemStack>();

                for (ItemStack item : contents) {
                    if (item != null)
                        chestContents.add(item);
                }

                chest.set("content", chestContents);
                chest.save(chestFile);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setDir(File rewardsChest) {
        this.dir = rewardsChest;
    }
}
