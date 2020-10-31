package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ServerListener implements Listener {
    private final Game game = NewXBedwars.getInstance().getGame();

    @EventHandler
    public void onSpawnMobHub(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND && event.getEntityType() != EntityType.VILLAGER) {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMaxPlayers(game.getMaxPlayers());
        if (game.getGameState() == GameState.RUNNING) {
            event.setMotd("开始了");
            return;
        }

        if (game.getPlayers().size() >= game.getMaxPlayers()) {
            event.setMotd("开始了");
            return;
        }

        event.setMotd("lobby");
    }
}
