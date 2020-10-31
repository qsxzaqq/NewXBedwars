package cc.i9mc.newxbedwars.specials;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.utils.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WarpPowder extends SpecialItem {

    private final int fullTeleportingTime = 6;
    private Game game = NewXBedwars.getInstance().getGame();
    private Player player = null;
    private ItemStack stack = null;
    private BukkitTask teleportingTask = null;
    private double teleportingTime = 6.0;

    public WarpPowder() {
        super();
    }

    public void cancelTeleport(boolean removeSpecial, boolean showMessage) {
        this.teleportingTask.cancel();

        this.teleportingTime = 6;
        this.player.setLevel(0);

        if (removeSpecial) {
            this.game.removeSpecialItem(this);
        }

        if (showMessage) {
            this.player.sendMessage("§c你的传送被取消!");
        }

        this.setStackAmount(this.getStack().getAmount() - 1);
        this.player.getInventory().setItem(player.getInventory().first(this.getCancelItemStack()), this.stack);
        this.player.updateInventory();
    }

    @Override
    public Material getActivatedMaterial() {
        return Material.GLOWSTONE_DUST;
    }

    private ItemStack getCancelItemStack() {
        ItemStack glowstone = new ItemStack(this.getActivatedMaterial(), 1);
        ItemMeta meta = glowstone.getItemMeta();
        meta.setDisplayName("§4取消传送");
        glowstone.setItemMeta(meta);

        return glowstone;
    }

    @Override
    public Material getItemMaterial() {
        return Material.SULPHUR;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    @SuppressWarnings("deprecation")
    public void runTask() {
        final int circles = 15;
        final double height = 2.0;

        this.stack = player.getInventory().getItemInHand();
        this.player.getInventory().setItem(player.getInventory().getHeldItemSlot(), this.getCancelItemStack());
        this.player.updateInventory();

        this.teleportingTime = 6;
        this.player.sendMessage("§a在 §c" + this.fullTeleportingTime + "§a 秒后你将被传送，请不要移动!");

        this.teleportingTask = new BukkitRunnable() {

            public final String particle = "fireworksSpark";
            public double through = 0.0;

            @Override
            public void run() {
                try {
                    int circleElements = 20;
                    double radius = 1.0;
                    double height2 = 1.0;
                    double circles = 15.0;
                    double fulltime = WarpPowder.this.fullTeleportingTime;
                    double teleportingTime = WarpPowder.this.teleportingTime;

                    double perThrough = (Math.ceil((height / circles) * ((fulltime * 20) / circles)) / 20);

                    WarpPowder.this.teleportingTime = teleportingTime - perThrough;
                    Team team = WarpPowder.this.game.getTeam(WarpPowder.this.player);
                    Location tLoc = team.getSpawn();

                    if (WarpPowder.this.teleportingTime <= 1.0) {
                        WarpPowder.this.player.teleport(team.getSpawn());
                        WarpPowder.this.cancelTeleport(true, false);
                        return;
                    }

                    WarpPowder.this.player.setLevel((int) WarpPowder.this.teleportingTime);

                    Location loc = WarpPowder.this.player.getLocation();

                    double y = (height2 / circles) * through;
                    for (int i = 0; i < 20; i++) {
                        double alpha = (360.0 / circleElements) * i;
                        double x = radius * Math.sin(Math.toRadians(alpha));
                        double z = radius * Math.cos(Math.toRadians(alpha));

                        Location particleFrom = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
                        Util.spawnParticle(game.getPlayers(), particleFrom);

                        Location particleTo = new Location(tLoc.getWorld(), tLoc.getX() + x, tLoc.getY() + y, tLoc.getZ() + z);
                        Util.spawnParticle(game.getPlayers(), particleTo);
                    }

                    this.through += 1.0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    this.cancel();
                    WarpPowder.this.cancelTeleport(true, false);
                }
            }
        }.runTaskTimer(NewXBedwars.getInstance(), 0L,
                (long) Math.ceil((height / circles) * ((this.fullTeleportingTime * 20) / circles)));
        this.game.addSpecialItem(this);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setStackAmount(int amount) {
        this.stack.setAmount(amount);
    }
}
