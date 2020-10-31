package cc.i9mc.newxbedwars.databse.map;

import cc.i9mc.gameutils.BukkitGameUtils;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.utils.Util;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MapDataSQL {
    private final Gson GSON = new Gson();

    public MapDataSQL() {
        BukkitGameUtils.getInstance().getConnectionPoolHandler().registerDatabase("bwdata");
    }

    private static LinkedHashMap<String, Integer> sortMapByValue(LinkedHashMap<String, Integer> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(oriMap.entrySet());
        entryList.sort(Comparator.comparing(Map.Entry::getValue));

        Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
        Map.Entry<String, Integer> tmpEntry;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    public void addMap(String mapName, MapDataPojo mapData) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwdata");
            if (connection == null || connection.isClosed()) {
                return;
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM XBWMaps Where MapName=?");
            preparedStatement.setString(1, mapName);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                preparedStatement = connection.prepareStatement("INSERT INTO XBWMaps (MapName,Data) VALUES (?,?);");
                preparedStatement.setString(1, mapName);
                preparedStatement.setString(2, GSON.toJson(mapData));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MapData randomMap() {
        Connection connection = null;
        ResultSet resultSet = null;
        MapData mapData = null;
        LinkedHashMap<String, Integer> mapBindServers = new LinkedHashMap<>();

        try {
            connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwdata");
            if (connection == null || connection.isClosed()) {
                return null;
            }

            //********************************************************************************************//
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM XBWMaps");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                mapBindServers.put(resultSet.getString("MapName"), 0);
            }
            //********************************************************************************************//

            if (!NewXBedwars.getInstance().getConfig().getBoolean("isDocker")) {
                //********************************************************************************************//
                preparedStatement = connection.prepareStatement("SELECT * FROM XBWMapBind");
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    mapBindServers.put(resultSet.getString("MapName"), mapBindServers.getOrDefault(resultSet.getString("MapName"), 0) + 1);
                }
                //********************************************************************************************//

                //********************************************************************************************//
                preparedStatement = connection.prepareStatement("SELECT * FROM XBWMapBind Where Address=?");
                preparedStatement.setString(1, "" + Bukkit.getPort());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    mapBindServers.clear();
                    mapBindServers.put(resultSet.getString("MapName"), 0);
                }
                //********************************************************************************************//

                mapBindServers = sortMapByValue(mapBindServers);

                for (Map.Entry<String, Integer> e : mapBindServers.entrySet()) {
                    System.out.println(e.getKey() + "  ----  " + e.getValue());
                }

                preparedStatement = connection.prepareStatement("SELECT * FROM XBWMapBind Where Address=?");
                preparedStatement.setString(1, "" + Bukkit.getPort());
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    preparedStatement = connection.prepareStatement("INSERT INTO XBWMapBind (Address,MapName) VALUES (?,?);");
                    preparedStatement.setString(1, "" + Bukkit.getPort());
                    preparedStatement.setString(2, getHead(mapBindServers).getKey());
                    preparedStatement.executeUpdate();
                }
            }


            preparedStatement = connection.prepareStatement("SELECT * FROM XBWMaps Where MapName=?");
            preparedStatement.setString(1, NewXBedwars.getInstance().getConfig().getBoolean("isDocker") ? getMapFormID(mapBindServers, Integer.parseInt(System.getenv("MAPID"))).getKey() : getHead(mapBindServers).getKey());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("MapName");
                String url = resultSet.getString("URL");
                MapDataPojo mapDataPojo = GSON.fromJson(resultSet.getString("Data"), MapDataPojo.class);

                if (!NewXBedwars.getInstance().getConfig().getBoolean("updateMap")) {
                    if (new File(name).exists()) {
                        new File(name).delete();
                    }

                    FileUtils.copyDirectory(new File(url), new File(name));
                }

                WorldCreator cr = new WorldCreator(name);
                cr.environment(World.Environment.NORMAL);
                World mapWorld = Bukkit.createWorld(cr);
                mapWorld.setAutoSave(false);
                mapWorld.setGameRuleValue("doMobSpawning", "false");
                mapWorld.setGameRuleValue("doFireTick", "false");

                mapData = new MapData(name, url, mapDataPojo.getAuthor(), mapDataPojo.getTeamMaxPlayers(), mapDataPojo.getMinPlayers(), mapDataPojo.getRange(), Util.locationConversion(mapDataPojo.getReSpawnLocation()), Util.locationConversion(mapDataPojo.getArea1Location()), Util.locationConversion(mapDataPojo.getArea2Location()), Util.locationConversion(mapDataPojo.getBaseLocations()), Util.dropConversion(mapDataPojo.getDropLocations()), Util.shopConversion(mapDataPojo.getShopLocations()));
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return mapData;
    }

    public MapData loadMap(String mapName) {
        Connection connection = null;
        ResultSet resultSet = null;
        MapData mapData = null;

        try {
            connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwdata");
            if (connection == null || connection.isClosed()) {
                return null;
            }


            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM XBWMaps Where MapName=?");
            preparedStatement.setString(1, mapName);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("MapName");
                String url = resultSet.getString("URL");
                MapDataPojo mapDataPojo = GSON.fromJson(resultSet.getString("Data"), MapDataPojo.class);

                if (new File(name).exists()) {
                    new File(name).delete();
                }

                FileUtils.copyDirectory(new File(url), new File(name));

                WorldCreator cr = new WorldCreator(name);
                cr.environment(World.Environment.NORMAL);
                World mapWorld = Bukkit.createWorld(cr);
                mapWorld.setAutoSave(false);
                mapWorld.setGameRuleValue("doMobSpawning", "false");
                mapWorld.setGameRuleValue("doFireTick", "false");

                mapData = new MapData(name, url, mapDataPojo.getAuthor(), mapDataPojo.getTeamMaxPlayers(), mapDataPojo.getMinPlayers(), mapDataPojo.getRange(), Util.locationConversion(mapDataPojo.getReSpawnLocation()), Util.locationConversion(mapDataPojo.getArea1Location()), Util.locationConversion(mapDataPojo.getArea2Location()), Util.locationConversion(mapDataPojo.getBaseLocations()), Util.dropConversion(mapDataPojo.getDropLocations()), Util.shopConversion(mapDataPojo.getShopLocations()));
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mapData;
    }

    public Location getWaitingLoc() {
        Connection connection = null;
        ResultSet resultSet = null;
        String mapname = null;

        try {
            connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwdata");
            if (connection == null || connection.isClosed()) {
                return null;
            }

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM XBWConfig Where configKey=?");
            preparedStatement.setString(1, "WaitingMapURL");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String URL = resultSet.getString("object");

                if (!NewXBedwars.getInstance().getConfig().getBoolean("updateMap")) {
                    if (new File(new File(URL).getName()).exists()) {
                        new File(new File(URL).getName()).delete();
                    }

                    FileUtils.copyDirectory(new File(URL), new File(new File(URL).getName()));

                }

                mapname = new File(URL).getName();
                WorldCreator cr = new WorldCreator(new File(URL).getName());
                cr.environment(World.Environment.NORMAL);
                World mapWorld = Bukkit.createWorld(cr);

                mapWorld.setAutoSave(false);
                mapWorld.setGameRuleValue("doMobSpawning", "false");
                mapWorld.setGameRuleValue("doFireTick", "false");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM XBWConfig Where configKey=?");
            preparedStatement.setString(1, "WaitingLoc");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Util.locationConversion(mapname, resultSet.getString("object"));
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setWaitingLoc(String sLoc) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwdata");
            if (connection == null || connection.isClosed()) {
                return;
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM XBWConfig Where configKey=?");
            preparedStatement.setString(1, "WaitingLoc");
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                preparedStatement = connection.prepareStatement("INSERT INTO XBWConfig (configKey,object) VALUES (?,?);");
                preparedStatement.setString(1, "WaitingLoc");
                preparedStatement.setString(2, sLoc);
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("UPDATE XBWConfig SET object=? Where configKey=?;");
                preparedStatement.setString(2, "WaitingLoc");
                preparedStatement.setString(1, sLoc);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private <K, V> Map.Entry<K, V> getHead(LinkedHashMap<K, V> map) {
        return map.entrySet().iterator().next();
    }

    private <K, V> Map.Entry<K, V> getMapFormID(LinkedHashMap<K, V> map, int id) {
        int i = 0;
        for (Map.Entry<K, V> e : map.entrySet()) {
            if ((i + 1) == id) {
                return e;
            }

            i++;
        }
        return null;
    }
}
