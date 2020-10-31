package cc.i9mc.newxbedwars.databse;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.types.ModeType;
import lombok.Data;
import org.bukkit.scheduler.BukkitRunnable;

@Data
public class PlayerData {
    private String name;
    private ModeType modeType;
    private int kills;
    private int deaths;
    private int destroyedBeds;
    private int wins;
    private int loses;
    private int games;
    private String[] shopSort;

    private int oldKills;
    private int oldDestroyedBeds;
    private int oldWins;

    private boolean has;

    public PlayerData(String name, ModeType modeType, int oldKills, int oldDestroyedBeds, int oldWins) {
        this.name = name;
        this.modeType = modeType;

        this.oldKills = oldKills;
        this.oldDestroyedBeds = oldDestroyedBeds;
        this.oldWins = oldWins;

        this.has = true;

        asyncLoadShop();
    }

    public PlayerData(String name) {
        this.name = name;
        this.modeType = ModeType.DEFAULT;
        this.has = false;

        asyncLoadShop();
    }

    public void asyncLoadShop(){
        new BukkitRunnable() {
            @Override
            public void run() {
                String[] shopSort = Database.getPlayerShopSort(name);
                if(shopSort == null){
                    shopSort = PlayerData.this.shopSort = new String[]{
                            "BlockShop#1",
                            "SwordShop#1",
                            "ArmorShop#1",
                            "FoodShop#1",
                            "BowShop#2",
                            "PotionShop#1",
                            "UtilityShop#2",
                            "BlockShop#8",
                            "SwordShop#2",
                            "ArmorShop#2",
                            "UtilityShop#1",
                            "BowShop#1",
                            "PotionShop#2",
                            "UtilityShop#4",
                            "AIR", "AIR", "AIR", "AIR", "AIR", "AIR", "AIR"};
                    Database.savePlayerShopSort(name, false, shopSort);
                }else {
                    PlayerData.this.shopSort = shopSort;
                }
            }
        }.runTaskAsynchronously(NewXBedwars.getInstance());
    }
}