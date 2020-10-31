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

public class SwordShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public SwordShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.GOLD_SWORD).setLores("§e点击查看！").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "§a近战武器", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.STONE_SWORD).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "石剑", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 10, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.IRON_SWORD).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "铁剑", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 7, 35)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.DIAMOND_SWORD).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "钻石剑", ColorType.NONE, new PriceCost(Material.EMERALD, 4, 400)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.STICK).addEnchant(Enchantment.KNOCKBACK, 1).getItem(), "击退棒", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 10, 150)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
