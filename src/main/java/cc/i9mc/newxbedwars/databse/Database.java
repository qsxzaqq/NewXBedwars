package cc.i9mc.newxbedwars.databse;

import cc.i9mc.gameutils.BukkitGameUtils;
import cc.i9mc.newxbedwars.types.ModeType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    public static PlayerData getPlayerData(String name) {
        PlayerData returnValue = null;

        try {
            Connection connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwstats");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bw_stats_players Where Name=?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                returnValue = new PlayerData(name, ModeType.valueOf(resultSet.getString("Mode")), resultSet.getInt("kills"), resultSet.getInt("destroyedBeds"), resultSet.getInt("wins"));
            } else {
                returnValue = new PlayerData(name);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public static String[] getPlayerShopSort(String name) {
        String[] returnValue = null;

        try {
            Connection connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwstats");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bw_shop_players Where Name=?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                returnValue = resultSet.getString("data").split(", ");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public static void savePlayerShopSort(String name, boolean has, String[] data) {
        try {
            Connection connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwstats");
            PreparedStatement preparedStatement;

            StringBuilder string = null;
            for(String s : data){
                if(string == null){
                    string = new StringBuilder(s + ", ");
                    continue;
                }

                string.append(s).append(", ");
            }

            if (!has) {
                preparedStatement = connection.prepareStatement("INSERT INTO bw_shop_players (Name,data) VALUES (?,?)");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, string.toString().substring(0, string.length() - 2));
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("UPDATE bw_shop_players SET data=? Where Name=?");
                preparedStatement.setString(1, string.toString().substring(0, string.length() - 2));
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();
            }


            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void savePlayerData(PlayerData playerData) {
        try {
            Connection connection = BukkitGameUtils.getInstance().getConnectionPoolHandler().getConnection("bwstats");
            PreparedStatement preparedStatement;

            if (!playerData.isHas()) {
                preparedStatement = connection.prepareStatement("INSERT INTO bw_stats_players (Name,Mode,kills,deaths,destroyedBeds,wins,loses,games) VALUES (?,?,?,?,?,?,?,?)");
                preparedStatement.setString(1, playerData.getName());
                preparedStatement.setString(2, playerData.getModeType().toString());
                preparedStatement.setInt(3, playerData.getKills());
                preparedStatement.setInt(4, playerData.getDeaths());
                preparedStatement.setInt(5, playerData.getDestroyedBeds());
                preparedStatement.setInt(6, playerData.getWins());
                preparedStatement.setInt(7, playerData.getLoses());
                preparedStatement.setInt(8, playerData.getGames());
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("SELECT * FROM bw_stats_players Where Name=?");
                preparedStatement.setString(1, playerData.getName());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE bw_stats_players SET Mode=?,kills=?,deaths=?,destroyedBeds=?,wins=?,loses=?,games=? Where Name=?");
                    preparedStatement.setString(1, playerData.getModeType().toString());
                    preparedStatement.setInt(2, resultSet.getInt("kills") + playerData.getKills());
                    preparedStatement.setInt(3, resultSet.getInt("deaths") + playerData.getDeaths());
                    preparedStatement.setInt(4, resultSet.getInt("destroyedBeds") + playerData.getDestroyedBeds());
                    preparedStatement.setInt(5, resultSet.getInt("wins") + playerData.getWins());
                    preparedStatement.setInt(6, resultSet.getInt("loses") + playerData.getLoses());
                    preparedStatement.setInt(7, resultSet.getInt("games") + playerData.getGames());
                    preparedStatement.setString(8, playerData.getName());
                    preparedStatement.executeUpdate();
                }
                resultSet.close();
            }


            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
