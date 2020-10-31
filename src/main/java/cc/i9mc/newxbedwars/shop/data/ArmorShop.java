package cc.i9mc.newxbedwars.shop.data;

import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.shop.ShopData;
import cc.i9mc.newxbedwars.shop.type.ColorType;
import cc.i9mc.newxbedwars.shop.type.ItemType;
import cc.i9mc.newxbedwars.shop.type.PriceCost;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.LinkedList;
import java.util.List;

public class ArmorShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public ArmorShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.CHAINMAIL_BOOTS).setLores("§e点击查看！").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "§a盔甲", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.CHAINMAIL_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "锁链装备§7（死亡不掉落）", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 40, 40)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.IRON_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "铁装备§7（死亡不掉落）", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 12, 60)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.DIAMOND_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "钻石装备§7（死亡不掉落）", ColorType.NONE, new PriceCost(Material.EMERALD, 6, 600)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
