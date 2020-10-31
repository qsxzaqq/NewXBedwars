package cc.i9mc.newxbedwars.game;

import cc.i9mc.gameutils.utils.TitleUtil;
import cc.i9mc.newxbedwars.scoreboards.LobbyBoard;
import cc.i9mc.newxbedwars.utils.SoundUtil;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

public class GameLobbyCountdown extends BukkitRunnable {
    private final Game game;
    @Getter
    private int countdown = 120;
    private boolean s = false;

    GameLobbyCountdown(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.isForceStart()) {
            cancel();
        }

        if (!game.hasEnoughPlayers()) {
            s = false;
            game.broadcastMessage("§c人数不足，取消倒计时！");
            countdown = 120;
            game.setGameState(GameState.WAITING);
            game.setGameLobbyCountdown(null);
            LobbyBoard.updateBoard();
            cancel();
        }

        if (countdown == 60 || countdown == 30 || countdown <= 5 && countdown > 0) {
            game.getPlayers().forEach(player -> {
                player.sendMessage("§e游戏将在§c" + countdown + "§e秒后开始！");
                TitleUtil.sendTitle(player, 1, 20, 1, "§c§l" + countdown, "§e§l准备战斗吧！");
                player.playSound(player.getLocation(), SoundUtil.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 1F, 10F);
            });
        }

        if (!s && game.getPlayers().size() == game.getMaxPlayers()) {
            s = true;
            countdown = 10;
            game.broadcastMessage("§e游戏人数已满,10秒后开始游戏！");
        }

        if (countdown <= 0) {
            cancel();

            game.start();
        }

        LobbyBoard.updateBoard();

        game.getPlayers().forEach(player -> {
            player.setLevel(countdown);
            if (countdown == 120) {
                player.setExp(1.0F);
            } else {
                player.setExp(1.0F - ((1.0F / 120) * (120 - countdown)));
            }
        });

        --countdown;
    }
}
