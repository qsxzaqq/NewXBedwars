package cc.i9mc.newxbedwars.listeners;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.GameState;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.guis.ItemShopGUI;
import cc.i9mc.newxbedwars.guis.ModeSelectionGUI;
import cc.i9mc.newxbedwars.guis.SpectatorCompassGUI;
import cc.i9mc.newxbedwars.guis.TeamShopGUI;
import cc.i9mc.newxbedwars.types.ModeType;
import cc.i9mc.newxbedwars.utils.SoundUtil;
import cc.i9mc.nick.Nick;
import cc.i9mc.watchnmslreport.BukkitReport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    private final Game game = NewXBedwars.getInstance().getGame();

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (game.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
            return;
        }

        if (game.getSpectatorManger().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void craftItem(PrepareItemCraftEvent event) {
        for (HumanEntity h : event.getViewers()) {
            if (h instanceof Player) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material interactingMaterial = event.getMaterial();

        if (interactingMaterial == null) {
            event.setCancelled(true);
            return;
        }

        if (game.getGameState() == GameState.WAITING) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                event.setCancelled(true);
                switch (interactingMaterial) {
                    case PAPER:
                        new ModeSelectionGUI(player).open();
                        return;
                    case SLIME_BALL:
                        NewXBedwars.sendToLobby(player);
                        return;
                    default:
                        return;
                }
            }
        }

        if (game.getGameState() == GameState.RUNNING) {
            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (game.getSpectatorManger().isSpectator(player) && event.getClickedBlock() != null) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getClickedBlock() != null && event.getClickedBlock().getType().toString().startsWith("BED")) {
                    if (player.isSneaking() && player.getItemInHand() != null && player.getItemInHand().getType().isBlock()) {
                        return;
                    }

                    player.sendMessage("§4睡你妈逼起来嗨!");
                    event.setCancelled(true);
                    return;
                }
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                switch (interactingMaterial) {
                    case COMPASS:
                        event.setCancelled(true);
                        if (!game.getSpectatorManger().isSpectator(player)) {
                            Player target = game.findTargetPlayer(player);
                            Team team = game.getTeam(target);

                            if (team == null) {
                                player.sendMessage("§c没有目标");
                                return;
                            }

                            player.sendMessage("§e玩家 " + team.getChatColor() + target.getName() + " §e距离您 " + ((int) target.getLocation().distance(player.getLocation())) + "m");
                            return;
                        }

                        new SpectatorCompassGUI(player, game).open();
                        return;
                    case PAPER:
                        event.setCancelled(true);
                        Bukkit.dispatchCommand(player, "queue join qc x");
                        return;
                    case SLIME_BALL:
                        event.setCancelled(true);
                        NewXBedwars.sendToLobby(player);
                        return;
                    case BED:
                        event.setCancelled(true);
                        if (game.getSpectatorManger().isSpectator(player)) {
                            return;
                        }

                        if (game.getEventManager().getSeconds() > 600) {
                            player.sendMessage("§c开局已超过10分钟.");
                            return;
                        }

                        Team team = game.getTeam(player);

                        if (team.isUnbed()) {
                            player.sendMessage("§c已使用过回春床了.");
                            return;
                        }

                        if (!team.isBedDestroy()) {
                            player.sendMessage("§c床还在,回啥春呢?");
                            return;
                        }

                        if (player.getLocation().distance(team.getSpawn()) > 18) {
                            player.sendMessage("§c请靠近出生点使用!");
                            return;
                        }

                        BlockFace face = team.getBedFace();

                        if (face == BlockFace.NORTH) {
                            Location l = team.getBedHead().getLocation();
                            l.getBlock().setType(Material.AIR);
                            l.getBlock().setType(Material.BED_BLOCK);
                            Block block = team.getBedHead();
                            BlockState bedFoot = block.getState();
                            BlockState bedHead = bedFoot.getBlock().getRelative(BlockFace.SOUTH).getState();
                            bedFoot.setType(Material.BED_BLOCK);
                            bedHead.setType(Material.BED_BLOCK);
                            bedFoot.setRawData((byte) 0);
                            bedHead.setRawData((byte) 8);
                            bedFoot.update(true, false);
                            bedHead.update(true, true);
                        } else if (face == BlockFace.EAST) {
                            Location l = team.getBedHead().getLocation();
                            l.getBlock().setType(Material.AIR);
                            l.getBlock().setType(Material.BED_BLOCK);
                            Block block = team.getBedHead();
                            BlockState bedFoot = block.getState();
                            BlockState bedHead = bedFoot.getBlock().getRelative(BlockFace.WEST).getState();
                            bedFoot.setType(Material.BED_BLOCK);
                            bedHead.setType(Material.BED_BLOCK);
                            bedFoot.setRawData((byte) 1);
                            bedHead.setRawData((byte) 9);
                            bedFoot.update(true, false);
                            bedHead.update(true, true);
                        } else if (face == BlockFace.SOUTH) {
                            Location l = team.getBedHead().getLocation();
                            l.getBlock().setType(Material.AIR);
                            l.getBlock().setType(Material.BED_BLOCK);
                            Block block = team.getBedHead();
                            BlockState bedFoot = block.getState();
                            BlockState bedHead = bedFoot.getBlock().getRelative(BlockFace.NORTH).getState();
                            bedFoot.setType(Material.BED_BLOCK);
                            bedHead.setType(Material.BED_BLOCK);
                            bedFoot.setRawData((byte) 2);
                            bedHead.setRawData((byte) 10);
                            bedFoot.update(true, false);
                            bedHead.update(true, true);
                        } else if (face == BlockFace.WEST) {
                            Location l = team.getBedHead().getLocation();
                            l.getBlock().setType(Material.AIR);
                            l.getBlock().setType(Material.BED_BLOCK);
                            Block block = team.getBedHead();
                            BlockState bedFoot = block.getState();
                            BlockState bedHead = bedFoot.getBlock().getRelative(BlockFace.EAST).getState();
                            bedFoot.setType(Material.BED_BLOCK);
                            bedHead.setType(Material.BED_BLOCK);
                            bedFoot.setRawData((byte) 3);
                            bedHead.setRawData((byte) 11);
                            bedFoot.update(true, false);
                            bedHead.update(true, true);
                        }

                        if (player.getItemInHand().getAmount() == 1) {
                            player.getInventory().setItemInHand(null);
                        } else {
                            player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                        }

                        team.setBedDestroy(false);
                        team.setUnbed(true);

                        player.sendMessage("§a使用回春床成功!");
                        return;
                    case FIREBALL:
                        event.setCancelled(true);
                        if (game.getSpectatorManger().isSpectator(player)) {
                            return;
                        }

                        if (Math.abs(System.currentTimeMillis() - (player.hasMetadata("Game FIREBALL TIMER") ? player.getMetadata("Game FIREBALL TIMER").get(0).asLong() : 0L)) < 1000) {
                            return;
                        }

                        if (player.getItemInHand().getAmount() == 1) {
                            player.getInventory().setItemInHand(null);
                        } else {
                            player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                        }

                        player.setMetadata("Game FIREBALL TIMER", new FixedMetadataValue(NewXBedwars.getInstance(), System.currentTimeMillis()));

                        Fireball fireball = player.launchProjectile(Fireball.class);
                        fireball.setVelocity(fireball.getVelocity().multiply(2));
                        fireball.setYield(3.0F);
                        fireball.setBounce(false);
                        fireball.setIsIncendiary(false);
                        fireball.setMetadata("Game FIREBALL", new FixedMetadataValue(NewXBedwars.getInstance(), player.getName()));
                        return;
                    case WATER_BUCKET:
                        for (Location location : game.getMapData().getShopLocations().keySet()) {
                            if (location.distance(player.getLocation()) <= 5) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        for (Location location : game.getMapData().getBaseLocations()) {
                            if (location.distance(player.getLocation()) <= 8) {
                                event.setCancelled(true);
                                return;
                            }
                        }
                        return;
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (game.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
            return;
        }

        if (game.getGameState() == GameState.RUNNING && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameDrop(PlayerDropItemEvent event) {
        if (game.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
            return;
        }

        if (game.getGameState() == GameState.RUNNING) {
            Player player = event.getPlayer();
            ItemStack itemStack = event.getItemDrop().getItemStack();

            if (game.getSpectatorManger().isSpectator(player)) {
                event.setCancelled(true);
                return;
            }

            if (itemStack.getType().toString().endsWith("_HELMET") || itemStack.getType().toString().endsWith("_CHESTPLATE") || itemStack.getType().toString().endsWith("_LEGGINGS") || itemStack.getType().toString().endsWith("_BOOTS")) {
                event.setCancelled(true);
                return;
            }

            if (itemStack.getType().toString().endsWith("_AXE") || itemStack.getType().toString().endsWith("PICKAXE") || itemStack.getType() == Material.SHEARS) {
                event.setCancelled(true);
                return;
            }

            if (itemStack.getType().toString().endsWith("_SWORD")) {
                if (itemStack.getType() == Material.WOOD_SWORD) {
                    event.getItemDrop().remove();
                }

                itemStack.removeEnchantment(Enchantment.DAMAGE_ALL);
                int size = 0;
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    ItemStack itemStack1 = player.getInventory().getItem(i);
                    if (itemStack1 != null && itemStack1.getType().toString().endsWith("_SWORD")) {
                        size++;
                    }
                }

                if (size == 0) {
                    Bukkit.getScheduler().runTaskLater(NewXBedwars.getInstance(), () -> game.giveInventory(player, 5, false), 8);
                }
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem().getItemStack();
        PlayerData playerData = NewXBedwars.getInstance().getCacher().get(player.getName());

        if (game.getSpectatorManger().isSpectator(player)) {
            event.setCancelled(true);
            return;
        }

        if (itemStack.getType() == Material.BED || itemStack.getType() == Material.BED_BLOCK) {
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().getDisplayName() != null) {
                return;
            }

            event.setCancelled(true);
            event.getItem().remove();
        }

        if (itemStack.getType() == Material.WOOD_SWORD || itemStack.getType() == Material.STONE_SWORD || itemStack.getType() == Material.IRON_SWORD || itemStack.getType() == Material.DIAMOND_SWORD) {
            if (game.getTeam(player).isSharpenedSwords()) {
                itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            }

            for (int i = 0; i < player.getInventory().getSize(); i++) {
                if (player.getInventory().getItem(i) != null) {
                    if (player.getInventory().getItem(i).getType() == Material.WOOD_SWORD) {
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                        break;
                    }
                }
            }
        }

        if (itemStack.getType() == Material.IRON_INGOT || itemStack.getType() == Material.GOLD_INGOT) {
            double xp = itemStack.getAmount();

            if (itemStack.getType() == Material.GOLD_INGOT) {
                xp = xp * 3;
            }

            if (playerData.getModeType() == ModeType.DEFAULT) {
                event.setCancelled(true);
                event.getItem().remove();

                player.playSound(player.getLocation(), SoundUtil.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 10, 15);
                player.getInventory().addItem(new ItemStack(itemStack.getType(), itemStack.getAmount()));
            } else if (playerData.getModeType() == ModeType.EXPERIENCE) {
                event.setCancelled(true);
                event.getItem().remove();

                player.playSound(player.getLocation(), SoundUtil.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 10, 15);
                player.setLevel((int) (player.getLevel() + xp));
            }

            if (itemStack.hasItemMeta() && itemStack.getItemMeta().getDisplayName() != null) {
                for (Entity entity : player.getNearbyEntities(2, 2, 2)) {
                    if (entity instanceof Player) {
                        Player players = (Player) entity;
                        players.playSound(players.getLocation(), SoundUtil.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 10, 15);

                        if (NewXBedwars.getInstance().getCacher().get(players.getName()).getModeType() == ModeType.DEFAULT) {
                            players.getInventory().addItem(new ItemStack(itemStack.getType(), itemStack.getAmount()));
                        } else {
                            players.setLevel((int) (players.getLevel() + xp));
                        }
                    }
                }
            }
        }

        if (itemStack.getType() == Material.DIAMOND) {
            if (playerData.getModeType() == ModeType.DEFAULT) {
                return;
            }

            double xp = itemStack.getAmount() * 40;
            event.setCancelled(true);

            if (player.hasPermission("bw.xp.vip1")) {
                xp = xp + (xp * 1.1);
            } else if (player.hasPermission("bw.xp.vip2")) {
                xp = xp + (xp * 1.2);
            } else if (player.hasPermission("bw.xp.vip3")) {
                xp = xp + (xp * 1.4);
            } else if (player.hasPermission("bw.xp.vip4")) {
                xp = xp + (xp * 1.8);
            }

            event.getItem().remove();
            player.setLevel((int) (player.getLevel() + xp));
            player.playSound(player.getLocation(), SoundUtil.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 10, 15);
        }

        if (itemStack.getType() == Material.EMERALD) {
            if (playerData.getModeType() == ModeType.DEFAULT) {
                return;
            }

            double xp = itemStack.getAmount() * 80;
            event.setCancelled(true);

            if (player.hasPermission("bw.xp.vip1")) {
                xp = xp + (xp * 1.1);
            } else if (player.hasPermission("bw.xp.vip2")) {
                xp = xp + (xp * 1.2);
            } else if (player.hasPermission("bw.xp.vip3")) {
                xp = xp + (xp * 1.4);
            } else if (player.hasPermission("bw.xp.vip4")) {
                xp = xp + (xp * 1.8);
            }

            event.getItem().remove();
            player.setLevel((int) (player.getLevel() + xp));
            player.playSound(player.getLocation(), SoundUtil.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 10, 15);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.startsWith("/report")) {
            return;
        }

        if (message.startsWith("/queue join qc x")) {
            return;
        }

        if (BukkitReport.getInstance().getStaffs().containsKey(player.getName())) {
            if (event.getMessage().startsWith("/wnm") || event.getMessage().startsWith("/staff")) {
                return;
            }
        }

        if (!player.hasPermission("bw.*")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked().hasMetadata("Shop")) {
            event.setCancelled(true);
            if (game.getSpectatorManger().isSpectator(player)) {
                return;
            }
            new ItemShopGUI(player, 0, game).open();
            return;
        }

        if (event.getRightClicked().hasMetadata("Shop2")) {
            event.setCancelled(true);
            if (game.getSpectatorManger().isSpectator(player)) {
                return;
            }

            new TeamShopGUI(player, game).open();
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (event.getItem().getType() != Material.POTION) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getInventory().getItemInHand().getType() == Material.GLASS_BOTTLE) {
                    player.getInventory().setItemInHand(new ItemStack(Material.AIR));
                }
            }
        }.runTaskLater(NewXBedwars.getInstance(), 0);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        if (player.hasMetadata("等待上一次求救")) {
            return;
        }

        if (player.getLocation().getPitch() > -80) {
            return;
        }

        player.setMetadata("等待上一次求救", new FixedMetadataValue(NewXBedwars.getInstance(), ""));

        Team team = game.getTeam(player);
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i > 5) {
                    player.removeMetadata("等待上一次求救", NewXBedwars.getInstance());
                    cancel();
                    return;
                }

                game.broadcastTeamTitle(team, 0, 8, 0, "", team.getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + " 说: §c注意,我们的床有危险！");
                game.broadcastTeamSound(team, SoundUtil.get("CLICK", "UI_BUTTON_CLICK"), 1f, 1f);
                i++;
            }
        }.runTaskTimer(NewXBedwars.getInstance(), 0, 10L);
    }
}
