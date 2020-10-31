package cc.i9mc.newxbedwars.shop.data;

import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.shop.ShopData;
import cc.i9mc.newxbedwars.shop.type.ColorType;
import cc.i9mc.newxbedwars.shop.type.ItemType;
import cc.i9mc.newxbedwars.shop.type.PriceCost;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedList;
import java.util.List;

public class DefaultShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public DefaultShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.NETHER_STAR).setLores("§e点击查看！").getItem(), "§b快速购买", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WOOL).setAmount(16).getItem(), "羊毛", ColorType.COLOR, new PriceCost(Material.IRON_INGOT, 4, 4)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.STONE_SWORD).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "石剑", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 10, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.CHAINMAIL_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "锁链装备§7（死亡不掉落）", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 40, 40)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.GOLDEN_APPLE).getItem(), "金苹果", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 3, 15)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.BOW).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "弓", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 12, 60)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.POTION).setPotionData(new PotionEffect(PotionEffectType.SPEED, 600, 1)).setDisplayName("§b速度药水§7（30秒）").getItem(), "速度药水§7（30秒）", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 100)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.TNT).getItem(), "TNT", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 4, 40)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WOOD).setAmount(16).getItem(), "橡木板", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 4, 20)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.IRON_SWORD).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "铁剑", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 7, 35)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.IRON_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "铁装备§7（死亡不掉落）", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 12, 60)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.FIREBALL).getItem(), "火球", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 50, 50)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.ARROW).setAmount(8).getItem(), "箭", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 8, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.POTION).setPotionData(new PotionEffect(PotionEffectType.JUMP, 600, 1)).setDisplayName("跳跃药水§7（30秒）").getItem(), "跳跃药水§7（30秒）", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 100)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WATER_BUCKET).getItem(), "水桶", ColorType.NONE, new PriceCost(Material.EMERALD, 1, 100)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
