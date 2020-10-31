package cc.i9mc.newxbedwars.shop.data;

import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.shop.ShopData;
import cc.i9mc.newxbedwars.shop.type.ColorType;
import cc.i9mc.newxbedwars.shop.type.ItemType;
import cc.i9mc.newxbedwars.shop.type.PriceCost;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedList;
import java.util.List;

public class PotionShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public PotionShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.BREWING_STAND_ITEM).setLores("§e点击查看！").getItem(), "§a药水", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.POTION).setPotionData(new PotionEffect(PotionEffectType.SPEED, 600, 1)).setDisplayName("§b速度药水§7（30秒）").getItem(), "速度药水§7（30秒）", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 100)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.POTION).setPotionData(new PotionEffect(PotionEffectType.JUMP, 600, 1)).setDisplayName("跳跃药水§7（30秒）").getItem(), "跳跃药水§7（30秒）", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 100)));
        //shopItems.add(new ItemType(ItemUtil.makePotion(1, new PotionEffect(PotionEffectType.INVISIBILITY, 600, 1), "§b隐身药水§7（30秒）", null), "隐身药水§7（30秒）", ColorType.NONE, new PriceCost(Material.EMERALD, 2, 200)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
