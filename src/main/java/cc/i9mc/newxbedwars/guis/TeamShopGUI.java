package cc.i9mc.newxbedwars.guis;

import cc.i9mc.gameutils.gui.CustonGUI;
import cc.i9mc.gameutils.gui.GUIAction;
import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.game.Game;
import cc.i9mc.newxbedwars.game.Team;
import cc.i9mc.newxbedwars.types.ModeType;
import cc.i9mc.newxbedwars.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TeamShopGUI extends CustonGUI {
    public TeamShopGUI(Player player, Game game) {
        super(player, "§8团队升级", 45);
        ModeType modeType = NewXBedwars.getInstance().getCacher().get(player.getName()).getModeType();

        if (!game.getTeam(player).isSharpenedSwords()) {
            setItem(11, new ItemBuilderUtil().setType(Material.IRON_SWORD).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a磨刀石").setLores("§7你放所有成员的剑将获得锋利I附魔！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "4 钻石" : "400级")).getItem(), new GUIAction(0, () -> {
                Team team = game.getTeam(player);

                if (modeType == ModeType.DEFAULT) {
                    int k = 0;
                    int i1 = player.getInventory().getContents().length;
                    ItemStack[] itemStacks = player.getInventory().getContents();
                    for (int j1 = 0; j1 < i1; ++j1) {
                        ItemStack itemStack1 = itemStacks[j1];
                        if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND)) {
                            k += itemStack1.getAmount();
                        }
                    }

                    if (k >= 4) {
                        int amount = 4;
                        i1 = player.getInventory().getContents().length;
                        itemStacks = player.getInventory().getContents();
                        for (int j1 = 0; j1 < i1; ++j1) {
                            ItemStack itemStack1 = itemStacks[j1];
                            if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND) && amount > 0) {
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
                    if (player.getLevel() >= 400) {
                        player.setLevel(player.getLevel() - 400);
                        player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                    } else {
                        player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                        player.sendMessage("§c没有足够资源购买！");
                        return;
                    }
                }

                team.setSharpenedSwords(true);
                new TeamShopGUI(player, game).open();

                for (Player player1 : team.getPlayers()) {
                    if (game.getSpectatorManger().isSpectator(player1)) {
                        continue;
                    }

                    for (int i = 0; i < player1.getInventory().getContents().length; i++) {
                        ItemStack itemStack1 = player1.getInventory().getContents()[i];
                        if (itemStack1 != null && itemStack1.getType().toString().endsWith("_SWORD")) {
                            itemStack1.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                        }
                    }
                }
            }, false));
        } else {
            setItem(11, new ItemBuilderUtil().setType(Material.IRON_SWORD).addEnchant(Enchantment.DIG_SPEED, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a磨刀石").setLores(" ", "§c满级").getItem(), new GUIAction(0, () -> {
            }, false));
        }

        Runnable runnable = () -> {
            Team team = game.getTeam(player);
            int money = 0;
            switch (team.getReinforcedArmor()) {
                case 0:
                    money = 2;
                    break;
                case 1:
                    money = 4;
                    break;
                case 2:
                    money = 8;
                    break;
                default:
                    break;
            }

            if (modeType == ModeType.DEFAULT) {
                int k = 0;
                int i1 = player.getInventory().getContents().length;
                ItemStack[] itemStacks = player.getInventory().getContents();
                for (int j1 = 0; j1 < i1; ++j1) {
                    ItemStack itemStack1 = itemStacks[j1];
                    if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND)) {
                        k += itemStack1.getAmount();
                    }
                }

                if (k >= money) {
                    int amount = money;
                    i1 = player.getInventory().getContents().length;
                    itemStacks = player.getInventory().getContents();
                    for (int j1 = 0; j1 < i1; ++j1) {
                        ItemStack itemStack1 = itemStacks[j1];
                        if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND) && amount > 0) {
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
                if (player.getLevel() >= (money * 100)) {
                    player.setLevel(player.getLevel() - (money * 100));
                    player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                } else {
                    player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                    player.sendMessage("§c没有足够资源购买！");
                    return;
                }
            }

            team.setReinforcedArmor(team.getReinforcedArmor() + 1);
            new TeamShopGUI(player, game).open();

            for (Player player1 : team.getPlayers()) {
                if (game.getSpectatorManger().isSpectator(player1)) {
                    continue;
                }

                for (int i = 0; i < player1.getInventory().getArmorContents().length; i++) {
                    ItemStack itemStack1 = player1.getInventory().getArmorContents()[i];
                    itemStack1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, game.getTeam(player).getReinforcedArmor());
                }
            }
        };

        switch (game.getTeam(player).getReinforcedArmor()) {
            case 0:
                setItem(12, new ItemBuilderUtil().setType(Material.IRON_CHESTPLATE).setDisplayName("§a精制护甲").setLores("§7你放所有成员的盔甲将获得保护I附魔！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "2 钻石" : "200级")).getItem(), new GUIAction(0, runnable, false));
                break;
            case 1:
                setItem(12, new ItemBuilderUtil().setType(Material.IRON_CHESTPLATE).setDisplayName("§a精制护甲").setLores("§7你放所有成员的盔甲将获得保护II附魔！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "4 钻石" : "400级")).getItem(), new GUIAction(0, runnable, false));
                break;
            case 2:
                setItem(12, new ItemBuilderUtil().setType(Material.IRON_CHESTPLATE).setDisplayName("§a精制护甲").setLores("§7你放所有成员的盔甲将获得保护III附魔！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "8 钻石" : "800级")).getItem(), new GUIAction(0, runnable, false));
                break;
            case 3:
                setItem(12, new ItemBuilderUtil().setType(Material.IRON_CHESTPLATE).setDisplayName("§a精制护甲").setLores(" ", "§c满级").addEnchant(Enchantment.DIG_SPEED, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), new GUIAction(0, () -> {
                }, false));
                break;
            default:
                break;
        }

        Runnable runnable1 = () -> {
            Team team = game.getTeam(player);
            int money = 0;
            switch (team.getManicMiner()) {
                case 0:
                    money = 2;
                    break;
                case 1:
                    money = 4;
                    break;
                default:
                    break;
            }

            if (modeType == ModeType.DEFAULT) {
                int k = 0;
                int i1 = player.getInventory().getContents().length;
                ItemStack[] itemStacks = player.getInventory().getContents();
                for (int j1 = 0; j1 < i1; ++j1) {
                    ItemStack itemStack1 = itemStacks[j1];
                    if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND)) {
                        k += itemStack1.getAmount();
                    }
                }

                if (k >= money) {
                    int amount = money;
                    i1 = player.getInventory().getContents().length;
                    itemStacks = player.getInventory().getContents();
                    for (int j1 = 0; j1 < i1; ++j1) {
                        ItemStack itemStack1 = itemStacks[j1];
                        if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND) && amount > 0) {
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
                if (player.getLevel() >= (money * 100)) {
                    player.setLevel(player.getLevel() - (money * 100));
                    player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                } else {
                    player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                    player.sendMessage("§c没有足够资源购买！");
                    return;
                }
            }

            team.setManicMiner(team.getManicMiner() + 1);
            new TeamShopGUI(player, game).open();
        };

        switch (game.getTeam(player).getManicMiner()) {
            case 0:
                setItem(13, new ItemBuilderUtil().setType(Material.GOLD_PICKAXE).setDisplayName("§a疯狂矿工").setLores("§7你放所有成员将永久拥有急迫I效果！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "2 钻石" : "200级")).getItem(), new GUIAction(0, runnable1, false));
                break;
            case 1:
                setItem(13, new ItemBuilderUtil().setType(Material.GOLD_PICKAXE).setDisplayName("§a疯狂矿工").setLores("§7你放所有成员将永久拥有急迫II效果！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "4 钻石" : "400级")).getItem(), new GUIAction(0, runnable1, false));
                break;
            case 2:
                setItem(13, new ItemBuilderUtil().setType(Material.GOLD_PICKAXE).setDisplayName("§a疯狂矿工").setLores(" ", "§c满级").addEnchant(Enchantment.DIG_SPEED, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem(), new GUIAction(0, () -> {
                }, false));
                break;
            default:
                break;
        }

        if (!game.getTeam(player).isMiner()) {
            setItem(14, new ItemBuilderUtil().setType(Material.IRON_PICKAXE).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a挖掘疲劳陷阱").setLores("§7下一个进入基地的敌人,会获得储蓄10秒的挖掘疲劳效果！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "2 钻石" : "200级")).getItem(), new GUIAction(0, () -> {
                Team team = game.getTeam(player);

                if (modeType == ModeType.DEFAULT) {
                    int k = 0;
                    int i1 = player.getInventory().getContents().length;
                    ItemStack[] itemStacks = player.getInventory().getContents();
                    for (int j1 = 0; j1 < i1; ++j1) {
                        ItemStack itemStack1 = itemStacks[j1];
                        if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND)) {
                            k += itemStack1.getAmount();
                        }
                    }

                    if (k >= 2) {
                        int amount = 2;
                        i1 = player.getInventory().getContents().length;
                        itemStacks = player.getInventory().getContents();
                        for (int j1 = 0; j1 < i1; ++j1) {
                            ItemStack itemStack1 = itemStacks[j1];
                            if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND) && amount > 0) {
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
                    if (player.getLevel() >= 200) {
                        player.setLevel(player.getLevel() - 200);
                        player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                    } else {
                        player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                        player.sendMessage("§c没有足够资源购买！");
                        return;
                    }
                }

                team.setMiner(true);
                new TeamShopGUI(player, game).open();
            }, false));
        } else {
            setItem(14, new ItemBuilderUtil().setType(Material.IRON_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a挖掘疲劳陷阱").setLores(" ", "§c满级").getItem(), new GUIAction(0, () -> {
            }, false));
        }

        if (!game.getTeam(player).isHealPool()) {
            setItem(15, new ItemBuilderUtil().setType(Material.BEACON).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a治愈池").setLores("§7基地附近的队伍成员获得生命恢复效果", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "4 钻石" : "400级")).getItem(), new GUIAction(0, () -> {
                Team team = game.getTeam(player);

                if (modeType == ModeType.DEFAULT) {
                    int k = 0;
                    int i1 = player.getInventory().getContents().length;
                    ItemStack[] itemStacks = player.getInventory().getContents();
                    for (int j1 = 0; j1 < i1; ++j1) {
                        ItemStack itemStack1 = itemStacks[j1];
                        if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND)) {
                            k += itemStack1.getAmount();
                        }
                    }

                    if (k >= 4) {
                        int amount = 4;
                        i1 = player.getInventory().getContents().length;
                        itemStacks = player.getInventory().getContents();
                        for (int j1 = 0; j1 < i1; ++j1) {
                            ItemStack itemStack1 = itemStacks[j1];
                            if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND) && amount > 0) {
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
                    if (player.getLevel() >= 400) {
                        player.setLevel(player.getLevel() - 400);
                        player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                    } else {
                        player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                        player.sendMessage("§c没有足够资源购买！");
                        return;
                    }
                }

                team.setHealPool(true);
                new TeamShopGUI(player, game).open();
            }, false));
        } else {
            setItem(15, new ItemBuilderUtil().setType(Material.BEACON).addEnchant(Enchantment.DIG_SPEED, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a治愈池").setLores(" ", "§c满级").getItem(), new GUIAction(0, () -> {
            }, false));
        }

        if (!game.getTeam(player).isTrap()) {
            setItem(31, new ItemBuilderUtil().setType(Material.TRIPWIRE_HOOK).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a这是个陷阱！").setLores("§7下一个来犯之敌将获得失明和缓慢效果！", " ", "§7花费: §3§l" + (modeType == ModeType.DEFAULT ? "1 钻石" : "100级")).getItem(), new GUIAction(0, () -> {
                Team team = game.getTeam(player);

                if (modeType == ModeType.DEFAULT) {
                    int k = 0;
                    int i1 = player.getInventory().getContents().length;
                    ItemStack[] itemStacks = player.getInventory().getContents();
                    for (int j1 = 0; j1 < i1; ++j1) {
                        ItemStack itemStack1 = itemStacks[j1];
                        if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND)) {
                            k += itemStack1.getAmount();
                        }
                    }

                    if (k >= 1) {
                        int amount = 1;
                        i1 = player.getInventory().getContents().length;
                        itemStacks = player.getInventory().getContents();
                        for (int j1 = 0; j1 < i1; ++j1) {
                            ItemStack itemStack1 = itemStacks[j1];
                            if (itemStack1 != null && itemStack1.getType().equals(Material.DIAMOND) && amount > 0) {
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
                    if (player.getLevel() >= 100) {
                        player.setLevel(player.getLevel() - 100);
                        player.playSound(player.getLocation(), SoundUtil.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 1f, 1f);
                    } else {
                        player.playSound(player.getLocation(), SoundUtil.get("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 30.0F, 1.0F);
                        player.sendMessage("§c没有足够资源购买！");
                        return;
                    }
                }

                team.setTrap(true);
                new TeamShopGUI(player, game).open();
            }, false));
        } else {
            setItem(31, new ItemBuilderUtil().setType(Material.TRIPWIRE_HOOK).addEnchant(Enchantment.DIG_SPEED, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§a这是个陷阱！").setLores(" ", "§c满级").getItem(), new GUIAction(0, () -> {
            }, false));
        }

        for (int i = 18; i < 27; i++) {
            setItem(i, new ItemBuilderUtil().setType(Material.STAINED_GLASS_PANE).setDurability(7).getItem(), new GUIAction(0, () -> {
            }, false));
        }
    }
}
