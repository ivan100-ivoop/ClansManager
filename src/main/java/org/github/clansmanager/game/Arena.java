package org.github.clansmanager.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.github.clansmanager.Loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Arena {
    public String ARENA_NAME = null;
    public String ARENA_ID = null;
    public Location ARENA_LOCATION = null;

    public int maxClans = 2;
    public List<Location> SPAWN = new ArrayList<>();
    public List<ItemStack[]> INVENTORY = new ArrayList<>();
    public boolean IN_USE = false;

    public Arena addSpawn(Location loc){
        this.SPAWN.add(loc);
        return this;
    }
    public Arena setLocation(Location loc){
        this.ARENA_LOCATION = loc;
        return this;
    }

    public Arena setMaxClans(int max){
        this.maxClans = max;
        return this;
    }

    public Arena setName(String name){
        this.ARENA_NAME = name;
        return this;
    }

    public Arena addInventory(ItemStack[] inv){
        this.INVENTORY.add(inv);
        return this;
    }

    public Arena setInUse(boolean inUse){
        this.IN_USE = inUse;
        return this;
    }

    public Arena setID(String uuid){
        this.ARENA_ID = uuid;
        return this;
    }

    public boolean saveArena() throws IOException {
        File arenasDir = new File(Loader.instance.getDataFolder(), "arenas/");
        File arenaFile = new File(arenasDir, String.format("%s.yml", this.ARENA_ID));

        if (!arenasDir.exists()) {
            arenasDir.mkdir();
        }

        if (!arenaFile.exists()) {
            FileConfiguration arena = YamlConfiguration.loadConfiguration(arenaFile);

            // arena name
            arena.set("arena.name", this.ARENA_NAME);

            // arena size
            arena.set("arena.size", this.maxClans);

            // arena id
            arena.set("arena.id", this.ARENA_ID);

            // arena location
            arena.set("arena.location.x", this.ARENA_LOCATION.getX());
            arena.set("arena.location.y", this.ARENA_LOCATION.getY());
            arena.set("arena.location.z", this.ARENA_LOCATION.getZ());
            arena.set("arena.location.pitch", this.ARENA_LOCATION.getPitch());
            arena.set("arena.location.yaw", this.ARENA_LOCATION.getYaw());
            arena.set("arena.location.world", this.ARENA_LOCATION.getWorld().getName());

            // spawns location
            for (int i = 0; i<this.SPAWN.size(); i++){
                Location spawn = this.SPAWN.get(i);
                arena.set(String.format("arena.spawn.%s.x", i), spawn.getX());
                arena.set(String.format("arena.spawn.%s.y", i), spawn.getY());
                arena.set(String.format("arena.spawn.%s.z", i), spawn.getZ());
                arena.set(String.format("arena.spawn.%s.pitch", i), spawn.getPitch());
                arena.set(String.format("arena.spawn.%s.yaw", i), spawn.getYaw());
                arena.set(String.format("arena.spawn.%s.world", i), spawn.getWorld().getName());
            }

            // Inventory save
            for (int i = 0; i< INVENTORY.size(); i++){
                arena.set(String.format("arena.inv.%s", i), itemStackArrayToString(this.INVENTORY.get(i)));
            }

            arena.save(arenaFile);

            return true;
        }
        return false;
    }

    private YamlConfiguration itemStackArrayToString(ItemStack[] items) {
        YamlConfiguration config = new YamlConfiguration();
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                ItemStack item = items[i];
                if (item != null) {
                    config.set(String.format("content.%d.type", i), item.getType().toString());
                    config.set(String.format("content.%d.amount", i), item.getAmount());
                    config.set(String.format("content.%d.meta", i), itemMetaToString(item));
                } else {
                    config.set(String.format("content.%d.meta", i), null); // Handle null ItemStacks
                }
            }
        }
        return config;
    }

    private static String itemMetaToString(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        if (itemStack != null && itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            config.set("item-meta", itemMeta);
        }
        return config.saveToString();
    }

    public static void LoadArenas(){
        File arenasDir = new File(Loader.instance.getDataFolder(), "arenas/");

        if (arenasDir.exists()) {
            File[] arenaFiles = arenasDir.listFiles();
            for (File arenaFile : arenaFiles ){
                if(arenaFile.getName().endsWith(".yml")) {
                    loadArena(arenaFile);
                }
            }
        }
    }

    private static ItemMeta stringToItemMeta(String serializedItemMeta) {
        if (serializedItemMeta != null && !serializedItemMeta.isEmpty()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.loadFromString(serializedItemMeta);
                return (ItemMeta) config.get("item-meta");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void loadArena(File arenaFile) {
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);
            String arenaId = config.getString("arena.id");
            if (arenaId != null) {
                String arenaName = config.getString("arena.name");
                int maxClans = config.getInt("arena.size", 0);

                double locX = config.getDouble("arena.location.x", 0);
                double locY = config.getDouble("arena.location.y", 0);
                double locZ = config.getDouble("arena.location.z", 0);
                float yaw = (float) config.getDouble("arena.location.yaw", 0);
                float pitch = (float) config.getDouble("arena.location.pitch", 0);
                String worldName = config.getString("arena.location.world", "");

                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Location location = new Location(world, locX, locY, locZ, yaw, pitch);
                    Arena arena = new Arena()
                            .setName(arenaName)
                            .setID(arenaId)
                            .setMaxClans(maxClans)
                            .setLocation(location);


                    loadSpawns(config, arena);
                    loadInventories(config, arena);

                    Loader.arenas.put(arenaId, arena);
                } else {
                    Loader.logger.log(Level.WARNING,  String.format("Error loading arena %s: World %s not found.", arenaId , worldName));
                }
            } else {
                Loader.logger.log(Level.WARNING, "Error loading arena: Arena ID not found.");
            }
        } catch (Exception e) {
            Loader.logger.log(Level.WARNING, String.format("Error loading arena from file: %s", arenaFile.getName()));
            e.printStackTrace();
        }
    }
    private static void loadInventories(FileConfiguration config, Arena arena) {
        ConfigurationSection invSection = config.getConfigurationSection("arena.inv");
        if (invSection != null) {
            for (String key : invSection.getKeys(false)) {
                List<ItemStack> items = new ArrayList<>();
                ConfigurationSection subInvSection = config.getConfigurationSection(String.format("arena.inv.%s.content", key));
                if (subInvSection != null) {
                    for (String subKey : subInvSection.getKeys(false)) {
                        String material = config.getString(String.format("arena.inv.%s.content.%s.type", key, subKey), "");
                        int amount = config.getInt(String.format("arena.inv.%s.content.%s.amount", key, subKey), 1);
                        ItemMeta meta = stringToItemMeta(config.getString(String.format("arena.inv.%s.content.%s.meta", key, subKey), ""));
                        ItemStack item = new ItemStack(Material.valueOf(material), amount);
                        item.setItemMeta(meta);
                        items.add(item);
                    }
                    arena.addInventory(items.toArray(new ItemStack[0]));
                }
            }
        }
    }
    private static void loadSpawns(FileConfiguration config, Arena arena) {
        ConfigurationSection spawnSection = config.getConfigurationSection("arena.spawn");
        if (spawnSection != null) {
            for (String key : spawnSection.getKeys(false)) {
                double x = config.getDouble("arena.spawn." + key + ".x", 0);
                double y = config.getDouble("arena.spawn." + key + ".y", 0);
                double z = config.getDouble("arena.spawn." + key + ".z", 0);
                float yaw = (float) config.getDouble("arena.spawn." + key + ".yaw", 0);
                float pitch = (float) config.getDouble("arena.spawn." + key + ".pitch", 0);
                String worldName = config.getString("arena.spawn." + key + ".world", "");

                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
                    arena.addSpawn(spawnLocation);
                } else {
                    Loader.logger.log(Level.WARNING, String.format("Error loading spawn %s for arena %s: World %s not found.", key , arena.ARENA_NAME, worldName));
                }
            }
        }
    }



}
