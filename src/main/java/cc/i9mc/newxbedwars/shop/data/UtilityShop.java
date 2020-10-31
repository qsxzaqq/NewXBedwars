package cc.i9mc.newxbedwars.shop.data;

import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.shop.ShopData;
import cc.i9mc.newxbedwars.shop.type.ColorType;
import cc.i9mc.newxbedwars.shop.type.ItemType;
import cc.i9mc.newxbedwars.shop.type.PriceCost;
import org.bukkit.Material;

import java.util.LinkedList;
import java.util.List;

public class UtilityShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public UtilityShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.TNT).setLores("§e点击查看！").getItem(), "§a实用道具", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.FIREBALL).getItem(), "火球", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 50, 50)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.TNT).getItem(), "TNT", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 4, 40)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.ENDER_PEARL).getItem(), "末影珍珠", ColorType.NONE, new PriceCost(Material.EMERALD, 4, 400)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WATER_BUCKET).getItem(), "水桶", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 100)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.COMPASS).setDisplayName("§e指南针").getItem(), "指南针", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 100)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.BLAZE_ROD).setDisplayName("§e援救平台").setLores(" ", "§f在空中使用该平台", "§f将在脚下生成一块临时史莱姆方块平台救生!").getItem(), "救援平台", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 80)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.SULPHUR).setDisplayName("§e回城卷轴").setLores(" ", "§f将在6秒后传送到出生点.", "§f注意:移动将会取消传送!").getItem(), "回城卷轴", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 10, 50)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.BED).setDisplayName("§e回春床").setLores(" ", "§f开局10分钟内可恢复一次床").getItem(), "回春床", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 80)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
