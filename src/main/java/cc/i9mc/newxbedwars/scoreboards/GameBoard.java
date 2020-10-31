package cc.i9mc.newxbedwars.scoreboards;

import cc.i9mc.gameutils.utils.board.Board;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.events.BedwarsGameStartEvent;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.*;

public class GameBoard implements Listener {
    private static final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
    private static Objective hp;
    private static Objective o;

    private static Game game;

    public GameBoard(Game game) {
        GameBoard.game = game;
    }

    public static Scoreboard getBoard() {
        return sb;
    }

    public static void show(Player p) {
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
            hp.getScore(player.getName()).setScore((int) player.getHealth());
            o.getScore(player.getName()).setScore((int) player.getHealth());


            List<String> list = new ArrayList<>();
            list.add("§7团队 " + new SimpleDateFormat("MM", Locale.CHINESE).format(Calendar.getInstance().getTime()) + "/" + new SimpleDateFormat("dd", Locale.CHINESE).format(Calendar.getInstance().getTime()) + "/" + new SimpleDateFormat("yy", Locale.CHINESE).format(Calendar.getInstance().getTime()) + " ");
            list.add(" ");
            list.add(game.getEventManager().formattedNextEvent());
            list.add("§a" + game.getFormattedTime(game.getEventManager().getLeftTime()));
            list.add("  ");
            for (Team team : game.getTeams()) {
                list.add(team.getName() + " " + (team.isBedDestroy() ? "§7❤" : "§c❤") + "§f | " + (team.getAlivePlayers().size()) + " " + (team == game.getTeam(player) ? " §7(我的队伍)" : ""));
            }
            list.add("   ");
            list.add("§emcyc.win");

            board.send("§e§l超级起床战争", list);
        }
    }

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        if (hp == null) {
            hp = sb.registerNewObjective("NAME_HEALTH", "health");
            hp.setDisplaySlot(DisplaySlot.BELOW_NAME);
            hp.setDisplayName(ChatColor.RED + "❤");
        }
        if (o == null) {
            o = sb.registerNewObjective("health", "dummy");
            o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        Util.setPlayerTeamTab();

        for (Player player : Bukkit.getOnlinePlayers()) {
            show(player);
        }

        game.getEventManager().registerRunnable("计分板", (s, c) -> updateBoard());
    }
}
