package cc.i9mc.newxbedwars.shop;

import cc.i9mc.newxbedwars.shop.type.ItemType;

import java.util.List;

public interface ShopData {
    ItemType getMainShopItem();

    List<ItemType> getShopItems();
}
