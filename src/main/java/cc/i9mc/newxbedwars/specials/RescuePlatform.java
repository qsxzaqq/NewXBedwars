package cc.i9mc.newxbedwars.specials;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RescuePlatform extends SpecialItem {

    private Game game = null;
    private int livingTime = 0;
    private Player owner = null;
    private List<Block> platformBlocks = null;
    private BukkitTask task = null;

    public RescuePlatform() {
        super();

        this.platformBlocks = new ArrayList<Block>();
        this.game = null;
        this.owner = null;
    }

    public void addPlatformBlock(Block block) {
        this.platformBlocks.add(block);
    }

    @SuppressWarnings("deprecation")
    public void create(Player player, Game game) {
        this.game = game;
        this.owner = player;

        int breakTime = 12;
        int waitTime = 20;
        Material configMaterial = Material.SLIME_BLOCK;

        ArrayList<RescuePlatform> livingPlatforms = this.getLivingPlatforms();
        if (!livingPlatforms.isEmpty()) {
            for (RescuePlatform livingPlatform : livingPlatforms) {
                int waitLeft = waitTime - livingPlatform.getLivingTime();
                if (waitLeft > 0) {
                    player.sendMessage("§c需要 §e" + waitLeft + "秒§c 你才能使用下一个救援平台!");
                    return;
                }
            }
        }

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            player.sendMessage("§c你不在空气中!");
            return;
        }

        Location mid = player.getLocation().clone();
        mid.setY(mid.getY() - 1.0D);

        ItemStack usedStack = null;

        usedStack = player.getInventory().getItemInHand();
        usedStack.setAmount(usedStack.getAmount() - 1);
        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), usedStack);
        player.updateInventory();

        for (BlockFace face : BlockFace.values()) {
            if (face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP)) {
                continue;
            }

            Block placed = mid.getBlock().getRelative(face);
            if (placed.getType() != Material.AIR) {
                continue;
            }

            placed.setType(configMaterial);

            this.addPlatformBlock(placed);
        }

        Vector vector = player.getLocation().getDirection();
        vector.setY(vector.getY() + 0.7);
        player.getLocation().setDirection(vector);

        this.runTask(breakTime, waitTime);
        game.addSpecialItem(this);
    }

    @Override
    public Material getActivatedMaterial() {
        return null;
    }

    public Game getGame() {
        return this.game;
    }

    @Override
    public Material getItemMaterial() {
        return Material.BLAZE_ROD;
    }

    private ArrayList<RescuePlatform> getLivingPlatforms() {
        ArrayList<RescuePlatform> livingPlatforms = new ArrayList<>();
        for (SpecialItem item : game.getSpecialItems()) {
            if (item instanceof RescuePlatform) {
                RescuePlatform rescuePlatform = (RescuePlatform) item;
                if (rescuePlatform.getOwner().equals(this.getOwner())) {
                    livingPlatforms.add(rescuePlatform);
                }
            }
        }
        return livingPlatforms;
    }

    public int getLivingTime() {
        return this.livingTime;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void runTask(final int breakTime, final int waitTime) {
        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                RescuePlatform.this.livingTime++;

                if (breakTime > 0 && RescuePlatform.this.livingTime == breakTime) {
                    for (Block block : RescuePlatform.this.platformBlocks) {
                        block.setType(Material.AIR);
                    }
                }

                if (RescuePlatform.this.livingTime >= waitTime
                        && RescuePlatform.this.livingTime >= breakTime) {
                    RescuePlatform.this.game.removeSpecialItem(RescuePlatform.this);
                    RescuePlatform.this.task = null;
                    this.cancel();
                }
            }
        }.runTaskTimer(NewXBedwars.getInstance(), 20L, 20L);
    }

}
