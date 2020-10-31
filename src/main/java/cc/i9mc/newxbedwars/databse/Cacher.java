package cc.i9mc.newxbedwars.databse;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Cacher {
    @Getter
    private final ConcurrentHashMap<String, PlayerData> data;

    public Cacher() {
        data = new ConcurrentHashMap<>();
    }

    public void add(String name, PlayerData playerData) {
        data.put(name, playerData);
    }

    public PlayerData get(String name) {
        return data.get(name);
    }

    public void remove(String name) {
        data.remove(name);
    }

    public boolean contains(String name) {
        return data.containsKey(name);
    }

    public PlayerData contains(String name, PlayerData returnData) {
        return data.containsKey(name) ? get(name) : returnData;
    }

    public List<PlayerData> sortKills() {
        List<PlayerData> stats = new ArrayList<>(data.values());
        stats.sort((PlayerData stats1, PlayerData stats2) -> stats2.getKills() - stats1.getKills());

        return stats;
    }
}
