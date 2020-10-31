package cc.i9mc.newxbedwars.specials;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RescuePlatformListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        if (ev.getAction().equals(Action.LEFT_CLICK_AIR) || ev.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        Player player = ev.getPlayer();
        Game game = NewXBedwars.getInstance().getGame();

        if (game == null) {
            return;
        }

        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        RescuePlatform platform = new RescuePlatform();
        if (!ev.getMaterial().equals(platform.getItemMaterial())) {
            return;
        }

        platform.create(player, game);
    }

}
