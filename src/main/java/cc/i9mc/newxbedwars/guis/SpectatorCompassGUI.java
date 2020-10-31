package cc.i9mc.newxbedwars.guis;

import cc.i9mc.gameutils.gui.CustonGUI;
import cc.i9mc.gameutils.gui.GUIAction;
import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.nick.Nick;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpectatorCompassGUI extends CustonGUI {

    public SpectatorCompassGUI(Player player, Game game) {
        super(player, "§8选择一个玩家来传送", game.getPlayers().size() <= 27 ? 27 : 54);

        int i = 0;
        for (Player players : game.getGamePlayers()) {
            Team team = game.getTeam(players);
            if (team == null) {
                continue;
            }

            if (game.getSpectatorManger().isSpectator(players)) {
                continue;
            }

            setItem(i, new ItemBuilderUtil().setOwner(players.getName()).setDisplayName(team.getName() + " " + (Nick.get().getCache().get(players.getName()) != null ? Nick.get().getCache().get(players.getName()) : players.getName())).setLores(" ", "§f血量: §8" + (int) players.getHealth(), "§f饥饿: §8" + players.getFoodLevel(), "§f等级: §8" + players.getLevel(), "§f距离: §8" + (int) players.getLocation().distance(player.getLocation())).getItem(), new GUIAction(0, () -> {
                if (player.getGameMode() != GameMode.SPECTATOR) {
                    if (game.getSpectatorManger().isSpectator(player)) {
                        if (player.getSpectatorTarget() != null) {
                            if ((player.getSpectatorTarget() instanceof Player)) {
                                player.setSpectatorTarget(null);
                            }
                        }
                    }
                }

                player.teleport(players);
            }, true));
            i++;
        }
    }
}
