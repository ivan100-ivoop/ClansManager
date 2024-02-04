package org.github.clansmanager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.github.clansmanager.utils.Clan;
import org.github.clansmanager.utils.DBManager;
import org.github.clansmanager.utils.Messages;

import java.util.ArrayList;
import java.util.List;

public class CManager {
    private DBManager database = null;
    private Loader plugin = null;

    private String[] getQuery = {};

    public CManager(){
        this.plugin = Loader.getPlugin(Loader.class);
        this.database = this.plugin.db;
        this.createTable();
        this.getQuery = new String[]{
                "SELECT * FROM " + this.database.fixName("clans") + " WHERE clan_name=?", //0
                "SELECT * FROM " + this.database.fixName("clans") + " WHERE id=?", //1
                "SELECT * FROM " + this.database.fixName("players") + " WHERE clan_id=?", //2
                "DELETE FROM " + this.database.fixName("players") + " WHERE clan_id=?", //3
                "DELETE FROM " + this.database.fixName("clans") + " WHERE id=?", //4
                "INSERT INTO " + this.database.fixName("clans") + "( clan_name, clan_owner, clan_balance, clan_rank, clan_base_location_world, clan_base_location_x, clan_base_location_y, clan_base_location_z, clan_base_location_pitch, clan_base_location_yaw ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", //5
                "SELECT * FROM " + this.database.fixName("clans") // 6
        };
    }

    public List<String> getClansTab(){
        List<String> tab = new ArrayList<>();
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            tab.add(clan.getName());
        }

