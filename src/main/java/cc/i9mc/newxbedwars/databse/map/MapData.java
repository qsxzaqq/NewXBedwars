package cc.i9mc.newxbedwars.databse.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class MapData {
    private String name;
    private String URL;
    private String author;
    private int teamMaxPlayers;
    private int minPlayers;
    private int range;
    private Location reSpawnLocation;
    private Location area1Location;
    private Location area2Location;
    private List<Location> baseLocations;
    private HashMap<Location, DropType> dropLocations;
    private HashMap<Location, Integer> shopLocations;

    public MapData(String name) {
        this.name = name;
        this.baseLocations = new ArrayList<>();
        this.dropLocations = new HashMap<>();
        this.shopLocations = new HashMap<>();
    }

    public void addBaseLocation(Location location) {
        this.baseLocations.add(location);
    }

    public void addDropLocation(DropType dropType, Location location) {
        this.dropLocations.put(location, dropType);
    }

    public void addShopLocation(Integer type, Location location) {
        this.shopLocations.put(location, type);
    }

    public List<Location> getDropLocations(DropType dropType) {
        return dropLocations.entrySet().stream().filter((e) -> e.getValue() == dropType).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<Location> getShopLocations(int type) {
        return shopLocations.entrySet().stream().filter((e) -> e.getValue() == type).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<Location> loadMap() {
        List<Location> blocks = new ArrayList<>();
        for (int x = Math.min(area1Location.getBlockX(), area2Location.getBlockX()); x <= Math.max(area1Location.getBlockX(), area2Location.getBlockX()); x++) {
            for (int y = Math.min(area1Location.getBlockY(), area2Location.getBlockY()); y <= Math.max(area1Location.getBlockY(), area2Location.getBlockY()); y++) {
                for (int z = Math.min(area1Location.getBlockZ(), area2Location.getBlockZ()); z <= Math.max(area1Location.getBlockZ(), area2Location.getBlockZ()); z++) {
                    Block block = new Location(area1Location.getWorld(), x, y, z).getBlock();

                    if (block != null) {
                        if (block.getType() == Material.AIR || block.getType() == Material.BED_BLOCK || block.getType() == Material.LONG_GRASS || block.getType() == Material.DEAD_BUSH) {
                            continue;
                        }
                        System.out.println(x + ", " + y + ", " + z);
                        blocks.add(block.getLocation());
                    }
                }
            }
        }

        return blocks;
    }

    public boolean hasRegion(Location location) {
        int x1 = area1Location.getBlockX();
        int x2 = area2Location.getBlockX();
        int y1 = area1Location.getBlockY();
        int y2 = area2Location.getBlockY();
        int z1 = area1Location.getBlockZ();
        int z2 = area2Location.getBlockZ();
        int minY = Math.min(y1, y2) - 1;
        int maxY = Math.max(y1, y2) + 1;
        int minZ = Math.min(z1, z2) - 1;
        int maxZ = Math.max(z1, z2) + 1;
        int minX = Math.min(x1, x2) - 1;
        int maxX = Math.max(x1, x2) + 1;
        if (location.getX() > minX && location.getX() < maxX) {
            if (location.getY() > minY && location.getY() < maxY) {
                return !(location.getZ() > minZ) || !(location.getZ() < maxZ);
            }
        }
        return true;
    }

    public boolean chunkIsInRegion(double x, double z) {
        int x1 = area1Location.getBlockX();
        int x2 = area2Location.getBlockX();
        int z1 = area1Location.getBlockZ();
        int z2 = area2Location.getBlockZ();
        int minZ = Math.min(z1, z2) - 1;
        int maxZ = Math.max(z1, z2) + 1;
        int minX = Math.min(x1, x2) - 1;
        int maxX = Math.max(x1, x2) + 1;

        return (x >= minX && x <= maxX && z >= minZ && z <= maxZ);
    }
}
