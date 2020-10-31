package cc.i9mc.newxbedwars.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsPlayerKilledEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player = null;
    private Player killer = null;
    private boolean last = false;

    public BedwarsPlayerKilledEvent(Player player, Player killer, boolean last) {
        this.player = player;
        this.killer = killer;
        this.last = last;
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

    public Player getKiller() {
        return this.killer;
    }

    public boolean isLast() {
        return last;
    }
}
