package cc.i9mc.newxbedwars.game.event;

import cc.i9mc.gameutils.utils.TitleUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GeneratorRunnable;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.utils.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StartEvent extends GameEvent {
    public StartEvent() {
        super("开始游戏", 5, 0);
    }

    public void excuteRunnbale(Game game, int seconds) {
        game.broadcastSound(SoundUtil.get("CLICK", "UI_BUTTON_CLICK"), 1f, 1f);
        game.broadcastTitle(1, 20, 1, "§c§l游戏即将开始", "§e§l" + seconds);
    }

    public void excute(Game game) {
        game.getEventManager().registerRunnable("团队升级", (s, c) -> game.getGamePlayers().forEach(player -> {
            if (!game.getSpectatorManger().isSpectator(player)) {
                for (Team team : game.getTeams()) {
                    if (team.getManicMiner() > 0) {
                        NewXBedwars.getInstance().mainThreadRunnable(() -> team.getPlayers().forEach((player1 -> player1.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 40, team.getManicMiner())))));
                    }

                    if (team == game.getTeam(player)) {
                        if (player.getLocation().distance(team.getSpawn()) <= 7 && team.isHealPool()) {
                            NewXBedwars.getInstance().mainThreadRunnable(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1)));
                        }

                        continue;
                    }

                    if (player.getLocation().distance(team.getSpawn()) <= 20 && !team.isDie() && team.getPlayers().size() != 0) {
                        if (team.isTrap()) {
                            team.setTrap(false);

                            NewXBedwars.getInstance().mainThreadRunnable(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1)));
                            NewXBedwars.getInstance().mainThreadRunnable(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1)));

                            NewXBedwars.getInstance().mainThreadRunnable(() -> team.getPlayers().forEach((player1 -> {
                                TitleUtil.sendTitle(player1, 0, 20, 0, "§c§l陷阱触发！", "");
                                player1.playSound(player1.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                            })));
                        }

                        if (team.isMiner()) {
                            NewXBedwars.getInstance().mainThreadRunnable(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 0)));
                            team.setMiner(false);
                        }
                    }
                }
            }
        }));
        new GeneratorRunnable(game).start();
    }
}
