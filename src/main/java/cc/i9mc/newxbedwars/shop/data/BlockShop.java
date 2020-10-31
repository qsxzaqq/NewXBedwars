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

public class BlockShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public BlockShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.HARD_CLAY).setLores("§e点击查看！").getItem(), "§a方块", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WOOL).setAmount(16).getItem(), "羊毛", ColorType.COLOR, new PriceCost(Material.IRON_INGOT, 4, 4)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WOOL).setAmount(4).addEnchant(Enchantment.DIG_SPEED, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).getItem(), "火速羊毛", ColorType.COLOR, new PriceCost(Material.IRON_INGOT, 4, 4)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.STAINED_CLAY).setAmount(16).getItem(), "硬化粘土", ColorType.COLOR, new PriceCost(Material.IRON_INGOT, 12, 12)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.STAINED_GLASS).setAmount(4).getItem(), "防爆玻璃", ColorType.COLOR, new PriceCost(Material.IRON_INGOT, 12, 12)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.ENDER_STONE).setAmount(12).getItem(), "末地石", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 24, 24)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.LADDER).setAmount(16).getItem(), "梯子", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 4, 4)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.SIGN).setAmount(8).getItem(), "告示牌", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 2, 2)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WOOD).setAmount(16).getItem(), "橡木板", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 4, 20)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.SLIME_BLOCK).setAmount(4).getItem(), "史莱姆块", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 2, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.WEB).setAmount(2).getItem(), "蜘蛛网", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 4, 20)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.OBSIDIAN).setAmount(4).getItem(), "黑曜石", ColorType.NONE, new PriceCost(Material.EMERALD, 4, 400)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
