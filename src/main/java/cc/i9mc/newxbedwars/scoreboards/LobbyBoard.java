package cc.i9mc.newxbedwars.scoreboards;

import cc.i9mc.gameutils.utils.board.Board;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameLobbyCountdown;
import cc.i9mc.newxbedwars.game.GameState;
import cc.i9mc.newxbedwars.types.ModeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LobbyBoard implements Listener {
    private static final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
    private static Objective hp;
    private static Objective o;


    private static Game game;

    public LobbyBoard(Game game) {
        LobbyBoard.game = game;
    }

    public static Scoreboard getBoard() {
        return sb;
    }

    public static void show(Player p) {
        if (hp == null) {
            hp = sb.registerNewObjective("NAME_HEALTH", "health");
            hp.setDisplaySlot(DisplaySlot.BELOW_NAME);
            hp.setDisplayName(ChatColor.GOLD + "✫");
        }
        if (o == null) {
            o = sb.registerNewObjective("health", "dummy");
            o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        p.setScoreboard(sb);
    }

    public static void updateBoard() {
        for (Map.Entry<UUID, Board> boardEntry : NewXBedwars.getInstance().getBoardManager().getBoardMap().entrySet()) {
            UUID uuid = boardEntry.getKey();
            Board board = boardEntry.getValue();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            PlayerData playerData = NewXBedwars.getInstance().getCacher().get(player.getName());
            if (playerData == null) {
                continue;
            }
            hp.getScore(player.getName()).setScore(NewXBedwars.getInstance().getLevel((playerData.getOldKills() * 2) + (playerData.getOldDestroyedBeds() * 10) + (playerData.getOldWins() * 15)));
            o.getScore(player.getName()).setScore(NewXBedwars.getInstance().getLevel((playerData.getOldKills() * 2) + (playerData.getOldDestroyedBeds() * 10) + (playerData.getOldWins() * 15)));

            List<String> list = new ArrayList<>();
            list.add(" ");
            list.add("§f地图: §a" + game.getMapData().getName());
            list.add("§f队伍: §a" + game.getMapData().getTeamMaxPlayers() + "人 " + game.getTeams().size() + "队");
            list.add("§f作者: §a" + game.getMapData().getAuthor());
            list.add("  ");
            list.add("§f玩家: §a" + game.getPlayers().size() + "/" + game.getMaxPlayers());
            list.add("   ");
            list.add(getCountdown());
            list.add("    ");
            list.add("§f你的模式: §a" + (playerData.getModeType() == ModeType.DEFAULT ? "普通模式" : "经验模式"));
            list.add("     ");
            list.add("§f版本: §a" + NewXBedwars.getInstance().getDescription().getVersion());
            list.add("§f服务器: §aBW-HYP-" + String.valueOf(Bukkit.getPort()).substring(String.valueOf(Bukkit.getPort()).length() - 2));
            list.add("      ");
            list.add("§emcyc.win");

            board.send("§e§l超级起床战争", list);
        }
    }

    private static String getCountdown() {
        Game game = NewXBedwars.getInstance().getGame();
        GameLobbyCountdown gameLobbyCountdown = game.getGameLobbyCountdown();

        if (gameLobbyCountdown != null) {
            return gameLobbyCountdown.getCountdown() + "秒后开始";
        } else if (game.getGameState() == GameState.WAITING) {
            return "§f等待中...";
        }

        return null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        LobbyBoard.updateBoard();
    }
}
