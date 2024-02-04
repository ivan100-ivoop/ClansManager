package org.github.clansmanager.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.github.clansmanager.Loader;

import java.io.File;

public class Arena {

    private static FileConfiguration getConfig(){
        File _arena = new File(Loader.instance.dataDir, "arena.yml");
        FileConfiguration arena = YamlConfiguration.loadConfiguration(_arena);
        return arena;
    }

    private static void saveConfig(FileConfiguration arena){
        File _arena = new File(Loader.instance.dataDir, "arena.yml");
        try {
            if(!_arena.exists()){
                _arena.createNewFile();
            }
            arena.save(_arena);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSpawn1(Location location) {
        FileConfiguration arena = Arena.getConfig();
        arena.set("spawn1.x", location.getX());
        arena.set("spawn1.y", location.getY());
        arena.set("spawn1.z", location.getZ());
        arena.set("spawn1.pitch", location.getPitch());
        arena.set("spawn1.yaw", location.getYaw());
        arena.set("spawn1.world", location.getWorld().getName());
        Arena.saveConfig(arena);
    }

    public static void setSpawn2(Location location) {
        FileConfiguration arena = Arena.getConfig();
        arena.set("spawn2.x", location.getX());
        arena.set("spawn2.y", location.getY());
        arena.set("spawn2.z", location.getZ());
        arena.set("spawn2.pitch", location.getPitch());
        arena.set("spawn2.yaw", location.getYaw());
        arena.set("spawn2.world", location.getWorld().getName());
        Arena.saveConfig(arena);
    }

    public static Location getSpawn1() {
        FileConfiguration arena = Arena.getConfig();
        return new Location(Bukkit.getWorld(arena.getString("spawn1.world", "world")), arena.getDouble("spawn1.x", 0), arena.getDouble("spawn1.y", 0), arena.getDouble("spawn1.z", 0), new Double(arena.getDouble("spawn1.yaw", 0)).floatValue(), new Double(arena.getDouble("spawn1.pitch", 0)).floatValue());
    }


    public static Location getSpawn2() {
        FileConfiguration arena = Arena.getConfig();
        return new Location(Bukkit.getWorld(arena.getString("spawn2.world", "world")), arena.getDouble("spawn2.x", 0), arena.getDouble("spawn2.y", 0), arena.getDouble("spawn2.z", 0), new Double(arena.getDouble("spawn2.yaw", 0)).floatValue(), new Double(arena.getDouble("spawn2.pitch", 0)).floatValue());
    }

    public static void setInvOne(PlayerInventory inventory) {
        FileConfiguration arena = Arena.getConfig();
        arena.set("inv1", inventory.getContents());
        Arena.saveConfig(arena);
    }

    public static void setInvTwo(PlayerInventory inventory) {
        FileConfiguration arena = Arena.getConfig();
        arena.set("inv2", inventory.getContents());
        Arena.saveConfig(arena);
    }

    public static ItemStack[] getInvOne() {
        FileConfiguration arena = Arena.getConfig();
        return arena.getList("inv1").toArray(new ItemStack[0]);
    }
    public static ItemStack[] getInvTwo() {
        FileConfiguration arena = Arena.getConfig();
        return arena.getList("inv2").toArray(new ItemStack[0]);
    }
}
