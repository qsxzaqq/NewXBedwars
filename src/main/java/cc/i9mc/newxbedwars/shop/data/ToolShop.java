package cc.i9mc.newxbedwars.shop.data;

import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.shop.ShopData;
import cc.i9mc.newxbedwars.shop.type.ColorType;
import cc.i9mc.newxbedwars.shop.type.ItemType;
import cc.i9mc.newxbedwars.shop.type.PriceCost;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.LinkedList;
import java.util.List;

public class ToolShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public ToolShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.STONE_PICKAXE).setLores("§e点击查看！").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "§a工具", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WOOD_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem(), "稿子", ColorType.PICKAXE, new PriceCost(Material.IRON_INGOT, 10, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WOOD_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem(), "斧子", ColorType.AXE, new PriceCost(Material.IRON_INGOT, 10, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.SHEARS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "剪刀", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 30, 30)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
