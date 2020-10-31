package cc.i9mc.newxbedwars.specials;

import cc.i9mc.newxbedwars.NewXBedwars;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public abstract class SpecialItem {
    @Getter
    private static final List<Class<? extends SpecialItem>> availableSpecials = new ArrayList<>();

    public static void loadSpecials() {
        SpecialItem.availableSpecials.add(RescuePlatform.class);
        SpecialItem.availableSpecials.add(WarpPowder.class);
        NewXBedwars.getInstance().getServer().getPluginManager().registerEvents(new RescuePlatformListener(), NewXBedwars.getInstance());
        NewXBedwars.getInstance().getServer().getPluginManager().registerEvents(new WarpPowderListener(), NewXBedwars.getInstance());
    }

    public abstract Material getActivatedMaterial();

    public abstract Material getItemMaterial();

}
