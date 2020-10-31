package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {
    private final Game game = NewXBedwars.getInstance().getGame();

    @EventHandler
    public void onUnload(ChunkUnloadEvent unload) {
        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        if (!NewXBedwars.getInstance().getMapData().chunkIsInRegion(unload.getChunk().getX(), unload.getChunk().getZ())) {
            return;
        }

        unload.setCancelled(true);
    }

}
