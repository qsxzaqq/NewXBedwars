package cc.i9mc.newxbedwars.game;

import cc.i9mc.gameutils.utils.TitleUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.utils.Util;
import cc.i9mc.nick.Nick;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JinVan on 2020/7/3.
 */
public class SpectatorManger implements Runnable, Listener {
    private final Game game;
    @Getter
    private final List<String> spectators;
    private final Map<String, String> spectatorPlayers;

    public SpectatorManger(Game game) {
        this.game = game;
        spectators = new ArrayList<>();
        spectatorPlayers = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, NewXBedwars.getInstance());
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(NewXBedwars.getInstance(), this, 0, 1);
    }

    public void addSpectator(Player player) {
        spectators.add(player.getName());
    }

    public void addSpectator(String name) {
        spectators.add(name);
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player.getName());
    }

    public boolean isSpectator(String name) {
        return spectators.contains(name);
    }

    @Override
    public void run() {
        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        for (String name : spectators) {
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) continue;

            if (player.getLocation().getY() < 0.0D) {
                player.teleport(player.getLocation().add(0.0D, 128.0D, 0.0D));
                return;
            }

            if (player.getSpectatorTarget() == null) {
                if (spectatorPlayers.containsKey(player.getName())) {
                    spectatorPlayers.remove(player.getName());
                    NewXBedwars.getInstance().mainThreadRunnable(() -> player.setSpectatorTarget(null));
                }
                return;
            }
            if (!(player.getSpectatorTarget() instanceof Player)) return;

            Player spectatorTarget = (Player) player.getSpectatorTarget();
            TitleUtil.sendTitle(player, 10, 30, 15, "§a正在旁观§7" + Nick.get().getCache().getOrDefault(spectatorTarget.getName(), spectatorTarget.getName()), "§a点击左键打开菜单   §c按Shift键退出");
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (game.getGameState() != GameState.RUNNING) return;
        if (!isSpectator(player)) return;
        if (player.getGameMode() != GameMode.SPECTATOR && player.getSpectatorTarget() == null) return;
        if (!(player.getSpectatorTarget() instanceof Player)) return;

        player.setSpectatorTarget(null);
        TitleUtil.sendTitle(player, 10, 30, 15, "", "§e退出旁观模式");
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        player.setAllowFlight(true);
        Util.setFlying(player);
        spectatorPlayers.remove(player.getName());
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (game.getGameState() != GameState.RUNNING) return;
        if (!game.getSpectatorManger().isSpectator(player)) return;
        if (!(event.getRightClicked() instanceof Player)) return;

        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(event.getRightClicked());
        spectatorPlayers.put(player.getName(), player.getName());
    }
}
