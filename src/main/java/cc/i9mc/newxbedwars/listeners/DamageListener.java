package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.gameutils.utils.ActionBarUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.events.BedwarsPlayerKilledEvent;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.nick.Nick;
import cc.i9mc.rejoin.events.RejoinGameDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageListener implements Listener {
    private final Game game = NewXBedwars.getInstance().getGame();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("Shop") || event.getEntity().hasMetadata("Shop2")) {
            event.setCancelled(true);
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();

        if (game.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (game.getGameLobbyCountdown() != null) {
                    if (game.getGameLobbyCountdown().getCountdown() < 3) {
                        return;
                    }
                }
                player.teleport(game.getWaitingLocation());
                return;
            }
        }

        if (game.getGameState() == GameState.RUNNING) {
            if (game.getEventManager().isOver()) {
                event.setCancelled(true);
                return;
            }

            if (game.getSpectatorManger().isSpectator(player)) {
                event.setCancelled(true);
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setDamage(100.0D);

                Player killer = player.getKiller();
                Team team = game.getTeam(player);
                Team killerTeam = game.getTeam(killer);

                if (killer != null && killerTeam != null) {
                    if (team.isBedDestroy()) {
                        Player finalKiller = killer;
                        new BukkitRunnable() {
                            int i = 0;

                            @Override
                            public void run() {
                                if (i == 5) {
                                    cancel();
                                    return;
                                }
                                ActionBarUtil.sendBar(finalKiller, "§6+1个金币");
                                i++;
                            }
                        }.runTaskTimerAsynchronously(NewXBedwars.getInstance(), 0, 10);
                        killer.sendMessage("§6+1个金币 (最终击杀)");
                        NewXBedwars.getInstance().getEcon().depositPlayer(player, 1);

                        game.broadcastMessage(team.getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "(" + team.getName() + "♛)[最终击杀]§e被" + killerTeam.getChatColor() + Nick.get().getCache().getOrDefault(killer.getName(), killer.getName()) + "(" + killerTeam.getName() + "♛)§e狠狠滴丢下虚空");
                    } else {
                        game.broadcastMessage(team.getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "(" + team.getName() + "♛)§e被" + killerTeam.getChatColor() + Nick.get().getCache().getOrDefault(killer.getName(), killer.getName()) + "(" + killerTeam.getName() + "♛)§e狠狠滴丢下虚空");
                    }
                    NewXBedwars.getInstance().getCacher().get(killer.getName()).setKills(NewXBedwars.getInstance().getCacher().get(killer.getName()).getKills() + 1);
                    Bukkit.getPluginManager().callEvent(new BedwarsPlayerKilledEvent(player, killer, team.isBedDestroy()));
                } else {
                    game.broadcastMessage(team.getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "(" + team.getName() + "♛)§e划下了虚空");
                    NewXBedwars.getInstance().getCacher().get(player.getName()).setDeaths(NewXBedwars.getInstance().getCacher().get(player.getName()).getDeaths() + 1);
                }
                player.setMetadata("voidPlayer", new FixedMetadataValue(NewXBedwars.getInstance(), ""));
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && player.hasMetadata("FIREBALL PLAYER NOFALL")) {
                event.setCancelled(true);
                player.removeMetadata("FIREBALL PLAYER NOFALL", NewXBedwars.getInstance());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team team = game.getTeam(player);

        event.setDeathMessage(null);
        event.setKeepInventory(true);
        event.getDrops().clear();
        event.getEntity().getInventory().clear();
        event.setDroppedExp(0);

        if (game.getGameState() == GameState.WAITING) {
            return;
        }

        if (game.getSpectatorManger().isSpectator(player)) {
            return;
        }

        if (!player.hasMetadata("voidPlayer")) {
            Player killer = player.getKiller();
            if (killer == null) killer = game.getPlayerDamager(player);
            Team killerTeam = game.getTeam(killer);

            if (killer != null && killerTeam != null) {
                if (team.isBedDestroy()) {
                    Player finalKiller = killer;
                    new BukkitRunnable() {
                        int i = 0;

                        @Override
                        public void run() {
                            if (i == 5) {
                                cancel();
                                return;
                            }
                            ActionBarUtil.sendBar(finalKiller, "§6+1个金币");
                            i++;
                        }
                    }.runTaskTimerAsynchronously(NewXBedwars.getInstance(), 0, 10);
                    killer.sendMessage("§6+1个金币 (最终击杀)");
                    NewXBedwars.getInstance().getEcon().depositPlayer(player, 1);

                    game.broadcastMessage(team.getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "(" + team.getName() + "♛)[最终击杀]§e被" + killerTeam.getChatColor() + Nick.get().getCache().getOrDefault(killer.getName(), killer.getName()) + "(" + killerTeam.getName() + "♛)§e狠狠滴推倒");
                    Bukkit.getPluginManager().callEvent(new RejoinGameDeathEvent(player));
                } else {
                    game.broadcastMessage(team.getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "(" + team.getName() + "♛)§e被" + killerTeam.getChatColor() + Nick.get().getCache().getOrDefault(killer.getName(), killer.getName()) + "(" + killerTeam.getName() + "♛)§e狠狠滴推倒");
                }

                Bukkit.getPluginManager().callEvent(new BedwarsPlayerKilledEvent(player, killer, team.isBedDestroy()));

                NewXBedwars.getInstance().getCacher().get(killer.getName()).setKills(NewXBedwars.getInstance().getCacher().get(killer.getName()).getKills() + 1);
                NewXBedwars.getInstance().getCacher().get(player.getName()).setDeaths(NewXBedwars.getInstance().getCacher().get(player.getName()).getDeaths() + 1);
            }
        }

        player.removeMetadata("voidPlayer", NewXBedwars.getInstance());
        Bukkit.getScheduler().runTaskLater(NewXBedwars.getInstance(), () -> {
            player.spigot().respawn();
            game.getPlayers().forEach((player1 -> player1.hidePlayer(player)));
            player.hidePlayer(player);
        }, 10L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (entity instanceof Player || damager instanceof Player || damager instanceof Projectile) {
            if (game.getGameState() == GameState.RUNNING) {
                if (damager instanceof Player && entity instanceof Player) {
                    if (game.getSpectatorManger().isSpectator((Player) damager)) {
                        event.setCancelled(true);
                    }

                    if (game.getTeam((Player) damager) == game.getTeam((Player) entity)) {
                        event.setCancelled(true);
                    } else {
                        game.setPlayerDamager((Player) entity, (Player) damager);
                    }
                } else if (entity instanceof Player && damager instanceof Projectile) {
                    Projectile projectile = (Projectile) damager;

                    if (projectile.getType() == EntityType.FIREBALL) {
                        event.setCancelled(true);
                        return;
                    }

                    if (projectile.getShooter() instanceof Player) {
                        if (game.getTeam((Player) projectile.getShooter()) == game.getTeam((Player) entity)) {
                            event.setCancelled(true);
                        } else {
                            game.setPlayerDamager((Player) entity, (Player) projectile.getShooter());
                        }
                    }
                }
            }
        }
    }
}
