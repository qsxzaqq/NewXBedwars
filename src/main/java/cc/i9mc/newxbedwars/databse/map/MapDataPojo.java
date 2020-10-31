package cc.i9mc.newxbedwars.databse.map;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class MapDataPojo implements Serializable {
    private String author;
    private int teamMaxPlayers;
    private int minPlayers;
    private int range;
    private String reSpawnLocation;
    private String area1Location;
    private String area2Location;
    private List<String> baseLocations;
    private HashMap<String, String> dropLocations;
    private HashMap<String, Integer> shopLocations;
}
