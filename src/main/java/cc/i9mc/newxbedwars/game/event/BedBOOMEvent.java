package cc.i9mc.newxbedwars.game.event;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.Team;
import org.bukkit.Material;
import org.bukkit.Sound;

public class BedBOOMEvent extends GameEvent {
    public BedBOOMEvent() {
        super("床自毁", 360, 5);
    }

    @Override
    public void excute(Game game) {
        for (Team team : game.getTeams()) {
            if (team.isBedDestroy()) continue;
            NewXBedwars.getInstance().mainThreadRunnable(() -> {
                team.getBedHead().setType(Material.AIR);
                team.getBedFeet().setType(Material.AIR);
            });
            team.setBedDestroy(true);
        }

        game.broadcastSound(Sound.ENDERDRAGON_GROWL, 1, 1);
        game.broadcastTitle(10, 20, 10, "§c§l床自毁", "§e所有队伍床消失");
    }

    @Override
    public void excuteRunnbale(Game game, int seconds) {

    }
}
