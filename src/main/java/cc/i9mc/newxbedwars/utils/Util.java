package cc.i9mc.newxbedwars.utils;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.map.DropType;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.game.TeamColor;
import cc.i9mc.nick.Nick;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static List<Location> locationConversion(List<String> strings) {
        List<Location> locations = new ArrayList<>();
        for (String loc : strings) {
            locations.add(locationConversion(loc));
        }

        if (locations.isEmpty()) {
            return null;
        }

        return locations;
    }

    public static HashMap<Location, DropType> dropConversion(HashMap<String, String> strings) {
        HashMap<Location, DropType> drops = new HashMap<>();

        for (Map.Entry<String, String> e : strings.entrySet()) {
            drops.put(locationConversion(e.getKey()), DropType.valueOf(e.getValue()));
        }

        if (drops.isEmpty()) {
            return null;
        }

        return drops;
    }

    public static HashMap<Location, Integer> shopConversion(HashMap<String, Integer> strings) {
        HashMap<Location, Integer> shops = new HashMap<>();

        for (Map.Entry<String, Integer> e : strings.entrySet()) {
            shops.put(locationConversion(e.getKey()), e.getValue());
        }

        if (shops.isEmpty()) {
            return null;
        }

        return shops;
    }

    public static Location locationConversion(String string) {
        return new Location(Bukkit.getWorld(string.split(",")[0]), Double.parseDouble(string.split(",")[1]), Double.parseDouble(string.split(",")[2]), Double.parseDouble(string.split(",")[3]), Float.parseFloat(string.split(",")[4]), Float.parseFloat(string.split(",")[5]));
    }

    public static Location locationConversion(String world, String string) {
        return new Location(Bukkit.getWorld(world), Double.parseDouble(string.split(",")[0]), Double.parseDouble(string.split(",")[1]), Double.parseDouble(string.split(",")[2]), Float.parseFloat(string.split(",")[3]), Float.parseFloat(string.split(",")[4]));
    }

    public static void spawnALL(NewXBedwars main) {
        Game game = main.getGame();

        for (Location loc : game.getMapData().getShopLocations(1)) {
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }

            spawnVillager(main, loc);
        }

        for (Location loc : game.getMapData().getShopLocations(2)) {
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }

            spawnVillager2(main, loc);
        }

        for (Location loc : game.getMapData().getDropLocations(DropType.DIAMOND)) {
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }

            spawnArmorStand(game, loc, DropType.DIAMOND, "qwq");
        }

        for (Location loc : game.getMapData().getDropLocations(DropType.EMERALD)) {
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }

            spawnArmorStand(game, loc, DropType.EMERALD, "qaq");
        }
    }

    private static void spawnVillager(NewXBedwars main, Location l) {
        Location loc = l.add(0.0D, -1.5D, 0.0D);
        ArmorStand ab = l.getWorld().spawn(loc, ArmorStand.class);
        Villager v = l.getWorld().spawn(l, Villager.class);
        v.setCustomNameVisible(false);
        if (v.getType() == EntityType.VILLAGER) {
            v.setProfession(Villager.Profession.LIBRARIAN);
        }

        v.setMetadata("Shop", new FixedMetadataValue(main, "Shop"));
        ab.setGravity(false);
        ab.setVisible(false);
        ab.setPassenger(v);
        spawnArmorStand2(l.add(0.0D, 1.3D, 0.0D), "§b§l物品商人");
    }

    private static void spawnVillager2(NewXBedwars main, Location l) {
        Location loc = l.add(0.0D, -1.5D, 0.0D);
        ArmorStand ab = l.getWorld().spawn(loc, ArmorStand.class);
        Villager v = l.getWorld().spawn(l, Villager.class);
        v.setCustomNameVisible(false);
        if (v.getType() == EntityType.VILLAGER) {
            v.setProfession(Villager.Profession.BLACKSMITH);
        }

        v.setMetadata("Shop2", new FixedMetadataValue(main, "Shop2"));
        spawnArmorStand2(l.add(0.0D, 1.3D, 0.0D), "§e§l团队升级");
        ab.setGravity(false);
        ab.setVisible(false);
        ab.setPassenger(v);
    }

    private static void spawnArmorStand2(Location l, String name) {
        ArmorStand a2 = l.getWorld().spawn(l.add(0.0D, 0.25D, 0.0D), ArmorStand.class);
        a2.setCustomName(name);
        a2.setCustomNameVisible(true);
        a2.setGravity(false);
        a2.setVisible(false);
        a2.setFallDistance(5.0F);
    }

    private static void spawnArmorStand(Game game, Location l, DropType type, String name) {
        ArmorStand as;
        l.add(0.0D, 1.0D, 0.0D);

        if (type == DropType.EMERALD) {
            as = l.getWorld().spawn(l, ArmorStand.class);
            as.setGravity(false);
            as.setVisible(false);
            as.setFallDistance(7.0F);
            as.setHelmet(new ItemStack(Material.EMERALD_BLOCK));
            game.getArmorSande().put(as, name);

            as = l.getWorld().spawn(l, ArmorStand.class);
            as.setCustomName(name);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setVisible(false);
            as.setFallDistance(6.0F);
            game.getArmorSande().put(as, name);

            as = l.getWorld().spawn(l.add(0.0D, 0.25D, 0.0D), ArmorStand.class);
            as.setCustomName(name);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setVisible(false);
            as.setFallDistance(5.0F);
            game.getArmorSande().put(as, name);

            as = l.getWorld().spawn(l.add(0.0D, 0.28D, 0.0D), ArmorStand.class);
            as.setCustomName(name);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setFallDistance(4.0F);
            as.setVisible(false);
            game.getArmorSande().put(as, name);
        }

        if (type == DropType.DIAMOND) {
            as = l.getWorld().spawn(l, ArmorStand.class);
            as.setGravity(false);
            as.setVisible(false);
            as.setFallDistance(7.0F);
            as.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
            game.getArmorStand().put(as, name);

            as = l.getWorld().spawn(l, ArmorStand.class);
            as.setCustomName(name);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setVisible(false);
            as.setFallDistance(6.0F);
            game.getArmorStand().put(as, name);

            as = l.getWorld().spawn(l.add(0.0D, 0.25D, 0.0D), ArmorStand.class);
            as.setCustomName(name);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setVisible(false);
            as.setFallDistance(5.0F);
            game.getArmorStand().put(as, name);

            as = l.getWorld().spawn(l.add(0.0D, 0.28D, 0.0D), ArmorStand.class);
            as.setCustomName(name);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setFallDistance(4.0F);
            as.setVisible(false);
            game.getArmorStand().put(as, name);
        }

    }

    public static void dropTargetBlock(Block targetBlock) {
        if (targetBlock.getType().equals(Material.BED_BLOCK)) {
            Block bedHead;
            Block bedFeet;
            Bed bedBlock = (Bed) targetBlock.getState().getData();

            if (!bedBlock.isHeadOfBed()) {
                bedFeet = targetBlock;
                bedHead = getBedNeighbor(bedFeet);
            } else {
                bedHead = targetBlock;
                bedFeet = getBedNeighbor(bedHead);
            }

            bedHead.setType(Material.AIR);
        } else {
            targetBlock.setType(Material.AIR);
        }
    }

    private static Block getBedNeighbor(Block head) {
        if (isBedBlock(head.getRelative(BlockFace.EAST))) {
            return head.getRelative(BlockFace.EAST);
        } else if (isBedBlock(head.getRelative(BlockFace.WEST))) {
            return head.getRelative(BlockFace.WEST);
        } else if (isBedBlock(head.getRelative(BlockFace.SOUTH))) {
            return head.getRelative(BlockFace.SOUTH);
        } else {
            return head.getRelative(BlockFace.NORTH);
        }
    }

    private static boolean isBedBlock(Block isBed) {
        if (isBed == null) {
            return false;
        }

        return (isBed.getType() == Material.BED || isBed.getType() == Material.BED_BLOCK);
    }

    public static void setFlying(Player player) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ABILITIES);
        packet.getModifier().writeDefaults();
        packet.getFloat().write(0, 0.05F);
        packet.getBooleans().write(1, true);
        packet.getBooleans().write(2, true);
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public static void spawnParticle(List<Player> players, Location loc) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getModifier().writeDefaults();
        packet.getParticles().write(0, EnumWrappers.Particle.FIREWORKS_SPARK);
        packet.getBooleans().write(0, false);
        packet.getFloat().write(0, (float) loc.getX());
        packet.getFloat().write(1, (float) loc.getY());
        packet.getFloat().write(2, (float) loc.getZ());
        packet.getFloat().write(3, 0.0F);
        packet.getFloat().write(4, 0.0F);
        packet.getFloat().write(5, 0.0F);
        packet.getFloat().write(6, 0.0F);
        packet.getIntegers().write(0, 1);
        players.forEach(player -> {
            try {
                protocolManager.sendServerPacket(player, packet);
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        });
    }

    public static Vector getPosition(Location location1, Location location2, double Y) {
        double X = location1.getX() - location2.getX();
        double Z = location1.getZ() - location2.getZ();
        return new Vector(X, Y, Z);
    }

    public static void setPlayerTeamTab() {
        Game game = NewXBedwars.getInstance().getGame();

        for (TeamColor teamColor : TeamColor.values()) {
            Team team = game.getTeam(teamColor);
            if (team == null) {
                continue;
            }

            team.getPlayers().forEach(player -> {
                NametagEdit.getApi().clearNametag(player);
                NametagEdit.getApi().setNametag(player, team.getName() + " ", Nick.get().getCache().getOrDefault(player.getName(), ""));
            });
        }
    }
}
