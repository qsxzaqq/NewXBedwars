package cc.i9mc.newxbedwars.shop.type;

import org.bukkit.Material;

public class PriceCost {
    private final Material material;
    private final int amount;
    private final int xp;

    public PriceCost(Material material, int amount, int xp) {
        this.material = material;
        this.amount = amount;
        this.xp = xp;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public int getXp() {
        return xp;
    }
}
