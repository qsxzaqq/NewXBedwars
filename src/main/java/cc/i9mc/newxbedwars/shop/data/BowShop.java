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

public class BowShop implements ShopData {
    private final ItemType mainShopItem;
    private final List<ItemType> shopItems = new LinkedList<>();

    public BowShop() {
        mainShopItem = new ItemType(new ItemBuilderUtil().setType(Material.BOW).setLores("§e点击查看！").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "§a远程武器", ColorType.NONE, null);

        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.ARROW).setAmount(8).getItem(), "箭", ColorType.NONE, new PriceCost(Material.IRON_INGOT, 8, 10)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.BOW).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "弓", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 12, 60)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "弓（力量I）", ColorType.NONE, new PriceCost(Material.GOLD_INGOT, 24, 60)));
        shopItems.add(new ItemType(new ItemBuilderUtil().setType(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), "弓（力量I,冲击I）", ColorType.NONE, new PriceCost(Material.EMERALD, 6, 600)));
    }

    public ItemType getMainShopItem() {
        return mainShopItem;
    }

    public List<ItemType> getShopItems() {
        return shopItems;
    }
}
