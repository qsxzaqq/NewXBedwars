package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final Game game = NewXBedwars.getInstance().getGame();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        game.removePlayers(event.getPlayer());
    }
}
