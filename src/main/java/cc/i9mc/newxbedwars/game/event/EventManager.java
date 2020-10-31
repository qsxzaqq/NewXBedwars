package cc.i9mc.newxbedwars.game.event;

import cc.i9mc.gameutils.utils.ActionBarUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.Team;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class EventManager extends TimerTask {
    private final Game game;
    private Timer timer;
    private int currentEvent = 0;
    private int seconds = 0;
    @Getter
    private boolean over = false;
    @Getter
    private final HashMap<String, Runnable> runnables = new HashMap<>();
    private final HashMap<Integer, GameEvent> events = new HashMap<>();

    public EventManager(Game game) {
        this.game = game;
        this.registerEvent(new StartEvent());
        this.registerEvent(new DiamondUpdateEvent(2, 360, 1));
        this.registerEvent(new EmeraldUpdateEvent(2, 360, 2));
        this.registerEvent(new DiamondUpdateEvent(3, 360, 3));
        this.registerEvent(new EmeraldUpdateEvent(3, 360, 4));
        this.registerEvent(new BedBOOMEvent());
        this.registerEvent(new OverEvent());
        this.registerEvent(new EndEvent());
    }

    @Override
    public void run() {
        GameEvent event = this.currentEvent();

        event.excuteRunnbale(this.game, event.getExcuteSeconds() - seconds);
        if (this.seconds >= event.getExcuteSeconds()) {
            this.seconds = 0;
            this.currentEvent = event.getPriority() + 1;
            event.excute(this.game);
        }

        if (game.isOver() && !over) {
            Bukkit.getScheduler().runTaskLater(NewXBedwars.getInstance(), () -> {
                setCurrentEvent(6);
                currentEvent().excute(game);
            }, 40L);
            over = true;
        }

        for (Player player1 : game.getGamePlayers()) {
            if (game.getSpectatorManger().isSpectator(player1)) {
                continue;
            }

            if (player1.getInventory().contains(new ItemStack(Material.COMPASS))) {
                Player target = game.findTargetPlayer(player1);
                if (target != null) {
                    NewXBedwars.getInstance().mainThreadRunnable(() -> player1.setCompassTarget(target.getLocation()));
                }
            }
        }

        for (Team team : game.getTeams())
            team.getPlayers().forEach(player -> {
                if (player.getLocation().getWorld().equals(team.getSpawn().getWorld()))
                    ActionBarUtil.sendBar(player, "§f队伍: " + team.getChatColor() + team.getName() + "§f 追踪: " + team.getChatColor() + (int) player.getLocation().distance(team.getSpawn()) + "m");
            });


        runnables.values().forEach(runnable -> {
            if (runnable.getSeconds() != 0) {
                if (runnable.getNextSeconds() == runnable.getSeconds()) {
                    runnable.getEvent().run(runnable.getSeconds() - runnable.getNextSeconds(), currentEvent);
                    runnable.setNextSeconds(0);
                }
                runnable.setNextSeconds(runnable.getNextSeconds() + 1);
            } else {
                runnable.getEvent().run(seconds, currentEvent);
            }
        });

        ++this.seconds;
    }

    public GameEvent currentEvent() {
        return this.events.getOrDefault(this.currentEvent, this.events.get(6));
    }

    public void setCurrentEvent(int priority) {
        this.seconds = 0;
        this.currentEvent = priority;
    }

    public int getLeftTime() {
        return this.currentEvent().getExcuteSeconds() - this.seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public String formattedNextEvent() {
        GameEvent currentEvent = this.currentEvent();
        return currentEvent instanceof EndEvent ? currentEvent.getName() : currentEvent.getName();
    }

    public void registerRunnable(String name, Runnable.Event runnable) {
        this.runnables.put(name, new Runnable(0, 0, runnable));
    }

    public void registerRunnable(String name, Runnable.Event runnable, int seconds) {
        this.runnables.put(name, new Runnable(seconds, 0, runnable));
    }

    private void registerEvent(GameEvent event) {
        this.events.put(event.getPriority(), event);
    }

    public void start() {
        if (this.timer == null) {
            timer = new Timer();
            timer.schedule(this, 0, 1000);
        }
    }

    public void stop() {
        if (this.timer != null) {
            timer.cancel();
            this.currentEvent = 0;
            this.seconds = 0;
        }
    }
}
