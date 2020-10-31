package cc.i9mc.newxbedwars.game;

import cc.i9mc.gameutils.utils.BungeeUtil;
import cc.i9mc.gameutils.utils.FireWorkUtil;
import cc.i9mc.k8sgameack.events.ACKGameEndEvent;
import cc.i9mc.k8sgameack.events.ACKGameStartEvent;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.nick.Nick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameOverRunnable extends BukkitRunnable {
    private final String[] lead = new String[]{"§e§l击杀数第一名", "§6§l击杀数第二名", "§c§l击杀数第三名"};
    private final Game game;
    private int counter = 15;
    private boolean b = true;

    public GameOverRunnable(Game game) {
        this.game = game;
        this.runTaskTimer(NewXBedwars.getInstance(), 0L, 20L);
    }

    public void run() {
        Team winner = this.game.getWinner();
        if (this.counter > 0) {
            if (this.b) {
                this.game.getTeams().forEach(team -> {
                    boolean isWinner = winner != null && winner.getName().equals(team.getName());

                    if (isWinner) {
                        this.game.broadcastTeamTitle(team, 0, 40, 0, "§6§l获胜！", "§7你获得了最终的胜利");
                    } else {
                        this.game.broadcastTeamTitle(team, 0, 40, 0, "§c§l失败！", "§7你输掉了这场游戏");
                    }
                });

                StringBuilder winnerText = new StringBuilder("§7");
                if (winner != null) {
                    for (Player player : winner.getPlayers()) {
                        if (game.getSpectatorManger().isSpectator(player)) {
                            continue;
                        }

                        winnerText.append(Nick.get().getCache().getOrDefault(player.getName(), player.getName())).append(", ");
                    }

                    winnerText = new StringBuilder(winnerText.substring(0, winnerText.length() - 2));
                } else {
                    winnerText.append("无");
                }


                List<String> messages = new ArrayList<>();
                messages.add("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                messages.add("§f                                              §l起床战争");
                messages.add(" ");
                messages.add("                                    §e胜利者 §7- " + winnerText.toString());
                messages.add(" ");
                int i = 0;
                for (PlayerData playerData : NewXBedwars.getInstance().getCacher().sortKills()) {
                    if (i > 2) {
                        continue;
                    }

                    messages.add("                          " + this.lead[i] + " §7- " + Nick.get().getCache().getOrDefault(playerData.getName(), playerData.getName()) + " - " + playerData.getKills());
                    i++;
                }
                messages.add(" ");
                messages.add("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

                for (String line : messages) {
                    Bukkit.broadcastMessage(line);
                }

                this.b = false;

                for (PlayerData playerData : NewXBedwars.getInstance().getCacher().getData().values()) {
                    playerData.setGames(playerData.getGames() + 1);
                }
            }

            if (winner != null) {
                winner.getPlayers().forEach(player -> FireWorkUtil.spawnFireWork(player.getLocation().add(0.0D, 2.0D, 0.0D), player.getLocation().getWorld()));
            }
        }

        if (this.counter == 0) {
            Bukkit.getOnlinePlayers().forEach(player -> BungeeUtil.send("BW-Lobby-1", player));
            Bukkit.getPluginManager().callEvent(new ACKGameEndEvent());

            (new BukkitRunnable() {
                public void run() {
                    Bukkit.shutdown();
                }
            }).runTaskLater(NewXBedwars.getInstance(), 40L);
            this.cancel();
        }

        --this.counter;
    }
}