        return tab;
    }

    public List<String> getClansTab(Player owner){
        List<String> tab = new ArrayList<>();
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            if(clan.isOwner(owner))
                tab.add(clan.getName());
        }

        return tab;
    }

    public List<Clan> getClanById(int clanId) {
        List<Clan> clans = new ArrayList<>();

        try {
            if (!this.database.isConnected())
                this.database.connect();

            List<Object[]> rows = this.database.executeQuery(this.getQuery[1], clanId);

            for (Object[] row : rows) {
                Location loc = getLocationFromRow(row);
                clans.add(new Clan()
                        .setId(((int) row[0]))
                        .setManager(this)
                        .setName(String.valueOf(row[1]))
                        .setOwner(String.valueOf(row[2]))
                        .setBalance(((double) row[4]))
                        .setPrefix(String.valueOf(row[3] == null ? "": row[3]))
                        .setLocation(loc)
                        .setMembers(getPlayers(((int) row[0])))
                        .setKills(((int) (row[13] == null ? 0 : row[13])))
                        .setDeath(((int) (row[12] == null ? 0 : row[12])))
                        .setLock(((boolean) (row[14] == null ? false : (((int) row[14]) == 1 ? true : false))))
                );
            }
        } finally {
            this.database.disconnect();
        }

        return clans;
    }

    public List<Clan> getClanByName(String clanName){
        List<Clan> clan = new ArrayList<>();

        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery(this.getQuery[0], clanName);
        if(rows != null && rows.size() >= 0){
            for(Object[] row : rows){
                Location loc = getLocationFromRow(row);
                clan.add(new Clan()
                        .setId(((int) row[0]))
                        .setManager(this)
                        .setName(String.valueOf(row[1]))
                        .setBalance(((double) row[4]))
                        .setOwner(String.valueOf(row[2]))
                        .setPrefix(String.valueOf(row[3] == null ? "": row[3]))
                        .setLocation(loc)
                        .setMembers(getPlayers(((int) row[0])))
                        .setKills(((int) (row[13] == null ? 0 : row[13])))
                        .setDeath(((int) (row[12] == null ? 0 : row[12])))
                        .setLock(((boolean) (row[14] == null ? false : (((int) row[14]) == 1 ? true : false))))
                );
            }
        }

        return clan;
    }

    public Clan getClansByName(String clanName){

        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery(this.getQuery[0], clanName);
        if(rows != null && rows.size() >= 0){
            for(Object[] row : rows){
                Location loc = getLocationFromRow(row);
                return new Clan()
                        .setId(((int) row[0]))
                        .setManager(this)
                        .setName(String.valueOf(row[1]))
                        .setBalance(((double) row[4]))
                        .setOwner(String.valueOf(row[2]))
                        .setPrefix(String.valueOf(row[3] == null ? "": row[3]))
                        .setLocation(loc)
                        .setMembers(getPlayers(((int) row[0])))
                        .setKills(((int) (row[13] == null ? 0 : row[13])))
                        .setDeath(((int) (row[12] == null ? 0 : row[12])))
                        .setLock(((boolean) (row[14] == null ? false : (((int) row[14]) == 1 ? true : false))));
            }
        }

        return null;
    }

    public List<Clan> topBalance(){
        List<Clan> clan = new ArrayList<>();

        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery("SELECT * FROM " + this.database.fixName("clans") + " ORDER BY clan_balance DESC");
        if(rows != null && rows.size() >= 0){
            for(Object[] row : rows){
                Location loc = getLocationFromRow(row);
                clan.add(new Clan()
                        .setId(((int) row[0]))
                        .setManager(this)
                        .setName(String.valueOf(row[1]))
                        .setBalance(((double) row[4]))
                        .setOwner(String.valueOf(row[2]))
                        .setPrefix(String.valueOf(row[3] == null ? "": row[3]))
                        .setLocation(loc)
                        .setMembers(getPlayers(((int) row[0])))
                        .setKills(((int) (row[13] == null ? 0 : row[13])))
                        .setDeath(((int) (row[12] == null ? 0 : row[12])))
                        .setLock(((boolean) (row[14] == null ? false : (((int) row[14]) == 1 ? true : false))))
                );
            }
        }
        this.database.disconnect();
        return clan;
    }

    public List<Clan> topKills(){
        List<Clan> clan = new ArrayList<>();

        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery("SELECT * FROM " + this.database.fixName("clans") + " ORDER BY clan_kills DESC");
        if(rows != null && rows.size() >= 0){
            for(Object[] row : rows){
                Location loc = getLocationFromRow(row);
                clan.add(new Clan()
                        .setId(((int) row[0]))
                        .setManager(this)
                        .setName(String.valueOf(row[1]))
                        .setBalance(((double) row[4]))
                        .setOwner(String.valueOf(row[2]))
                        .setPrefix(String.valueOf(row[3] == null ? "": row[3]))
                        .setLocation(loc)
                        .setMembers(getPlayers(((int) row[0])))
                        .setKills(((int) (row[13] == null ? 0 : row[13])))
                        .setDeath(((int) (row[12] == null ? 0 : row[12])))
                        .setLock(((boolean) (row[14] == null ? false : (((int) row[14]) == 1 ? true : false))))
                );
            }
        }

        this.database.disconnect();

        return clan;
    }

    public List<Clan> topDeath(){
        List<Clan> clan = new ArrayList<>();

        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery("SELECT * FROM " + this.database.fixName("clans") + " ORDER BY clan_death DESC");
        if(rows != null && rows.size() >= 0){
            for(Object[] row : rows){
                Location loc = getLocationFromRow(row);
                clan.add(new Clan()
                        .setId(((int) row[0]))
                        .setManager(this)
                        .setName(String.valueOf(row[1]))
                        .setBalance(((double) row[4]))
                        .setOwner(String.valueOf(row[2]))
                        .setPrefix(String.valueOf(row[3] == null ? "": row[3]))
                        .setLocation(loc)
                        .setMembers(getPlayers(((int) row[0])))
                        .setKills(((int) (row[13] == null ? 0 : row[13])))
                        .setDeath(((int) (row[12] == null ? 0 : row[12])))
                        .setLock(((boolean) (row[14] == null ? false : (((int) row[14]) == 1 ? true : false))))
                );
            }
        }
        this.database.disconnect();
        return clan;
    }



    public Clan kickClanMember(Player owner, Player target){
        List<Clan> clans = getAllClans();
        for (Clan clan : clans){
            if(clan.isOwner(owner)){
                if(clan.isMember(target)){
                    if(!this.database.isConnected())
                        this.database.connect();
                    this.database.execute("DELETE FROM "+ this.database.fixName("players") +" WHERE player_name=? AND clan_id=?", target.getName(), clan.getId());
                }
                return clan;
            }
        }
        return null;
    }

    public Clan kickAllClanMember(Player owner){
        List<Clan> clans = getAllClans();
        for (Clan clan : clans){
            if(clan.isOwner(owner)){
                if(clan.getMembers().size() >= 0) {
                    if (!this.database.isConnected())
                        this.database.connect();
                    for (String member : clan.getMembers()) {
                        Player newMemberPlayer = Bukkit.getPlayer(member);

                        if (newMemberPlayer != null) {
                            newMemberPlayer.sendMessage(Messages.withPrefix("clan.kick-notify", "&eYour are kick from clan %clan_name%").replace("%clan_name%", clan.getName()));
                        }

                        owner.sendMessage(Messages.withPrefix("success.kick-success", "&eYour successful kick player %player% from clan %clan_name%!").replace("%player%", member).replace("%clan_name%", clan.getName()));
                        this.database.execute("DELETE FROM " + this.database.fixName("players") + " WHERE player_name=? AND clan_id=?", member, clan.getId());
                    }
                }
                return clan;
            }
        }
        return null;
    }


    public Clan removeClan(String clanName){
        Clan selected = null;
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            if(clan.getName().equalsIgnoreCase(clanName)){
                selected = clan;
            }
        }

        if(!this.database.isConnected())
            this.database.connect();

        if(selected != null){
            if(this.database.execute(this.getQuery[3], selected.getId()) && this.database.execute(this.getQuery[4], selected.getId())){
                return selected;
            }
        }

        return null;
    }

    public boolean leaveClan(Player target, int clanId){
        if(haveClan(clanId)){
            if(!this.database.isConnected())
                this.database.connect();
           return this.database.execute("DELETE FROM " + this.database.fixName("players") + " WHERE player_name=? AND clan_id=?", target.getName(), clanId);
        }
        return false;
    }

    private boolean haveClan(int clanId) {
        List<Clan> clans = this.getAllClans();
        for(Clan clan : clans){
            if(clan.getId() == clanId){
                return true;
            }
        }

        return false;
    }

    public Clan removeClan(Player owner){
        Clan selected = null;
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            if(clan.isOwner(owner)){
                selected = clan;
            }
        }

        if(!this.database.isConnected())
            this.database.connect();

        if(selected != null){
            if(this.database.execute(this.getQuery[3], selected.getId()) && this.database.execute(this.getQuery[4], selected.getId())){
                return selected;
            }
        }

        return null;
    }


    public boolean createClan(String clanName, Player owner){
        if(getClanIdByName(clanName) != -1){
            return false;
        }

        if(!this.database.isConnected())
            this.database.connect();

        Location loc = owner.getLocation();
        return this.database.executeInsert(
                this.getQuery[5],
                clanName,
                owner.getName(),
                0,
                0,
                loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw()
        );
    }

    public boolean updateClanLocation(Player owner){
        Location loc = owner.getLocation();
        List<Clan> clans = this.getAllClans();
        for(Clan clan : clans){
            if(clan.getOwner().equalsIgnoreCase(owner.getName())){
                if(!this.database.isConnected())
                    this.database.connect();

                return this.database.execute(
                        "UPDATE " + this.database.fixName("clans") + " SET clan_base_location_world=?, clan_base_location_x=?, clan_base_location_y=?, clan_base_location_z=?, clan_base_location_pitch=?, clan_base_location_yaw=? WHERE clan_name=? AND clan_owner=?",
                        loc.getWorld().getName(),
                        loc.getX(),
                        loc.getY(),
                        loc.getZ(),
                        loc.getPitch(),
                        loc.getYaw(),
                        clan.getName(),
                        owner.getName()
                );
            }
        }
        return false;
    }

    public boolean updateClanPrefix(Player owner, String prefix){
        List<Clan> clans = this.getAllClans();

        if(!this.database.isConnected())
            this.database.connect();

        for(Clan clan : clans){
            if(clan.isOwner(owner)){
                return this.database.execute(
                        "UPDATE " + this.database.fixName("clans") + " SET clan_prefix=? WHERE clan_name=? AND clan_owner=?",
                        prefix,
                        clan.getName(),
                        owner.getName()
                );
            }
        }
        return false;
    }

    public boolean updateClanOwner(Player owner, Player newOwner){
        List<Clan> clans = this.getAllClans();


        for(Clan clan : clans){
            if(clan.isOwner(owner)){
                if(!this.database.isConnected())
                    this.database.connect();

                if(clan.isMember(newOwner)){
                    this.database.execute("DELETE FROM "+ this.database.fixName("players") +" WHERE player_name=?", newOwner.getName());
                    this.database.execute("INSERT INTO " + this.database.fixName("players") + "( clan_id, player_name ) VALUES (?, ?)", clan.getId(), owner.getName());
                }

                return this.database.execute(
                        "UPDATE " + this.database.fixName("clans") + " SET clan_owner=? WHERE id=?",
                        newOwner.getName(),
                        clan.getId()
                );
            }
        }
        return false;
    }

    public boolean isMemberOrOwner(Player target){
        if(!this.database.isConnected())
            this.database.connect();
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans) {
            if (clan.isOwner(target) || clan.isMember(target)) {
                return true;
            }
        }

        return false;
    }

    public boolean invitePlayer(Player player, int clanId) {

        Clan selectedClan = null;

        List<Clan> clans = this.getClanById(clanId);

        if (!this.database.isConnected())
            this.database.connect();

        for (Clan clan : clans) {
            if (clan.isMember(player) || clan.isOwner(player)) {
                return false;
            }
        }

        for (Clan clan : clans){
            if(clan.getId() == clanId){
                selectedClan = clan;
            }
        }

        if(selectedClan != null)
            return this.database.executeInsert("INSERT INTO " + this.database.fixName("invite") + " ( clan_id, clan_name, player_name) VALUES (?, ?, ?) ", selectedClan.getId(), selectedClan.getName(), player.getName());

        return false;
    }

    public boolean invitePlayer(Player player, String clanName) {

        Clan selectedClan = null;

        List<Clan> clans = this.getClanByName(clanName);

        if (!this.database.isConnected())
            this.database.connect();

        for (Clan clan : clans) {
            if (clan.isMember(player) || clan.isOwner(player)) {
                return false;
            }
        }

        for (Clan clan : clans){
            if(clan.getName().equalsIgnoreCase(clanName)){
                selectedClan = clan;
            }
        }


        if(selectedClan != null)
            return this.database.executeInsert("INSERT INTO " + this.database.fixName("invite") + " ( clan_id, clan_name, player_name) VALUES (?, ?, ?) ", selectedClan.getId(), selectedClan.getName(), player.getName());

        return false;
    }

    public List<String> getInvites(Player player){
        List<String> clans = new ArrayList<>();

        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery("SELECT clan_id FROM " + this.database.fixName("invite") + " WHERE player_name=?", player.getName());

        if(rows != null && rows.size() >= 0) {
            for (Object[] row : rows){
                clans.add(getClanNameById(((int) row[0])));
            }
        }
        return clans;
    }


    public Clan acceptClan(Player target, int clanId){
        Clan selected = null;

        List<Clan> clans = getClanById(clanId);
        for (Clan clan : clans){
            if(clan.getId() == clanId){
                selected = clan;
            }
        }

        if(selected != null){
            int _clanId = this.isHaveInvite(target);
            if(_clanId > -1){
                if(!this.database.isConnected())
                    this.database.connect();
                if(this.database.execute("DELETE FROM " + this.database.fixName("invite") + " WHERE player_name=?", target.getName())){
                    if(this.database.execute("INSERT INTO " + this.database.fixName("players") + "( clan_id, player_name ) VALUES (?, ?)", _clanId, target.getName()))
                    {
                        selected.getMembers().add(target.getName());
                        return selected;
                    }
                }
            }

        }

        return null;
    }

    public Clan acceptClan(Player target, String clanName){
        Clan selected = null;

        List<Clan> clans = getClanByName(clanName);
        for (Clan clan : clans){
            if(clan.getName().equalsIgnoreCase(clanName)){
                selected = clan;
            }
        }

        if(selected != null){
            int clanId = this.isHaveInvite(target);
            if(clanId > -1){
                if(!this.database.isConnected())
                    this.database.connect();
                if(this.database.execute("DELETE FROM  " + this.database.fixName("invite") + " WHERE player_name=?", target.getName())){
                    if(this.database.execute("INSERT INTO " + this.database.fixName("players") + "( clan_id, player_name ) VALUES (?, ?)", clanId, target.getName()))
                    {
                        selected.getMembers().add(target.getName());
                        return selected;
                    }
                }
            }

        }

        return null;
    }

    private int isHaveInvite(Player target) {
        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery("SELECT clan_id FROM " + this.database.fixName("invite") + " WHERE player_name=?", target.getName());
        if(rows != null && rows.size() >= 0) {
            for(Object[] row : rows){
                return ((int) row[0]);
            }
        }
        return -1;
    }


    public String getClanNameById(int clanId){
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            if(clan.getId() == clanId){
                return clan.getName();
            }
        }

        return null;
    }


    public String getClanPrefixById(int clanId){
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            if(clan.getId() == clanId){
                return clan.getPrefix();
            }
        }

        return null;
    }

    public String getClanPrefixByName(String clanName){
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            if(clan.getName().equalsIgnoreCase(clanName)){
                return clan.getPrefix();
            }
        }

        return null;
    }
    private Location getLocationFromRow(Object[] row) {
        String world = (String) row[6];
        double x = (double) row[7];
        double y = (double) row[8];
        double z = (double) row[9];
        double pitch = (double) row[10];
        double yaw = (double) row[11];
        return new Location(Bukkit.getWorld(world), x, y, z, new Double(yaw).floatValue(), new Double(pitch).floatValue());
    }
    public List<Clan> getAllClans() {
        List<Clan> clan = new ArrayList<>();

        if(!this.database.isConnected())
            this.database.connect();

        List<Object[]> rows = this.database.executeQuery(this.getQuery[6]);

        if(rows != null && rows.size() >= 0) {
            for (Object[] row : rows) {
                clan.add(new Clan()
                        .setId(((int) row[0]))
                        .setManager(this)
                        .setName(String.valueOf(row[1]))
                        .setOwner(String.valueOf(row[2]))
                        .setPrefix(String.valueOf(row[3] == null ? "": row[3]))
                        .setLocation(getLocationFromRow(row))
                        .setMembers(this.getPlayers(((int) row[0])))
                        .setBalance(((double) row[4]))
                        .setKills(((int) (row[13] == null ? 0 : row[13])))
                        .setDeath(((int) (row[12] == null ? 0 : row[12])))
                        .setLock(((boolean) (row[14] == null ? false : (((int) row[14]) == 1 ? true : false))))
                );
            }
        }

        return clan;

    }

    public int getClanIdByName(String clanName){
        List<Clan> clans = this.getAllClans();

        for (Clan clan : clans){
            if(clan.getName().equalsIgnoreCase(clanName)){
                return clan.getId();
            }
        }

        return -1;
    }

    public boolean updateKills(Clan clan) {
        String sql = "UPDATE " + this.database.fixName("clans") + " SET clan_kills=? WHERE id=?";
        if(!this.database.isConnected())
            this.database.connect();

        return this.database.execute(sql, clan.getKills(), clan.getId());
    }

    public boolean updateDeaths(Clan clan) {
        String sql = "UPDATE " + this.database.fixName("clans") + " SET clan_death=? WHERE id=?";
        if(!this.database.isConnected())
            this.database.connect();

        return this.database.execute(sql, clan.getDeath(), clan.getId());
    }

    private List<String> getPlayers(int clanId) {
        List<String> clan = new ArrayList<>();

        try {
            if (!this.database.isConnected())
                this.database.connect();

            List<Object[]> rows = this.database.executeQuery(this.getQuery[2], clanId);

            for (Object[] row : rows) {
                clan.add(String.valueOf(row[2]));
            }
        } finally {
            this.database.disconnect();
        }

        return clan;
    }
    private void createTable(){
        this.database.connect();
        if (!this.database.tableExists(this.database.fixName("clans"))) {
            if (!this.database.isMysql()) {
                this.database.execute("CREATE TABLE IF NOT EXISTS " + this.database.fixName("clans") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "clan_name VARCHAR (255)," +
                        "clan_owner VARCHAR (255)," +
                        "clan_prefix VARCHAR (255) NULL," +
                        "clan_balance DOUBLE," +
                        "clan_rank INTEGER," +
                        "clan_base_location_world VARCHAR (255)," +
                        "clan_base_location_x DOUBLE," +
                        "clan_base_location_y DOUBLE," +
                        "clan_base_location_z DOUBLE," +
                        "clan_base_location_pitch DOUBLE," +
                        "clan_base_location_yaw DOUBLE," +
                        "clan_death INTEGER NULL," +
                        "clan_kills INTEGER NULL," +
                        "clan_lock BOOLEAN NULL," +
                        "UNIQUE (id)" +
                        ");");
            } else {
                this.database.execute("CREATE TABLE IF NOT EXISTS " + this.database.fixName("clans") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `clan_name` varchar(255) NOT NULL," +
                        "  `clan_owner` varchar(255) NOT NULL," +
                        "  `clan_prefix` varchar(255) NULL," +
                        "  `clan_balance` double NOT NULL," +
                        "  `clan_rank` varchar(111) NOT NULL," +
                        "  `clan_base_location_world` varchar(255) NOT NULL," +
                        "  `clan_base_location_x` double NOT NULL," +
                        "  `clan_base_location_y` double NOT NULL," +
                        "  `clan_base_location_z` double NOT NULL," +
                        "  `clan_base_location_pitch` double NOT NULL," +
                        "  `clan_base_location_yaw` double NOT NULL," +
                        "  `clan_death` varchar(111) NULL," +
                        "  `clan_kills` varchar(111) NULL," +
                        "  `clan_lock` boolean NULL," +
                        ");");
                this.database.execute("ALTER TABLE " + this.database.fixName("clans") + " ADD PRIMARY KEY (`id`);");
                this.database.execute("ALTER TABLE " + this.database.fixName("clans") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;");
            }
        }

        if(!this.database.tableExists(this.database.fixName("invite"))){
            if(!this.database.isMysql()) {
                this.database.execute("CREATE TABLE IF NOT EXISTS " + this.database.fixName("invite") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "clan_name VARCHAR (255)," +
                        "clan_id INTEGER," +
                        "player_name VARCHAR (255)," +
                        "UNIQUE (id)" +
                        ");");
            } else {
                this.database.execute("CREATE TABLE IF NOT EXISTS " + this.database.fixName("invite") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `clan_id` int(11) NOT NULL," +
                        "  `clan_name` varchar(255) NOT NULL," +
                        "  `player_name` varchar(255) NOT NULL," +
                        ");");

                this.database.execute("ALTER TABLE " + this.database.fixName("invite") + " ADD PRIMARY KEY (`id`);");
                this.database.execute("ALTER TABLE " + this.database.fixName("invite") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;");

            }
        }

            if (!this.database.tableExists(this.database.fixName("players"))) {
                if(!this.database.isMysql()) {
                    this.database.execute("CREATE TABLE IF NOT EXISTS " + this.database.fixName("players") + " (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "clan_id INTEGER," +
                            "player_name VARCHAR (255)," +
                            "UNIQUE (id)" +
                            ");");
                } else {
                    this.database.execute("CREATE TABLE IF NOT EXISTS " + this.database.fixName("players") + " (" +
                            "  `id` int(11) NOT NULL," +
                            "  `clan_id` int(11) NOT NULL," +
                            "  `player_name` varchar(255) NOT NULL," +
                            ");");
                    this.database.execute("ALTER TABLE " + this.database.fixName("players") + " ADD PRIMARY KEY (`id`);");
                    this.database.execute("ALTER TABLE " + this.database.fixName("players") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;");

                }
        }
        this.database.disconnect();
    }

    public Clan getClanByOwnerPlayer(Player player) {
        List<Clan> clans = this.getAllClans();
        for (Clan clan : clans){
            if(clan.isOwner(player)){
                return clan;
            }
        }

        return null;
    }

    public Clan getClanByMemberPlayer(Player player) {
        List<Clan> clans = this.getAllClans();
        for (Clan clan : clans){
            if(clan.isMember(player)){
                return clan;
            }
        }

        return null;
    }

    public void disconnect() {
        if(this.database.isConnected())
            this.database.disconnect();
    }

    public boolean updateBalance(Clan clan) {
        String sql = "UPDATE " + this.database.fixName("clans") + " SET clan_balance=? WHERE id=?";
        if(!this.database.isConnected())
            this.database.connect();

        return this.database.execute(sql, clan.getBalance(), clan.getId());
    }

    public void updateLock(Clan clan) {

        String sql = "UPDATE " + this.database.fixName("clans") + " SET clan_lock=? WHERE id=?";
        if(!this.database.isConnected())
            this.database.connect();
        this.database.execute(sql, clan.isLock(), clan.getId());
        this.database.disconnect();
    }
}
