package cc.i9mc.newxbedwars.game.event;

import cc.i9mc.newxbedwars.events.BedwarsGameEndEvent;
import cc.i9mc.newxbedwars.game.Game;
import org.bukkit.Bukkit;

public class EndEvent extends GameEvent {
    public EndEvent() {
        super("游戏结束！", 30, 7);
    }

    public void excute(Game game) {
        Bukkit.getPluginManager().callEvent(new BedwarsGameEndEvent());
        Bukkit.shutdown();
    }

    @Override
    public void excuteRunnbale(Game game, int seconds) {

    }
}
