package cc.i9mc.newxbedwars.commands;

import cc.i9mc.newxbedwars.databse.map.DropType;
import cc.i9mc.newxbedwars.databse.map.MapData;
import cc.i9mc.newxbedwars.databse.map.MapDataPojo;
import cc.i9mc.newxbedwars.databse.map.MapDataSQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCommand implements CommandExecutor {
    private final List<MapData> maps;
    private final MapDataSQL mds;

    public AdminCommand() {
        mds = new MapDataSQL();
        maps = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("此命令仅限玩家");
            return true;
        }

        Player player = (Player) commandSender;

        if (strings.length == 0) {
            player.sendMessage("/game toWorld <地图名字> ----- 前往世界");
            player.sendMessage("/game loadWorld <地图名字> ----- 加载世界");
            player.sendMessage("/game loadGame <地图名字> ----- 加载现成配置");
            player.sendMessage(" ");
            player.sendMessage("/game setWaitingLoc ----- 设置等待大厅");
            player.sendMessage(" ");
            player.sendMessage("/game new <地图名称> ----- 创建新地图");
            player.sendMessage("/game setAuthor <地图名称> <作者名称> ----- 设置作者名称");
            player.sendMessage("/game setTeamMaxPlayers <地图名称> <数量> ----- 设置队伍最大人数");
            player.sendMessage("/game setMinPlayers <地图名称> <数量> ----- 设置地图最小人数");
            player.sendMessage("/game setRange <地图名称> <数量> ----- 设置出生点保护范围");
            player.sendMessage("/game setReSpawnLoc <地图名称> ----- 设置地图重生点");
            player.sendMessage("/game addBaseLoc <地图名称> ----- 增加基地出生点");
            player.sendMessage("/game addDropLoc <地图名称> <类型> ----- 增加基地掉落资源点 类型: B/D/E");
            player.sendMessage("/game addShop <地图名称> <类型> ----- 增加村民点 类型: 1/2");
            player.sendMessage("/game setloc1 <地图名称> ----- 设置边界1");
            player.sendMessage("/game setloc2 <地图名称> ----- 设置边界2");
            player.sendMessage(" ");
            player.sendMessage("/game info <地图名称> ----- 查看设置的参数");
            player.sendMessage("/game save <地图名称> ----- 保存设置的地图");
            return true;
        }

        if (strings[0].equalsIgnoreCase("setWaitingLoc")) {
            Location loc = player.getLocation();
            String sLoc = loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
            mds.setWaitingLoc(sLoc);
            player.sendMessage("设置等待大厅成功!");
            return true;
        }

        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("toWorld")) {
                player.teleport(Bukkit.getWorld(strings[1]).getSpawnLocation());
            }

            if (strings[0].equalsIgnoreCase("loadWorld")) {
                WorldCreator cr = new WorldCreator(strings[1]);
                cr.environment(World.Environment.NORMAL);
                World mapWorld = Bukkit.createWorld(cr);

                mapWorld.setAutoSave(false);
                mapWorld.setGameRuleValue("doMobSpawning", "false");
                mapWorld.setGameRuleValue("doFireTick", "false");
            }

            if (strings[0].equalsIgnoreCase("loadGame")) {
                maps.add(new MapDataSQL().loadMap(strings[1]));
                player.sendMessage("配置加载成功!");
                return true;
            }

            if (strings[0].equalsIgnoreCase("new")) {
                maps.add(new MapData(strings[1]));
                player.sendMessage("地图创建成功!");
                return true;
            }

            if (strings[0].equalsIgnoreCase("setReSpawnLoc")) {
                getMap(strings[1]).setReSpawnLocation(player.getLocation());
                player.sendMessage("地图重生点设置成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("addBaseLoc")) {
                getMap(strings[1]).addBaseLocation(player.getLocation());
                player.sendMessage("基地出生点增加成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("setloc1")) {
                getMap(strings[1]).setArea1Location(player.getLocation());
                player.sendMessage("基地出生点增加成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("setloc2")) {
                getMap(strings[1]).setArea2Location(player.getLocation());
                player.sendMessage("基地出生点增加成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("info")) {
                MapData mapData = getMap(strings[1]);
                player.sendMessage("名称: " + mapData.getName());
                player.sendMessage("作者: " + mapData.getAuthor());
                player.sendMessage("地图最小人数: " + mapData.getMinPlayers());
                player.sendMessage("队伍最大人数: " + mapData.getTeamMaxPlayers());
                player.sendMessage("基地出生点: " + mapData.getBaseLocations().size());
                return true;
            }

            if (strings[0].equalsIgnoreCase("save")) {
                MapData mapData = getMap(strings[1]);

                MapDataPojo mapDataPojo = new MapDataPojo(mapData.getAuthor(), mapData.getTeamMaxPlayers(), mapData.getMinPlayers(), mapData.getRange(), locationToStringLocation(mapData.getReSpawnLocation()), locationToStringLocation(mapData.getArea1Location()), locationToStringLocation(mapData.getArea2Location()), listLocationToListStringLocation(mapData.getBaseLocations()), dropLocationToListStringLocation(mapData.getDropLocations()), shopLocationToListStringLocation(mapData.getShopLocations()));

                mds.addMap(mapData.getName(), mapDataPojo);
                player.sendMessage("保存成功！");
                return true;
            }
        }

        if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("setAuthor")) {
                getMap(strings[1]).setAuthor(strings[2]);
                player.sendMessage("作者设置成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("setTeamMaxPlayers")) {
                getMap(strings[1]).setTeamMaxPlayers(Integer.valueOf(strings[2]));
                player.sendMessage("队伍最大人数设置成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("setMinPlayers")) {
                getMap(strings[1]).setMinPlayers(Integer.valueOf(strings[2]));
                player.sendMessage("地图最小人数设置成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("setRange")) {
                getMap(strings[1]).setRange(Integer.valueOf(strings[2]));
                player.sendMessage("出生点范围设置成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("addDropLoc")) {
                getMap(strings[1]).addDropLocation(DropType.valueOf(strings[2].toUpperCase()), player.getLocation());
                player.sendMessage("增加基地掉落点成功！");
                return true;
            }

            if (strings[0].equalsIgnoreCase("addShop")) {
                getMap(strings[1]).addShopLocation(Integer.valueOf(strings[2]), player.getLocation());
                player.sendMessage("增加商人成功！");
                return true;
            }
        }

        return false;
    }

    private List<String> listLocationToListStringLocation(List<Location> baseLocs) {
        List<String> sd = new ArrayList<>();
        for (Location loc : baseLocs) {
            sd.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
        }
        return sd;
    }

    private HashMap<String, String> dropLocationToListStringLocation(HashMap<Location, DropType> dropTypeHashMap) {
        HashMap<String, String> sd = new HashMap<>();

        for (Map.Entry<Location, DropType> e : dropTypeHashMap.entrySet()) {
            sd.put(locationToStringLocation(e.getKey()), e.getValue().toString());
        }
        return sd;
    }

    private HashMap<String, Integer> shopLocationToListStringLocation(HashMap<Location, Integer> shopTypeHashMap) {
        HashMap<String, Integer> sd = new HashMap<>();

        for (Map.Entry<Location, Integer> e : shopTypeHashMap.entrySet()) {
            sd.put(locationToStringLocation(e.getKey()), e.getValue());
        }
        return sd;
    }

    public String locationToStringLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    private MapData getMap(String name) {
        for (MapData md : maps) {
            if (md.getName().equals(name)) {
                return md;
            }
        }
        return null;
    }


}