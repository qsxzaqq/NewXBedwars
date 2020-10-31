package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.Database;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import cc.i9mc.watchnmslreport.BukkitReport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {
    private final Game game = NewXBedwars.getInstance().getGame();

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (game.getGameState() == GameState.RUNNING && game.getAllowReJoin().contains(e.getPlayer().getName())) {
            e.allow();
            return;
        }

        if ((e.getPlayer().hasPermission("bw.*") || BukkitReport.getInstance().getStaffs().containsKey(e.getPlayer().getName()))) {
            e.allow();
            return;
        }

        if (game.getPlayers().size() >= game.getMaxPlayers()) {
            e.disallow(PlayerLoginEvent.Result.KICK_FULL, "开始了");
            return;
        }

        if (game.getGameState() == GameState.RUNNING) {
            e.disallow(PlayerLoginEvent.Result.KICK_FULL, "开始了");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        game.addPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        PlayerData playerData = Database.getPlayerData(event.getName());
        NewXBedwars.getInstance().getCacher().add(event.getName(), playerData);
    }
}
