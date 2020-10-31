package cc.i9mc.newxbedwars.guis;

import cc.i9mc.gameutils.gui.CustonGUI;
import cc.i9mc.gameutils.gui.GUIAction;
import cc.i9mc.gameutils.gui.GUIActionRunnable;
import cc.i9mc.gameutils.gui.NewGUIAction;
import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.Database;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.shop.ItemShopManager;
import cc.i9mc.newxbedwars.shop.ShopData;
import cc.i9mc.newxbedwars.shop.data.DefaultShop;
import cc.i9mc.newxbedwars.shop.type.ColorType;
import cc.i9mc.newxbedwars.shop.type.ItemType;
import cc.i9mc.newxbedwars.shop.type.PriceCost;
import cc.i9mc.newxbedwars.types.ArmorType;
import cc.i9mc.newxbedwars.types.ModeType;
import cc.i9mc.newxbedwars.types.ToolType;
import cc.i9mc.newxbedwars.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemShopGUI extends CustonGUI {
    private final Integer[] slots = new Integer[]{19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

    public ItemShopGUI(Player player, int slot, Game game) {
        super(player, "§8道具商店 - " + ChatColor.stripColor(ItemShopManager.getShops().get(slot).getMainShopItem().getDisplayName()), 54);
        PlayerData playerData = NewXBedwars.getInstance().getCacher().get(player.getName());

        int i = 0;
        for (ShopData shopData : ItemShopManager.getShops()) {
            if (i > 9) {
                continue;
            }

            int finalI = i;
            setItem(i, new ItemBuilderUtil().setItemStack(shopData.getMainShopItem().getItemStack().clone()).setDisplayName(shopData.getMainShopItem().getDisplayName()).getItem(), new GUIAction(0, () -> {
                if (finalI != slot) new ItemShopGUI(player, finalI, game).open();
            }, false));
            ++i;
        }

        for (int i1 = 9; i1 < 18; i1++) {
            if (i1 == (slot + 9)) {
                setItem(i1, new ItemBuilderUtil().setType(Material.STAINED_GLASS_PANE).setDurability(5).getItem(), new GUIAction(0, () -> {
                }, false));
                continue;
            }

            setItem(i1, new ItemBuilderUtil().setType(Material.STAINED_GLASS_PANE).setDurability(7).getItem(), new GUIAction(0, () -> {
            }, false));
        }

        int j = -1;
        ShopData shopData = ItemShopManager.getShops().get(slot);
        if (shopData instanceof DefaultShop) {
            for (String s : playerData.getShopSort()) {
                j++;
                String[] strings = !s.equals("AIR") ? s.split("#") : null;

                ItemType itemType = null;
                if (strings != null && strings.length == 2) {
                    for (ShopData shopData1 : ItemShopManager.getShops()) {
                        if (shopData1.getClass().getSimpleName().equals(strings[0])) {
                            itemType = shopData1.getShopItems().get(Integer.parseInt(strings[1]) - 1);
                        }
                    }
                }

                if (strings == null || itemType == null) {
                    setItem(slots[j], new ItemBuilderUtil().setType(Material.STAINED_GLASS_PANE).setDurability(14).setDisplayName("§c空闲的槽位").setLores("§7这是一个快捷购买槽位!§bShift+左键", "§7将任意物品放到这里~").getItem(), new NewGUIAction(0, event -> {
                        if (!event.isShiftClick()) return;

                        player.sendMessage("§c这是个空的槽位!请使用Shift+左键添加物品到这里~");
                    }, false));
                    continue;
                }

                setItem(player, slot, slots[j], game, playerData, itemType, -1, Arrays.asList("§7Shift+左键从快捷购买中移除", " "));
            }
            return;
        }

        for (ItemType itemType : shopData.getShopItems()) {
            j++;
            setItem(player, slot, slots[j], game, playerData, itemType, j, null);
        }
    }

    public void setItem(Player player, int slot, int size, Game game, PlayerData playerData, ItemType itemType, int itemSlot, List<String> moreLore) {
        ModeType modeType = playerData.getModeType();

        ItemBuilderUtil itemBuilderUtil = new ItemBuilderUtil();
        itemBuilderUtil.setItemStack(itemType.getItemStack().clone());
        if (itemType.getColorType() == ColorType.PICKAXE) {
            if (game.getPickAxe().containsKey(player.getUniqueId())) {
                switch (game.getPickAxe().get(player.getUniqueId())) {
                    case STONE:
                        itemBuilderUtil.setType(Material.STONE_PICKAXE);
                        itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 20, 20));
                        break;
                    case IRON:
                        itemBuilderUtil.setType(Material.IRON_PICKAXE);
                        itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 8, 24));
                        break;
                    case DIAMOND:
                    case MAX:
                        itemBuilderUtil.setType(Material.DIAMOND_PICKAXE);
                        itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 12, 36));
                        break;
                    default:
                        itemBuilderUtil.setType(Material.WOOD_PICKAXE);
                        itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 10, 10));
                        break;
                }
            } else {
                itemBuilderUtil.setType(Material.WOOD_PICKAXE);
                itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 10, 10));
            }
        } else if (itemType.getColorType() == ColorType.AXE) {
            if (game.getAxe().containsKey(player.getUniqueId())) {
                switch (game.getAxe().get(player.getUniqueId())) {
                    case STONE:
                        itemBuilderUtil.setType(Material.STONE_AXE);
                        itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 20, 20));
                        break;
                    case IRON:
                        itemBuilderUtil.setType(Material.IRON_AXE);
                        itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 8, 24));
                        break;
                    case DIAMOND:
                    case MAX:
                        itemBuilderUtil.setType(Material.DIAMOND_AXE);
                        itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 12, 36));
                        break;
                    default:
                        itemBuilderUtil.setType(Material.WOOD_AXE);
                        itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 10, 10));
                        break;
                }
            } else {
                itemBuilderUtil.setType(Material.WOOD_AXE);
                itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 10, 10));
            }
        }

        List<String> lore = new ArrayList<>();
        lore.add("§7物品:");
        lore.add("§8•" + itemType.getDisplayName());
        lore.add(" ");
        if (moreLore != null && !moreLore.isEmpty()) lore.addAll(moreLore);
        lore.add(modeType == ModeType.EXPERIENCE ? "§7花费: §3§l" + itemType.getPriceCost().getXp() + "级" : "§7花费: §3§l" + itemType.getPriceCost().getAmount() + " " + (itemType.getPriceCost().getMaterial() == Material.IRON_INGOT ? "铁" : (itemType.getPriceCost().getMaterial() == Material.GOLD_INGOT ? "金" : (itemType.getPriceCost().getMaterial() == Material.EMERALD ? "绿宝石" : ""))));

        super.setItem(size, itemBuilderUtil.setDisplayName("§c" + itemType.getDisplayName()).setLores(lore).getItem(), new NewGUIAction(0, event -> {
            if (event.isShiftClick()) {
                if (slot == 0) {
                    int slot1 = Arrays.asList(slots).indexOf(size);
                    if (slot1 == -1) return;

                    playerData.getShopSort()[slot1] = "AIR";
                    Bukkit.getScheduler().runTaskAsynchronously(NewXBedwars.getInstance(), () -> Database.savePlayerShopSort(player.getName(), true, playerData.getShopSort()));
                    new ItemShopGUI(player, slot, game).open();
                    return;
                }

                new DIYShopGUI(game, player, playerData, itemBuilderUtil.getItem().clone(), ItemShopManager.getShops().get(slot).getClass().getSimpleName() + "#" + (itemSlot + 1)).open();
                return;
            }

            if (itemType.getColorType() == ColorType.PICKAXE && game.getPickAxe().getOrDefault(player.getUniqueId(), ToolType.WOOD) == ToolType.MAX) {
                return;
            }

            if (itemType.getColorType() == ColorType.AXE && game.getAxe().getOrDefault(player.getUniqueId(), ToolType.WOOD) == ToolType.MAX) {
                return;
            }

            if (itemBuilderUtil.getItem().getType() == Material.SHEARS && game.getShears().contains(player.getUniqueId())) {
                return;
            }

            if (modeType == ModeType.DEFAULT) {
                int k = 0;
                int i1 = player.getInventory().getContents().length;
                ItemStack[] itemStacks = player.getInventory().getContents();
                for (int j1 = 0; j1 < i1; ++j1) {
                    ItemStack itemStack1 = itemStacks[j1];
                    if (itemStack1 != null && itemStack1.getType().equals(itemType.getPriceCost().getMaterial())) {
                        k += itemStack1.getAmount();
                    }
                }

                if (k >= itemType.getPriceCost().getAmount()) {
                    int amount = itemType.getPriceCost().getAmount();
                    i1 = player.getInventory().getContents().length;
                    itemStacks = player.getInventory().getContents();
                    for (int j1 = 0; j1 < i1; ++j1) {
                        ItemStack itemStack1 = itemStacks[j1];
                        if (itemStack1 != null && itemStack1.getType().equals(itemType.getPriceCost().getMaterial()) && amount > 0) {
                            if (itemStack1.getAmount() >= amount) {
                                itemStack1.setAmount(itemStack1.getAmount() - amount);
                                amount = 0;
                            } else if (itemStack1.getAmount() < amount) {
                                amount -= itemStack1.getAmount();
                                itemStack1.setAmount(0);
                            }

                            player.getInventory().setItem(j1, itemStack1);
                        }
                    }

                    player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                } else {
                    player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                    player.sendMessage("§c没有足够资源购买！");
                    return;
                }
            } else {
                if (player.getLevel() >= itemType.getPriceCost().getXp()) {
                    player.setLevel(player.getLevel() - itemType.getPriceCost().getXp());
                    player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                } else {
                    player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                    player.sendMessage("§c没有足够资源购买！");
                    return;
                }
            }

            switch (itemBuilderUtil.getItem().getType()) {
                case CHAINMAIL_BOOTS:
                    game.getArmor().put(player.getUniqueId(), ArmorType.CHAINMAIL);
                    game.giveInventory(player, 1, false);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case IRON_BOOTS:
                    game.getArmor().put(player.getUniqueId(), ArmorType.IRON);
                    game.giveInventory(player, 1, false);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case DIAMOND_BOOTS:
                    game.getArmor().put(player.getUniqueId(), ArmorType.DIAMOND);
                    game.giveInventory(player, 1, false);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case WOOD_PICKAXE:
                    game.getPickAxe().put(player.getUniqueId(), ToolType.STONE);
                    game.giveInventory(player, 2, false);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case STONE_PICKAXE:
                    game.getPickAxe().put(player.getUniqueId(), ToolType.IRON);
                    game.giveInventory(player, 2, true);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case IRON_PICKAXE:
                    game.getPickAxe().put(player.getUniqueId(), ToolType.DIAMOND);
                    game.giveInventory(player, 2, true);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case DIAMOND_PICKAXE:
                    game.getPickAxe().put(player.getUniqueId(), ToolType.MAX);
                    game.giveInventory(player, 2, true);
                    new ItemShopGUI(player, slot, game).open();
                    return;

                case WOOD_AXE:
                    game.getAxe().put(player.getUniqueId(), ToolType.STONE);
                    game.giveInventory(player, 3, false);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case STONE_AXE:
                    game.getAxe().put(player.getUniqueId(), ToolType.IRON);
                    game.giveInventory(player, 3, true);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case IRON_AXE:
                    game.getAxe().put(player.getUniqueId(), ToolType.DIAMOND);
                    game.giveInventory(player, 3, true);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                case DIAMOND_AXE:
                    game.getAxe().put(player.getUniqueId(), ToolType.MAX);
                    game.giveInventory(player, 3, true);
                    new ItemShopGUI(player, slot, game).open();
                    return;

                case SHEARS:
                    game.getShears().add(player.getUniqueId());
                    game.giveInventory(player, 4, false);
                    new ItemShopGUI(player, slot, game).open();
                    return;
                default:
                    break;
            }

            ItemBuilderUtil itemBuilderUtil1 = new ItemBuilderUtil().setItemStack(itemType.getItemStack().clone());
            if (itemType.getItemStack().getType().toString().endsWith("_SWORD")) {
                player.getInventory().remove(Material.WOOD_SWORD);

                if (game.getTeam(player).isSharpenedSwords())
                    itemBuilderUtil1.addEnchant(Enchantment.DAMAGE_ALL, 1);
            }

            if (itemType.getColorType() == ColorType.COLOR) {
                itemBuilderUtil1.setDurability(game.getTeam(player).getDyeColor().getWoolData());
            }

            player.getInventory().addItem(itemBuilderUtil1.getItem());
        }, false));
    }
}
