package cc.i9mc.newxbedwars.shop.data;

import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.shop.ShopData;
import cc.i9mc.newxbedwars.shop.type.ColorType;
import cc.i9mc.newxbedwars.shop.type.ItemType;
import cc.i9mc.newxbedwars.shop.type.PriceCost;
import org.bukkit.Material;

import java.util.LinkedList;
import java.util.List;

public class FoodShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public FoodShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.GRILLED_PORK).setLores("§e点击查看！").getItem(), "§a食物", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.GOLDEN_APPLE).getItem(), "金苹果", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 3, 15)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.COOKED_BEEF).setAmount(2).getItem(), "牛排", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 10, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.CARROT_ITEM).setAmount(2).getItem(), "胡萝卜", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 1, 4)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
