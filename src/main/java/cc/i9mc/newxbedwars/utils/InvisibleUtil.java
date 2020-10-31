package cc.i9mc.newxbedwars.utils;

import cc.i9mc.newxbedwars.game.Game;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InvisibleUtil {
    public void hideEquip(Game game, Player player, boolean hide) {
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment1 = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : player.getEquipment().getHelmet()));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment2 = new PacketPlayOutEntityEquipment(player.getEntityId(), 3, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : player.getEquipment().getChestplate()));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment3 = new PacketPlayOutEntityEquipment(player.getEntityId(), 2, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : player.getEquipment().getLeggings()));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment4 = new PacketPlayOutEntityEquipment(player.getEntityId(), 1, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : player.getEquipment().getBoots()));
        for (Player player1 : game.getPlayers()) {
            if (player1.equals(player)) {
                continue;
            }
            CraftPlayer craftPlayer = (CraftPlayer) player1;

            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment1);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment2);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment3);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment4);
        }
    }
}
