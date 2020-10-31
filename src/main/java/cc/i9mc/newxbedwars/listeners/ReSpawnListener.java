package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.gameutils.utils.TitleUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.types.ToolType;
import cc.i9mc.newxbedwars.utils.SoundUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReSpawnListener implements Listener {
    private final List<UUID> noDamage = new ArrayList<>();
    private final Game game = NewXBedwars.getInstance().getGame();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Team team = game.getTeam(player);
        PlayerData playerData = NewXBedwars.getInstance().getCacher().get(player.getName());

        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        game.clearPlayer(player);

        if (team.isBedDestroy() || team.isDie()) {
            TextComponent textComponent = new TextComponent("§c你凉了!想再来一局嘛? ");
            textComponent.addExtra("§b§l点击这里!");
            textComponent.getExtra().get(0).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/queue join qc x"));
            player.spigot().sendMessage(textComponent);

            event.setRespawnLocation(game.getMapData().getReSpawnLocation());
            player.setVelocity(new Vector(0, 0, 0));
            player.setFallDistance(0.0F);
            player.teleport(game.getMapData().getReSpawnLocation());
            game.getPlayers().forEach((player1 -> player1.hidePlayer(player)));
            TitleUtil.sendTitle(player, 10, 20, 10, "§c你凉了！", "§7你没床了");

            game.toSpectator(player);

            if (team.getPlayers().isEmpty()) {
                playerData.setLoses(playerData.getLoses() + 1);
                playerData.setGames(playerData.getGames() + 1);
            }

            if (team.getPlayers().isEmpty() && !team.isDie()) {
                game.broadcastSound(SoundUtil.get("ENDERDRAGON_HIT", "ENTITY_ENDERDRAGON_HURT"), 10, 10);
                game.broadcastMessage("§7▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
                game.broadcastMessage(" ");
                game.broadcastMessage(team.getChatColor() + team.getName() + " §c凉了! §e挖床者: " + (team.getDestroyPlayer() != null ? team.getDestroyPlayer() : "null"));
                game.broadcastMessage(" ");
                game.broadcastMessage("§7▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");

                team.setBedDestroy(true);
                team.setDie(true);
            }


            game.getSpectatorManger().addSpectator(player);
            return;
        }

        event.setRespawnLocation(game.getMapData().getReSpawnLocation());
        player.setGameMode(GameMode.SPECTATOR);
        player.setVelocity(new Vector(0, 0, 0));
        player.setFallDistance(0.0F);
        player.teleport(game.getMapData().getReSpawnLocation());
        player.setFlying(true);

        new BukkitRunnable() {
            int delay = 5;

            @Override
            public void run() {
                if (player.isOnline()) {
                    if (this.delay > 0) {
                        TitleUtil.sendTitle(player, 1, 20, 1, "§e§l" + delay, "§7你死了,将在稍后重生");
                        this.delay -= 1;
                        return;
                    }

                    player.setExp(0f);
                    player.setLevel(0);
                    if (game.getPickAxe().containsKey(player.getUniqueId()) && game.getPickAxe().get(player.getUniqueId()) != ToolType.WOOD) {
                        ToolType toolType = game.getPickAxe().get(player.getUniqueId());
                        if (toolType == ToolType.MAX) {
                            game.getPickAxe().put(player.getUniqueId(), ToolType.DIAMOND);
                        } else if (toolType == ToolType.DIAMOND) {
                            game.getPickAxe().put(player.getUniqueId(), ToolType.IRON);
                        } else if (toolType == ToolType.IRON) {
                            game.getPickAxe().put(player.getUniqueId(), ToolType.STONE);
                        }
                    }

                    if (game.getAxe().containsKey(player.getUniqueId()) && game.getAxe().get(player.getUniqueId()) != ToolType.WOOD) {
                        ToolType toolType = game.getAxe().get(player.getUniqueId());
                        if (toolType == ToolType.MAX) {
                            game.getAxe().put(player.getUniqueId(), ToolType.DIAMOND);
                        } else if (toolType == ToolType.DIAMOND) {
                            game.getAxe().put(player.getUniqueId(), ToolType.IRON);
                        } else if (toolType == ToolType.IRON) {
                            game.getAxe().put(player.getUniqueId(), ToolType.STONE);
                        }
                    }

                    game.giveInventory(player);
                    player.showPlayer(player);
                    game.getPlayers().forEach((player1 -> player1.showPlayer(player)));
                    player.teleport(team.getSpawn());
                    player.setGameMode(GameMode.SURVIVAL);
                    noDamage.add(player.getUniqueId());

                    game.setPlayerDamager(player, null);

                    Bukkit.getScheduler().runTaskLater(NewXBedwars.getInstance(), () -> noDamage.remove(player.getUniqueId()), 60);

                    TitleUtil.sendTitle(player, 1, 20, 1, "§a已复活！", "§7因为你的床还在所以你复活了");
                }
                cancel();
            }
        }.runTaskTimer(NewXBedwars.getInstance(), 20L, 20L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent evt) {
        if (noDamage.contains(evt.getEntity().getUniqueId())) {
            evt.setCancelled(true);
        }
    }
}
