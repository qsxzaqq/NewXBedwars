package cc.i9mc.newxbedwars.game;

import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.nick.Nick;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import java.util.ArrayList;
import java.util.List;

@Data
public class Team {
    private final TeamColor teamColor;
    private final Location spawn;
    private final org.bukkit.scoreboard.Team team;
    private final Block bedFeet;
    private final Block bedHead;
    private final BlockFace bedFace;
    private boolean die;
    private String destroyPlayer;
    private boolean bedDestroy;
    private int forge;
    private int manicMiner;
    private boolean sharpenedSwords;
    private int reinforcedArmor;
    private boolean healPool;
    private boolean trap;
    private boolean miner;
    private boolean unbed;

    public Team(TeamColor teamColor, Location baseLocation) {
        this.die = false;
        this.sharpenedSwords = false;
        this.reinforcedArmor = 0;
        this.manicMiner = 0;
        this.miner = false;
        this.healPool = false;
        this.trap = false;
        this.unbed = false;
        this.spawn = baseLocation;
        this.teamColor = teamColor;
        team = Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam(String.valueOf(teamColor.getName()));

        List<Block> blocks = new ArrayList<>();

        for (int x = -18; x < 18; x++) {
            for (int y = -18; y < 18; y++) {
                for (int z = -18; z < 18; z++) {
                    Block block = spawn.clone().add(x, y, z).getBlock();

                    if (block != null && block.getType() == Material.BED_BLOCK) {
                        blocks.add(block);
                    }
                }
            }
        }

        Bed bedBlock = (Bed) blocks.get(0).getState().getData();

        if (!bedBlock.isHeadOfBed()) {
            bedFeet = blocks.get(0);
            bedHead = blocks.get(1);
        } else {
            bedHead = blocks.get(0);
            bedFeet = blocks.get(1);
        }

        bedFace = ((Bed) bedHead.getState().getData()).getFacing();
    }

    public void setBedDestroy(boolean bedDestroy) {
        this.bedDestroy = bedDestroy;
    }

    public ChatColor getChatColor() {
        return teamColor.getChatColor();
    }

    public DyeColor getDyeColor() {
        return teamColor.getDyeColor();
    }

    public Color getColor() {
        return teamColor.getColor();
    }

    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();

        for (String aPlayer : team.getEntries()) {
            Player player = Bukkit.getPlayerExact(aPlayer);
            if (player != null && !NewXBedwars.getInstance().getGame().getSpectatorManger().isSpectator(player)) {
                players.add(player);
            }
        }
        return players;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (String aPlayer : team.getEntries()) {
            Player player = Bukkit.getPlayerExact(aPlayer);
            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }


    public List<String> getPlayerNames() {
        return new ArrayList<>(team.getEntries());
    }


    public List<OfflinePlayer> getOfflinePlayers() {
        return new ArrayList<>(team.getPlayers());
    }

    public boolean isInTeam(String name) {
        return team.hasEntry(name);
    }

    public boolean isInTeam(Player player) {
        if (player == null) {
            return false;
        }
        return team.hasEntry(player.getName());
    }

    public boolean isInTeam(String name, String name1) {
        List<String> players = new ArrayList<>(team.getEntries());
        players.remove(name);
        return players.contains(name1);
    }


    public boolean addPlayer(Player player) {
        team.addEntry(player.getName());
        return true;
    }

    public void removePlayer(Player player) {
        if (team.hasEntry(player.getName())) {
            team.removeEntry(player.getName());
        }
    }

    public String getName() {
        return teamColor.getName();
    }

    public void setDestroyPlayer(Player player) {
        destroyPlayer = NewXBedwars.getInstance().getGame().getTeam(player).getChatColor() + Nick.get().getCache().getOrDefault(player.getName(), player.getName());
    }
}