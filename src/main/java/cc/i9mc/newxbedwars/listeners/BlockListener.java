package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.gameutils.utils.ActionBarUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.newxbedwars.databse.map.DropType;
import cc.i9mc.newxbedwars.events.BedwarsDestroyBedEvent;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.utils.SoundUtil;
import cc.i9mc.newxbedwars.utils.Util;
import cc.i9mc.nick.Nick;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockListener implements Listener {
    private final Game game = NewXBedwars.getInstance().getGame();
    private final HashMap<Player, Long> timeout = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (game.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
            return;
        }

        if (game.getSpectatorManger().isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (block.getType().toString().startsWith("BED")) {
            event.setCancelled(true);
            return;
        }

        if (NewXBedwars.getInstance().getMapData().hasRegion(block.getLocation())) {
            event.setCancelled(true);
            return;
        }

        for (Team team : game.getTeams()) {
            if (team.getSpawn().distance(block.getLocation()) <= 5) {
                event.setCancelled(true);
                return;
            }
        }

        for (Location location : game.getMapData().getDropLocations().keySet()) {
            if (location.distance(block.getLocation()) <= 3) {
                event.setCancelled(true);
                return;
            }
        }

        if (block.getType() == Material.TNT) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);

            TNTPrimed tnt = event.getBlock().getWorld().spawn(block.getLocation().add(0.5D, 0.0D, 0.5D), TNTPrimed.class);
            tnt.setVelocity(new Vector(0, 0, 0));

            if (player.getItemInHand().getType() == Material.TNT) {
                if (player.getItemInHand().getAmount() == 1) {
                    event.getPlayer().getInventory().setItemInHand(null);
                } else {
                    event.getPlayer().getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                }
            }
            return;
        }

        if (event.getItemInHand().getType() == Material.WOOL && !event.getItemInHand().getEnchantments().isEmpty()) {
            if (Math.abs(System.currentTimeMillis() - timeout.getOrDefault(event.getPlayer(), 0L)) < 1000) {
                event.setCancelled(true);
                return;
            }

            if (block.getY() != event.getBlockAgainst().getY()) {
                if (Math.max(Math.abs(player.getLocation().getX() - (block.getX() + 0.5D)), Math.abs(player.getLocation().getZ() - (block.getZ() + 0.5D))) < 0.5) {
                    return;
                }
            }

            BlockFace blockFace = event.getBlockAgainst().getFace(block);

            timeout.put(event.getPlayer(), System.currentTimeMillis());

            new BukkitRunnable() {
                int i = 1;

                @Override
                public void run() {
                    if (i > 6) {
                        cancel();
                    }

                    for (Team team : game.getTeams()) {
                        if (team.getSpawn().distance(block.getRelative(blockFace, i).getLocation()) <= 5) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (NewXBedwars.getInstance().getMapData().hasRegion(block.getRelative(blockFace, i).getLocation())) {
                        return;
                    }

                    for (Location location : game.getMapData().getDropLocations(DropType.DIAMOND)) {
                        if (location.distance(block.getRelative(blockFace, i).getLocation()) <= 3) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    for (Location location : game.getMapData().getDropLocations(DropType.EMERALD)) {
                        if (location.distance(block.getRelative(blockFace, i).getLocation()) <= 3) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (block.getRelative(blockFace, i).getType() == Material.AIR) {
                        block.getRelative(blockFace, i).setType(event.getItemInHand().getType());
                        block.getRelative(blockFace, i).setData(event.getItemInHand().getData().getData());
                        block.getWorld().playSound(block.getLocation(), SoundUtil.get("STEP_WOOL", "BLOCK_CLOTH_STEP"), 1f, 1f);
                    }

                    i++;
                }
            }.runTaskTimer(NewXBedwars.getInstance(), 0, 3L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (game.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
            return;
        }

        if (game.getGameState() == GameState.RUNNING) {
            Player player = event.getPlayer();
            Block block = event.getBlock();
            Team team = game.getTeam(player);

            if (game.getSpectatorManger().isSpectator(player)) {
                event.setCancelled(true);
                return;
            }

            if (block.getType().toString().startsWith("BED")) {
                event.setCancelled(true);

                if (team.getSpawn().distance(block.getLocation()) <= 18.0D) {
                    player.sendMessage("§c你不能破坏你家的床");
                    return;
                }

                for (Location location : game.getMapData().getBaseLocations()) {
                    if (location.distance(block.getLocation()) <= 18.0D) {
                        for (Team team1 : game.getTeams()) {
                            if (team1.getPlayers().size() > 0 && location.equals(team1.getSpawn())) {
                                Util.dropTargetBlock(block);

                                new BukkitRunnable() {
                                    int i = 0;

                                    @Override
                                    public void run() {
                                        if (i == 5) {
                                            cancel();
                                            return;
                                        }
                                        ActionBarUtil.sendBar(player, "§6+10个金币");
                                        i++;
                                    }
                                }.runTaskTimerAsynchronously(NewXBedwars.getInstance(), 0, 10);
                                player.sendMessage("§6+10个金币 (破坏床)");
                                NewXBedwars.getInstance().getEcon().depositPlayer(player, 10);

                                game.broadcastSound(SoundUtil.get("ENDERDRAGON_HIT", "ENTITY_ENDERDRAGON_HURT"), 10, 10);
                                game.broadcastMessage("§7▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
                                game.broadcastMessage(" ");
                                game.broadcastMessage("§c§l" + team1.getName() + " §a的床被 " + team.getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "§a 挖爆!");
                                game.broadcastMessage(" ");
                                game.broadcastMessage("§7▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");

                                game.broadcastTeamTitle(team1, 1, 20, 1, "§c§l床被摧毁", "§c死亡将无法复活");

                                Bukkit.getPluginManager().callEvent(new BedwarsDestroyBedEvent(player, team1));

                                team1.setDestroyPlayer(player);
                                team1.setBedDestroy(true);
                                PlayerData playerData = NewXBedwars.getInstance().getCacher().get(player.getName());
                                playerData.setDestroyedBeds(playerData.getDestroyedBeds() + 1);
                                return;
                            }
                        }
                        player.sendMessage("§c此床没有队伍");
                    }
                }
                return;
            }

            if (NewXBedwars.getInstance().getMapData().hasRegion(block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (game.getBlocks().contains(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (game.getGameState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        Entity entity = event.getEntity();
        for (int i = 0; i < event.blockList().size(); i++) {
            Block b = event.blockList().get(i);
            if (NewXBedwars.getInstance().getMapData().hasRegion(b.getLocation())) {
                event.setCancelled(true);
                continue;
            }

            if (b.getType() != Material.STAINED_GLASS && b.getType() != Material.BED_BLOCK) {
                if (!game.getBlocks().contains(b.getLocation())) {
                    event.setCancelled(true);
                    b.setType(Material.AIR);
                    b.getWorld().spigot().playEffect(b.getLocation(), Effect.EXPLOSION_HUGE);
                    b.getWorld().playSound(b.getLocation(), SoundUtil.get("EXPLODE", "ENTITY_GENERIC_EXPLODE"), 1.0F, 1.0F);
                }
            }
        }

        if (entity instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();
            if (!fireball.hasMetadata("Game FIREBALL")) {
                return;
            }
            String owner = fireball.getMetadata("Game FIREBALL").get(0).asString();

            for (Entity entity1 : entity.getNearbyEntities(4, 3, 4)) {
                if (!(entity1 instanceof Player)) {
                    continue;
                }

                Player player = (Player) entity1;
                if (fireball.hasMetadata("Game FIREBALL")) {
                    Team team = game.getTeam(owner);
                    if (team != null && team.isInTeam(owner, player.getName())) {
                        continue;
                    }
                }

                player.damage(3);
                game.setPlayerDamager(player, Bukkit.getPlayerExact(owner));
                player.setMetadata("FIREBALL PLAYER NOFALL", new FixedMetadataValue(NewXBedwars.getInstance(), owner));
                player.setVelocity(Util.getPosition(player.getLocation(), fireball.getLocation(), 1.5D).multiply(0.5));
            }
        }
        event.setCancelled(true);
    }
}
