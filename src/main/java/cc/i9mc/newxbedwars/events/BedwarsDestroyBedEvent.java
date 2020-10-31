package cc.i9mc.newxbedwars.events;

import cc.i9mc.newxbedwars.game.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsDestroyBedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player player = null;
    private Team team = null;
    private boolean cancelled = false;

    public BedwarsDestroyBedEvent(Player player, Team team) {
        this.player = player;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Team getTeam() {
        return this.team;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
